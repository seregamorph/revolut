package com.revolut.sinap.payment;

import com.revolut.sinap.api.ResponseCode;
import com.revolut.sinap.payment.domain.Payment;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.testng.Assert.assertEquals;

public class DummyAccountStorageTest {
    @Test
    public void test() {
        ConcurrentMap<Long, DummyAccountStorage.Account> accounts = new ConcurrentHashMap<>();
        accounts.put(1L, new DummyAccountStorage.Account(1, Currency.RUB, 0, 10000));
        accounts.put(2L, new DummyAccountStorage.Account(2, Currency.RUB, 0, 0));

        DummyAccountStorage storage = new DummyAccountStorage(accounts);

        Payment payment1 = newPayment(1, 2, 4000, Currency.RUB, "test1");

        assertEquals(storage.processPayment(payment1), ResponseCode.SUCCESS);
        assertEquals(storage.processPayment(payment1), ResponseCode.DUPLICATE_SUCCESS);

        Payment payment2 = newPayment(1, 2, 8000, Currency.RUB, "test2");
        assertEquals(storage.processPayment(payment2), ResponseCode.NO_MONEY);
        assertEquals(storage.processPayment(payment2), ResponseCode.NO_MONEY);

    }

    Payment newPayment(long sourceAccountId, long targetAccountId, long amount, Currency currency, String comment) {
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