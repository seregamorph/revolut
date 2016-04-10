package com.revolut.sinap.payment;

import java.util.UUID;

/**
 * Just to restrict, what methods can use PaymentService, e.g. it cannot change payment parameters.
 */
public interface PaymentServiceOperation {
    UUID transactionId();

    long getSourceAccountId();

    Currency getSourceCurrency();

    long getSourceAmount();

    long getTargetAccountId();

    Currency getTargetCurrency();

    long getTargetAmount();

    String getComment();
}
