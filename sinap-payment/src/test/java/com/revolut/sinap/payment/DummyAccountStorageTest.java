package com.revolut.sinap.payment;

import com.revolut.sinap.api.ResponseCode;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.testng.Assert.assertEquals;

public class DummyAccountStorageTest {
    @Test
    public void test1() {
        ConcurrentMap<Long, DummyAccountStorage.Account> accounts = new ConcurrentHashMap<>();
        accounts.put(1L, new DummyAccountStorage.Account(1, Currency.RUB, 0, 10000));
        accounts.put(2L, new DummyAccountStorage.Account(2, Currency.RUB, 0, 0));

        DummyAccountStorage storage = new DummyAccountStorage(accounts);

        Payment payment1 = newPayment(1, 2, 4000, Currency.RUB, "test1");

        assertEquals(storage.processPayment(payment1), ResponseCode.SUCCESS);
        assertEquals(storage.getPaymentStatus(payment1.transactionId()), ResponseCode.SUCCESS);
        assertEquals(storage.processPayment(payment1), ResponseCode.DUPLICATE_SUCCESS);
        assertEquals(storage.getPaymentStatus(payment1.transactionId()), ResponseCode.SUCCESS);

        Payment payment2 = newPayment(1, 2, 8000, Currency.RUB, "test2");
        assertEquals(storage.processPayment(payment2), ResponseCode.NO_MONEY);
        assertEquals(storage.processPayment(payment2), ResponseCode.NO_MONEY);
        assertEquals(storage.getPaymentStatus(payment2.transactionId()), ResponseCode.NOT_FOUND);
    }

    private static final int ACCOUNTS = 100;
    private static final int TRANSACTIONS = 1000000;

    @Test
    public void testHeavySingleThread() {
        long balance = TRANSACTIONS / ACCOUNTS;
        Map<Long, DummyAccountStorage.Account> accounts = LongStream.range(1, ACCOUNTS * 2 + 1)
                .mapToObj(accountId -> new DummyAccountStorage.Account(accountId, Currency.RUB, 0,
                        accountId <= ACCOUNTS ? balance : 0))
                .collect(Collectors.toMap(DummyAccountStorage.Account::accountId, e -> e));

        DummyAccountStorage storage = new DummyAccountStorage(new ConcurrentHashMap<>(accounts));

        for (int i = 0; i < TRANSACTIONS; i++) {
            long sourceAccountId = i % ACCOUNTS + 1;
            long targetAccountId = ACCOUNTS + i % ACCOUNTS + 1;
            Payment payment = newPayment(sourceAccountId, targetAccountId, 1, Currency.RUB, "test");
            assertEquals(storage.processPayment(payment), ResponseCode.SUCCESS);
        }

        for (int i = 0; i < ACCOUNTS; i++) {
            assertEquals(accounts.get(Long.valueOf(i + 1)).getBalance(), 0L);
            assertEquals(accounts.get(Long.valueOf(ACCOUNTS + i + 1)).getBalance(), balance);
        }
    }

    @Test(dependsOnMethods = {"test1", "testHeavySingleThread"})
    public void testHeavyMultiThread() {
        long balance = TRANSACTIONS / ACCOUNTS;
        Map<Long, DummyAccountStorage.Account> accounts = LongStream.range(1, ACCOUNTS * 2 + 1)
                .mapToObj(accountId -> new DummyAccountStorage.Account(accountId, Currency.RUB, 0,
                        accountId <= ACCOUNTS ? balance : 0))
                .collect(Collectors.toMap(DummyAccountStorage.Account::accountId, e -> e));

        DummyAccountStorage storage = new DummyAccountStorage(new ConcurrentHashMap<>(accounts));

        List<Payment> payments = new ArrayList<>(TRANSACTIONS);
        for (int i = 0; i < TRANSACTIONS; i++) {
            long sourceAccountId = i % ACCOUNTS + 1;
            long targetAccountId = ACCOUNTS + i % ACCOUNTS + 1;
            Payment payment = newPayment(sourceAccountId, targetAccountId, 1, Currency.RUB, "test");
            payments.add(payment);
        }

        // to increase contention
        Collections.shuffle(payments);

        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<ResponseCode>> futures = payments.stream()
                    .map(payment -> executor.submit(() -> storage.processPayment(payment)))
                    .collect(Collectors.toList());
            futures.forEach(future -> {
                try {
                    assertEquals(future.get(), ResponseCode.SUCCESS);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            executor.shutdown();
        }

        for (int i = 0; i < ACCOUNTS; i++) {
            assertEquals(accounts.get(Long.valueOf(i + 1)).getBalance(), 0L);
            assertEquals(accounts.get(Long.valueOf(ACCOUNTS + i + 1)).getBalance(), balance);
        }
    }

    private static Payment newPayment(long sourceAccountId, long targetAccountId, long amount, Currency currency, String comment) {
        return new Payment(UUID.randomUUID())
                .setSourceAccountId(sourceAccountId)
                .setSourceCurrency(currency)
                .setSourceAmount(amount)
                .setTargetAccountId(targetAccountId)
                .setTargetCurrency(currency)
                .setTargetAmount(amount)
                .setComment(comment);
    }
}
