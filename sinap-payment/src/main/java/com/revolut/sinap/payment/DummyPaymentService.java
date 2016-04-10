package com.revolut.sinap.payment;

import com.revolut.sinap.api.ResponseCode;

import java.util.Objects;
import java.util.UUID;

public class DummyPaymentService implements PaymentService {
    private final DummyAccountStorage accountStorage;

    public DummyPaymentService(DummyAccountStorage accountStorage) {
        this.accountStorage = Objects.requireNonNull(accountStorage, "accountStorage");
    }

    @Override
    public ResponseCode processPayment(PaymentServiceOperation payment) {
        return accountStorage.processPayment(payment);
    }

    @Override
    public ResponseCode processPaymentStatus(UUID transactionId) {
        return accountStorage.getPaymentStatus(transactionId);
    }
}
