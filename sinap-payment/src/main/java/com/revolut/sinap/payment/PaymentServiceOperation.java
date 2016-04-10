package com.revolut.sinap.payment;

import java.util.UUID;

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
