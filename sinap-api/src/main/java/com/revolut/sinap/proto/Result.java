package com.revolut.sinap.proto;

/**
 * Payment result/status codes.
 * Code series:
 * <pre>
 *   1 - success
 * 1xx - failures
 * 2xx - in progress
 * 3xx - two phase: waiting for confirm
 * </pre>
 * Most of codes are reserved.
 */
public enum Result {
    // final: success
    SUCCESS(1, "Success"),

    // final: failures
    CANCELED(100, "Operation canceled"),
    BAD_SOURCE_ACCOUNT(101, "Source account does not exist"),
    BAD_TARGET_ACCOUNT(102, "Target account does not exist"),
    LOCKED_SOURCE_ACCOUNT(103, "Source account is locked"),
    LOCKED_TARGET_ACCOUNT(104, "Target account is locked"),
    NO_MONEY(105, "Not enough money on source account"),
    LIMIT_EXCEEDED(105, "Daily limit exceeded"),

    // in progress
    RECEIVED(200, "Operation in progress"),

    // twophase: waiting for confirm
    CONFIRM(300, "Waiting for confirm");

    private final int code;
    private final String message;

    Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static boolean isFinal(int code) {
        return code > 0 && code < 200;
    }

    public static boolean isSuccess(int code) {
        return code >= 0 && code < 100;
    }

    public static boolean isFailed(int code) {
        return code >= 100 && code < 200;
    }

    public static boolean isConfirm(int code) {
        return code >= 300 && code < 400;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}