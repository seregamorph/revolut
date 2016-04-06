package com.revolut.sinap.api.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class PaymentResponse {
    @JsonProperty(value = "transaction_id")
    private long transactionId;

    /**
     * result code
     */
    @JsonProperty(required = true)
    private int result;
    @JsonProperty(required = true)
    private String message;

    public PaymentResponse setTransactionId(long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public PaymentResponse setResult(int result) {
        this.result = result;
        return this;
    }

    public PaymentResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "transactionId=" + transactionId +
                ", result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
