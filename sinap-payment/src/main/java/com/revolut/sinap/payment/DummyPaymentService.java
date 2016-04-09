package com.revolut.sinap.payment;

import com.revolut.sinap.api.ResponseCode;

public class DummyPaymentService implements PaymentService {
    @Override
    public ResponseCode processPayment(PaymentServiceOperation payment) {
        return ResponseCode.SUCCESS;
    }
}
