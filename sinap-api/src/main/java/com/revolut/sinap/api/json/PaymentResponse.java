package com.revolut.sinap.api.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.revolut.sinap.api.ResponseCode;

@JsonSerialize
public class PaymentResponse {
    @JsonProperty(value = "transaction_id")
    private long transactionId;
    @JsonProperty(value = "response_code")
    private int responseCode;
    private String message;

    public PaymentResponse setTransactionId(long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    @JsonIgnore
    public PaymentResponse setResponseCode(ResponseCode responseCode) {
        return setResponseCode(responseCode.code())
                .setMessage(responseCode.message());
    }

    public PaymentResponse setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public PaymentResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "transactionId=" + transactionId +
                ", responseCode" + responseCode +
                ", message='" + message + '\'' +
                '}';
    }
}
