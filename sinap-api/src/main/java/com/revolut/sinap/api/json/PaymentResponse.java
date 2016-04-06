package com.revolut.sinap.api.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class PaymentResponse {
    @JsonProperty(value = "transaction_id")
    private long transactionId;

    /**
     * response code
     */
    private int response;
    private String message;

    public PaymentResponse setTransactionId(long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public PaymentResponse setResponse(int response) {
        this.response = response;
        return this;
    }

    public PaymentResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public int getResponse() {
        return response;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "transactionId=" + transactionId +
                ", response" + response +
                ", message='" + message + '\'' +
                '}';
    }
}
