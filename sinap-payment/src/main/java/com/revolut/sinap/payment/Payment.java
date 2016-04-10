package com.revolut.sinap.payment;

import java.util.Objects;
import java.util.UUID;

public class Payment implements PaymentServiceOperation {
    private final UUID transactionId;

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

    public Payment(UUID transactionId) {
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId");
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

    @Override
    public UUID transactionId() {
        return transactionId;
    }

    @Override
    public long getSourceAccountId() {
        return sourceAccountId;
    }

    @Override
    public Currency getSourceCurrency() {
        return sourceCurrency;
    }

    @Override
    public long getSourceAmount() {
        return sourceAmount;
    }

    @Override
    public long getTargetAccountId() {
        return targetAccountId;
    }

    @Override
    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    @Override
    public long getTargetAmount() {
        return targetAmount;
    }

    @Override
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
