package com.revolut.sinap.payment;

import org.testng.annotations.Test;

import java.util.Locale;

import static com.revolut.sinap.payment.Currencies.formatAmount;
import static com.revolut.sinap.payment.Currencies.parseAmount;
import static org.testng.Assert.assertEquals;

public class CurrenciesTest {
    @Test
    public void testExponent() {
        assertEquals(Currencies.exponent(0), 1);
        assertEquals(Currencies.exponent(1), 10);
        assertEquals(Currencies.exponent(2), 100);
        assertEquals(Currencies.exponent(3), 1000);
        assertEquals(Currencies.exponent(4), 10000);
    }

    @Test
    public void parseAmountTest() {
        assertEquals(parseAmount("12", Currency.RUB), 1200);
        assertEquals(parseAmount("12.34", Currency.RUB), 1234);
        assertEquals(parseAmount("12,34", Currency.RUB), 1234);
    }

    @Test
    public void formatAmountTest() {
        assertEquals(formatAmount(1200, Currency.RUB, Locale.ENGLISH), "12.00");
        assertEquals(formatAmount(1234, Currency.RUB, Locale.ENGLISH), "12.34");
        assertEquals(formatAmount(1234, Currency.RUB, Locale.FRENCH), "12,34");
    }
}
