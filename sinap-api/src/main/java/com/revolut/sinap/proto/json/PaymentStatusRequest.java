package com.revolut.sinap.proto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class PaymentStatusRequest {
    @JsonProperty(value = "transaction_id", required = true)
    private long transactionId;

    public PaymentStatusRequest setTransactionId(long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public long getTransactionId() {
        return transactionId;
    }

    @Override
    public String toString() {
        return "PaymentStatusRequest{" +
                "transactionId=" + transactionId +
                '}';
    }
}
