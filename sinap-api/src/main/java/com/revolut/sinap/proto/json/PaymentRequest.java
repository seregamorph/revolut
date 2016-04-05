package com.revolut.sinap.proto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Single-phase payment request
 */
@JsonSerialize
// @JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"transaction_id", "source", "target"})
public class PaymentRequest {
    @JsonProperty(value = "transaction_id", required = true)
    private long transactionId;

    @JsonProperty(required = true)
    private Account source;

    @JsonProperty(required = true)
    private Account target;

    @JsonProperty(required = false)
    private String comment;

    public PaymentRequest setTransactionId(long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public PaymentRequest setSource(Account source) {
        this.source = source;
        return this;
    }

    public PaymentRequest setTarget(Account target) {
        this.target = target;
        return this;
    }

    public PaymentRequest setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public Account getSource() {
        return source;
    }

    public Account getTarget() {
        return target;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "transactionId=" + transactionId +
                ", source=" + source +
                ", target=" + target +
                ", comment='" + comment + '\'' +
                '}';
    }
}
