package com.revolut.sinap.payment.domain;

import com.revolut.sinap.api.ResponseCode;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Exception while payment processing
 */
public class PaymentException extends Exception {
    private final ResponseCode responseCode;

    public PaymentException(ResponseCode responseCode, @Nullable Throwable cause) {
        super("Error while processing payment " + responseCode, cause);
        this.responseCode = Objects.requireNonNull(responseCode, "responseCode");
    }

    public PaymentException(ResponseCode responseCode) {
        this(responseCode, null);
    }

    public ResponseCode responseCode() {
        return responseCode;
    }
}
