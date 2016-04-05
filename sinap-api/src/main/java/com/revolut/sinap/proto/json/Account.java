package com.revolut.sinap.proto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class Account {
    @JsonProperty(required = true)
    private long id;
    @JsonProperty(required = true)
    private String currency;
    @JsonProperty(required = true)
    private String amount;

    public Account setId(long id) {
        this.id = id;
        return this;
    }

    public Account setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public Account setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public long getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        // todo mask id if it is credit card number
        return "Account{" +
                "id=" + id +
                ", currency='" + currency + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
