package com.revolut.sinap.payment;

import com.revolut.sinap.api.ResponseCode;

import java.util.UUID;

public interface PaymentService {
    ResponseCode processPayment(PaymentServiceOperation payment);

    ResponseCode processPaymentStatus(UUID transactionId);
}
