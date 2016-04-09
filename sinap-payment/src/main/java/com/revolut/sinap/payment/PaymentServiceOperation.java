package com.revolut.sinap.payment;

public interface PaymentServiceOperation {
    long transactionId();

    long getSourceAccountId();

    Currency getSourceCurrency();

    long getSourceAmount();

    long getTargetAccountId();

    Currency getTargetCurrency();

    long getTargetAmount();

    String getComment();
}
