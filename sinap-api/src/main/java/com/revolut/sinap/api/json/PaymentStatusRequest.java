package com.revolut.sinap.api.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

@JsonSerialize
public class PaymentStatusRequest {
    /**
     * uuid
     */
    @JsonProperty(value = "transaction_id", required = true)
    private String transactionId;

    public PaymentStatusRequest setTransactionId(String transactionId) {
        this.transactionId = UUID.fromString(transactionId).toString();
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public String toString() {
        return "PaymentStatusRequest{" +
                "transactionId=" + transactionId +
                '}';
    }
}
