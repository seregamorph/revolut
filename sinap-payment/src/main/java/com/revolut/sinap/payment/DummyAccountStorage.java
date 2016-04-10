package com.revolut.sinap.payment;

import com.revolut.sinap.api.ResponseCode;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory thread safe account storage implementation
 */
public class DummyAccountStorage {
    private final ConcurrentMap<UUID, TransactionReference> transactions = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Account> accounts;

    public DummyAccountStorage(ConcurrentMap<Long, Account> accounts) {
        this.accounts = accounts;
    }

    public ResponseCode processPayment(PaymentServiceOperation payment) {
        UUID transactionId = payment.transactionId();
        long sourceAccountId = payment.getSourceAccountId();
        long sourceAmount = payment.getSourceAmount();
        Currency sourceCurrency = payment.getSourceCurrency();
        long targetAccountId = payment.getTargetAccountId();
        Currency targetCurrency = payment.getTargetCurrency();
        long targetAmount = payment.getTargetAmount();

        checkAccounts(sourceAccountId, targetAccountId);
        checkAmount(sourceAmount, sourceCurrency, targetAmount, targetCurrency);

        // explaining what is going on here
        // we create a TransactionReference object for transactionId key with currentThread reference
        // then we take a synchronized lock on the object (created in spinlock with computeIfAbsent)
        // in the concurrent world in a race another thread can get ahead of getting lock.
        // We check thread, if the same, process transaction
        // if not the same, wait for result
        // if null, require result immediately (null thread can be only for complete transaction)
        // set the thread reference to null due to GC issues
        // after creation "thread" reference can be changed only in synchronized block,
        // but there is not sync between create + put and get from map (at least, I'm not 100% sure about happens-before
        // in this case), so there is a synchronized block in constructor.
        // non-null "thread" reference is a sign, that transaction processing is in progress.
        // the idea is to reduce inter-thread contention and do not make a global lock
        Thread currentThread = Thread.currentThread();
        do {
            TransactionReference ref = transactions.computeIfAbsent(transactionId, k -> new TransactionReference(currentThread));
            boolean evictRef = false;
            try {
                synchronized (ref) {
                    // operation is created and going to be processed by another thread, let it get in to complete (we wait)
                    while (ref.thread != null && ref.thread != currentThread) {
                        assert ref.state == TransactionState.INITIAL;
                        assert ref.transaction == null;
                        try {
                            ref.wait(1000L);
                        } catch (InterruptedException e) {
                            throw new RuntimeException("interrupted");
                        }
                        // todo sanity check not to lock inhere forever if something went wrong
                    }

                    // operation is complete by another thread
                    if (ref.thread == null) {
                        if (ref.state == TransactionState.ROLLED_BACK) {
                            // no 'dirty read', try one more time
                            // but before, help other thread to remove rolled back TransactionReference
                            // just to prevent unpredictable spinlock
                            evictRef = true;
                            continue;
                        }
                        return requireCommittedResponseCode(ref);
                    }

                    assert ref.thread == currentThread;

                    Account sourceAccount = accounts.get(sourceAccountId);
                    if (sourceAccount == null || sourceAccount.currency != sourceCurrency) {
                        return ResponseCode.BAD_SOURCE_ACCOUNT;
                    }
                    Account targetAccount = accounts.get(targetAccountId);
                    if (targetAccount == null || targetAccount.currency != targetCurrency) {
                        return ResponseCode.BAD_TARGET_ACCOUNT;
                    }
                    // todo check locked, limits, etc.

                    // get locks always in the same order: transactionId, accountId(less), accountId(more)
                    // expecting never get deadlock here
                    Account firstLockAccount;
                    Account secondLockAccount;
                    if (sourceAccountId < targetAccountId) {
                        firstLockAccount = sourceAccount;
                        secondLockAccount = targetAccount;
                    } else {
                        assert sourceAccountId > targetAccountId;
                        firstLockAccount = targetAccount;
                        secondLockAccount = sourceAccount;
                    }

                    synchronized (firstLockAccount) {
                        synchronized (secondLockAccount) {
                            if (!sourceAccount.ensureAvailableToWithdraw(sourceAmount)) {
                                ref.rollback();
                                return ResponseCode.NO_MONEY;
                            }

                            Transaction transaction = new Transaction(transactionId, ResponseCode.SUCCESS,
                                    sourceAccountId, sourceAmount, sourceCurrency,
                                    targetAccountId, targetAmount, targetCurrency,
                                    payment.getComment());

                            // all checks made, now the balance changing logic
                            sourceAccount.enroll(-sourceAmount);
                            targetAccount.enroll(targetAmount);

                            ref.commit(transaction);
                        }
                    }

                    assert ref.state == TransactionState.COMMITED;
                    assert ref.transaction != null;
                    // wake up awaiting threads
                    ref.notifyAll();
                    return ResponseCode.SUCCESS;
                }
            } finally {
                if (evictRef) {
                    transactions.remove(transactionId, ref);
                }
            }
        } while (true);
    }

    private static ResponseCode requireCommittedResponseCode(TransactionReference ref) {
        assert Thread.holdsLock(ref);

        TransactionState state = ref.state;
        if (state != TransactionState.COMMITED) {
            throw new RuntimeException("Only committed state is expected " + state);
        }

        Transaction transaction = ref.transaction;
        ResponseCode responseCode = transaction.responseCode;
        if (responseCode == ResponseCode.SUCCESS) {
            return ResponseCode.DUPLICATE_SUCCESS;
        } else {
            return responseCode;
        }
    }

