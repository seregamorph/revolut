package com.revolut.sinap.payment;

import com.revolut.sinap.api.ResponseCode;

import java.util.UUID;

public class DummyPaymentService implements PaymentService {
    @Override
    public ResponseCode processPayment(PaymentServiceOperation payment) {
        return ResponseCode.SUCCESS;
    }

    @Override
    public ResponseCode processPaymentStatus(UUID transactionId) {
        return ResponseCode.SUCCESS;
    }
}
