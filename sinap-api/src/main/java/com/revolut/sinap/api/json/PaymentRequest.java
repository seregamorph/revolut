package com.revolut.sinap.api.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Single-phase payment request
 */
@JsonSerialize
@JsonPropertyOrder({"transaction_id", "source", "target"})
public class PaymentRequest {
    @JsonProperty(value = "transaction_id", required = true)
    private long transactionId;

    @JsonProperty(required = true)
    private Account source;

    @JsonProperty(required = true)
    private Account target;

    @JsonProperty(required = false)
    private String comment;

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

    public PaymentRequest setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public Account getSource() {
        return source;
    }

    public Account getTarget() {
        return target;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "transactionId=" + transactionId +
                ", source=" + source +
                ", target=" + target +
                ", comment='" + comment + '\'' +
                '}';
    }

    /**
     * Payment request source/destination account information
     */
    @JsonSerialize
    public static class Account {
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
}
