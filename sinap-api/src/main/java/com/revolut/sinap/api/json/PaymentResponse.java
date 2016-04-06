package com.revolut.sinap.api.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class PaymentResponse {
    /**
     * result code
     */
    @JsonProperty(required = true)
    private int result;
    @JsonProperty(required = true)
    private String message;

    public PaymentResponse setResult(int result) {
        this.result = result;
        return this;
    }

    public PaymentResponse setMessage(String message) {
        this.message = message;
        return this;
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
                "result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
