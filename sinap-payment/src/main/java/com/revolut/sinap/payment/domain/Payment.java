package com.revolut.sinap.payment.domain;

import com.revolut.sinap.api.ResponseCode;

public class Payment {
    private final long transactionId;

    private long sourceAccountid;
    private String sourceCurrency;
    /**
     * minor units
     */
    private long sourceAmount;

    private long targetAccountid;
    private String targetCurrency;
    /**
     * minor units
     */
    private long targetAmount;

    private String comment;

    private ResponseCode responseCode = ResponseCode.RECEIVED;

    public Payment(long transactionId) {
        this.transactionId = transactionId;
    }

    public Payment setSourceAccountid(long sourceAccountid) {
        this.sourceAccountid = sourceAccountid;
        return this;
    }

    public Payment setSourceCurrency(String sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
        return this;
    }

    public Payment setSourceAmount(long sourceAmount) {
        this.sourceAmount = sourceAmount;
        return this;
    }

    public Payment setTargetAccountid(long targetAccountid) {
        this.targetAccountid = targetAccountid;
        return this;
    }

    public Payment setTargetCurrency(String targetCurrency) {
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

    public Payment setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public long transactionId() {
        return transactionId;
    }

    public long getSourceAccountid() {
        return sourceAccountid;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public long getSourceAmount() {
        return sourceAmount;
    }

    public long getTargetAccountid() {
        return targetAccountid;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public long getTargetAmount() {
        return targetAmount;
    }

    public String getComment() {
        return comment;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        // todo mask account ids if it is card numbers
        return "Payment{" +
                "transactionId=" + transactionId +
                ", sourceAccountid=" + sourceAccountid +
                ", sourceCurrency='" + sourceCurrency + '\'' +
                ", sourceAmount=" + sourceAmount +
                ", targetAccountid=" + targetAccountid +
                ", targetCurrency='" + targetCurrency + '\'' +
                ", targetAmount=" + targetAmount +
                ", comment='" + comment + '\'' +
                ", responseCode=" + responseCode +
                '}';
    }
}
