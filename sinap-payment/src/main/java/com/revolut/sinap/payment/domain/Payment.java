package com.revolut.sinap.payment.domain;

import com.revolut.sinap.payment.Currency;
import com.revolut.sinap.payment.PaymentServiceOperation;

public class Payment implements PaymentServiceOperation {
    private final long transactionId;

    private long sourceAccountId;
    private Currency sourceCurrency;
    /**
     * minor units
     */
    private long sourceAmount;

    private long targetAccountId;
    private Currency targetCurrency;
    /**
     * minor units
     */
    private long targetAmount;

    private String comment;

    public Payment(long transactionId) {
        this.transactionId = transactionId;
    }

    public Payment setSourceAccountId(long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
        return this;
    }

    public Payment setSourceCurrency(Currency sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
        return this;
    }

    public Payment setSourceAmount(long sourceAmount) {
        this.sourceAmount = sourceAmount;
        return this;
    }

    public Payment setTargetAccountId(long targetAccountid) {
        this.targetAccountId = targetAccountid;
        return this;
    }

    public Payment setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
        return this;
    }

    public Payment setTargetAmount(long targetAmount) {
        this.targetAmount = targetAmount;
        return this;
    }

    public Payment setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public long transactionId() {
        return transactionId;
    }

    public long getSourceAccountId() {
        return sourceAccountId;
    }

    public Currency getSourceCurrency() {
        return sourceCurrency;
    }

    public long getSourceAmount() {
        return sourceAmount;
    }

    public long getTargetAccountId() {
        return targetAccountId;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public long getTargetAmount() {
        return targetAmount;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        // todo mask account ids if it is card numbers
        return "Payment{" +
                "transactionId=" + transactionId +
                ", sourceAccountId=" + sourceAccountId +
                ", sourceCurrency='" + sourceCurrency + '\'' +
                ", sourceAmount=" + sourceAmount +
                ", targetAccountId=" + targetAccountId +
                ", targetCurrency='" + targetCurrency + '\'' +
                ", targetAmount=" + targetAmount +
                ", comment='" + comment + '\'' +
                '}';
    }
}
