package com.revolut.sinap.api.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Payment request source/destination account information
 */
@JsonSerialize
public class Account {
    /**
     * unique account id
     */
    @JsonProperty(required = true)
    private long id;
    /**
     * Currency ISO 4217 code (3-letter)
     */
    @JsonProperty(required = true)
    private String currency;
    /**
     * ',' or '.' - separated operation amount in major units
     */
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
