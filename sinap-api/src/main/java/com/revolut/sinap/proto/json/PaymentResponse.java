package com.revolut.sinap.proto.json;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class PaymentResponse {
    private int result;
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
