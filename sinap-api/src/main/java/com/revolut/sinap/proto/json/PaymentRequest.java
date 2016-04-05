package com.revolut.sinap.proto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
// @JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest {
    @JsonProperty(required = true)
    private long transactionId;

    @JsonProperty(required = true)
    private Account source;

    @JsonProperty(required = true)
    private Account target;

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

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "transactionId=" + transactionId +
                ", source=" + source +
                ", target=" + target +
                '}';
    }
}
