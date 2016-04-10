package com.revolut.sinap.api.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.revolut.sinap.api.ResponseCode;

import java.util.UUID;

@JsonSerialize
public class PaymentStatusResponse {
    /**
     * uuid
     */
    @JsonProperty(value = "transaction_id")
    private String transactionId;

    private int responseCode;
    private String message;

    public PaymentStatusResponse setTransactionId(String transactionId) {
        this.transactionId = UUID.fromString(transactionId).toString();
        return this;
    }

    @JsonIgnore
    public PaymentStatusResponse setResponseCode(ResponseCode responseCode) {
        return setResponseCode(responseCode.code())
                .setMessage(responseCode.message());
    }

    public PaymentStatusResponse setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public PaymentStatusResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getTransactionId() {
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
        return "PaymentStatusResponse{" +
                "transactionId=" + transactionId +
                ", responseCode=" + responseCode +
                ", message='" + message + '\'' +
                '}';
    }
}