    private static void checkAccounts(long sourceAccountId, long targetAccountId) {
        if (sourceAccountId <= 0L) {
            throw new IllegalArgumentException("Illegal sourceAccountId " + sourceAccountId);
        }
        if (targetAccountId <= 0L) {
            throw new IllegalArgumentException("Illegal targetAccountId " + targetAccountId);
        }
        if (sourceAccountId == targetAccountId) {
            throw new IllegalArgumentException("Transactions on the same accounts are forbidden " + sourceAccountId);
        }
    }

    private static void checkAmount(long sourceAmount, Currency sourceCurrency, long targetAmount, Currency targetCurrency) {
        if (sourceCurrency == null || targetCurrency == null) {
            throw new IllegalArgumentException("Currency not set");
        }
        if (sourceCurrency != targetCurrency) {
            throw new IllegalArgumentException("Cross-currency payments are forbidden " + sourceCurrency + " " + targetCurrency);
        }
        if (sourceAmount <= 0 || targetAmount <= 0) {
            throw new IllegalArgumentException("Illegal amounts " + sourceAmount + " " + targetAmount);
        }
        if (sourceAmount != targetAmount) {
            throw new IllegalArgumentException("Source and Target amount differs. " + sourceAmount + " " + targetAmount);
        }
    }

    public ResponseCode getPaymentStatus(UUID transactionId) {
        TransactionReference ref = transactions.get(transactionId);
        if (ref == null) {
            return ResponseCode.NOT_FOUND;
        }
        synchronized (ref) {
            if (ref.state == TransactionState.COMMITED) {
                // only success
                return ref.transaction.responseCode;
            }
        }
        // for all other cases - NOT found
        return ResponseCode.NOT_FOUND;
    }

    /**
     * Mutable transaction holder.
     * Synchronization monitor.
     * <p/>
     * State one of three
     * 1) thread != null && state == INITIAL && transaction == null
     * 2) thread == null && state = COMMITTED && transaction != null
     * 3) thread == null && state = ROLLED_BACK && transaction == null
     */
    private static class TransactionReference {
        private Thread thread;
        private TransactionState state;
        private Transaction transaction;

        TransactionReference(Thread thread) {
            synchronized (this) {
                this.thread = thread;
                this.state = TransactionState.INITIAL;
            }
        }

        private void commit(Transaction transaction) {
            assert Thread.holdsLock(this);

            assert this.thread == Thread.currentThread();
            assert this.state == TransactionState.INITIAL;
            assert this.transaction == null;

            this.thread = null;
            this.state = TransactionState.COMMITED;
            this.transaction = transaction;
        }

        private void rollback() {
            assert Thread.holdsLock(this);

            assert this.thread != null;
            assert this.state == TransactionState.INITIAL;
            assert this.transaction == null;

            this.thread = null;
            this.state = TransactionState.ROLLED_BACK;
        }

    }

    private enum TransactionState {
        INITIAL,
        COMMITED,
        ROLLED_BACK
    }

    private static class Transaction {
        private final UUID transactionId;
        /**
         * for now, it's always success. Reserved for two-phase payments.
         */
        private final ResponseCode responseCode;
        private final long sourceAccountId;
        /**
         * minor units
         */
        private final long sourceAmount;
        private final Currency sourceCurrency;

        private final long targetAccountId;
        /**
         * minor units
         */
        private final long targetAmount;
        private final Currency targetCurrency;

        private final String comment;


        /**
         * too many parameters, I don't like it too.
         * Make a discount, that this is a private api. Also, the object is immutable.
         *
         * @param transactionId
         * @param sourceAccountId
         * @param sourceAmount
         * @param sourceCurrency
         * @param targetAccountId
         * @param targetAmount
         * @param targetCurrency
         * @param comment
         */
        private Transaction(UUID transactionId, ResponseCode responseCode,
                            long sourceAccountId, long sourceAmount, Currency sourceCurrency,
                            long targetAccountId, long targetAmount, Currency targetCurrency,
                            String comment) {
            this.transactionId = transactionId;
            this.responseCode = responseCode;
            this.sourceAccountId = sourceAccountId;
            this.sourceAmount = sourceAmount;
            this.sourceCurrency = sourceCurrency;
            this.targetAccountId = targetAccountId;
            this.targetAmount = targetAmount;
            this.targetCurrency = targetCurrency;
            this.comment = comment;
        }
    }

    public static class Account {
        private final long accountId;
        private final Currency currency;
        private final long lowerLimit;

        private long balance;

        public Account(long accountId, Currency currency, long lowerLimit, long balance) {
            if (balance < lowerLimit) {
                throw new IllegalArgumentException("Balance is less than lowerLimit " + balance + " " + lowerLimit);
            }
            this.accountId = accountId;
            this.currency = currency;
            this.lowerLimit = lowerLimit;
            // instances of objects are visible thru ConcurrentHashMap after creation
            synchronized (this) {
                this.balance = balance;
            }
        }

        public long accountId() {
            return accountId;
        }

        private boolean ensureAvailableToWithdraw(long amount) {
            assert Thread.holdsLock(this);
            return balance - amount >= lowerLimit;
        }

        /**
         * @param amount positive: enroll, negative - withdraw
         */
        private void enroll(long amount) {
            assert Thread.holdsLock(this);
            long newBalance = this.balance + amount;
            assert newBalance >= lowerLimit;
            this.balance = newBalance;
        }

        public synchronized long getBalance() {
            return balance;
        }

        @Override
        public String toString() {
            return "Account{" +
                    "accountId=" + accountId +
                    ", lowerLimit=" + lowerLimit +
                    ", balance=" + balance +
                    '}';
        }
    }
}
