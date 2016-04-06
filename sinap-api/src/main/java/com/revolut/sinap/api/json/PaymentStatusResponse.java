package com.revolut.sinap.api.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class PaymentStatusResponse {
    @JsonProperty(value = "transaction_id")
    private long transactionId;

    /**
     * result code
     */
    private int result;
    private String message;

    public PaymentStatusResponse setTransactionId(long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public PaymentStatusResponse setResult(int result) {
        this.result = result;
        return this;
    }

    public PaymentStatusResponse setMessage(String message) {
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
        return "PaymentStatusResponse{" +
                "transactionId=" + transactionId +
                ", result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
