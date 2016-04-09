package com.revolut.sinap.payment;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ISO 4217 currencies
 * https://en.wikipedia.org/wiki/ISO_4217
 */
public enum Currency {
    /**
     * Russian Rouble till 1998.
     * Still used in Russian internal transactions.
     */
    RUR(810, 2),
    /**
     * Russian Rouble after 1998.
     */
    RUB(643, 2),
    EUR(978, 2),
    USD(840, 2);

    private final int numericCode;
    private final int minorUnits;

    private static final Map<String, Currency> CURRENCIES_BY_CODE = Stream.of(values())
            .collect(Collectors.toMap(Currency::code, e -> e));

    Currency(int numericCode, int minorUnits) {
        this.numericCode = numericCode;
        this.minorUnits = minorUnits;
    }

    public String code() {
        return name();
    }

    public int numericCode() {
        return numericCode;
    }

    public int minorUnits() {
        return minorUnits;
    }

    @NotNull
    public static Currency getByCode(String code) {
        Currency currency = CURRENCIES_BY_CODE.get(code);
        if (currency == null) {
            throw new IllegalArgumentException("Missing currency for code [" + code + "]");
        }
        return currency;
    }

    @Override
    public String toString() {
        return code() + "(" + numericCode + ")";
    }
}
