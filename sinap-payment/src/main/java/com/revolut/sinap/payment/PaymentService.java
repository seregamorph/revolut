package com.revolut.sinap.payment;

import com.revolut.sinap.api.ResponseCode;

public interface PaymentService {
    ResponseCode processPayment(PaymentServiceOperation payment);

    ResponseCode processPaymentStatus(long transactionId);
}
