package com.revolut.sinap.payment;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Todo refactor to Money class
 */
public class Currencies {
    private Currencies() {
    }

    public static long parseAmount(String amountStr, Currency currency) {
        CurrencyNumberFormat tnf = getCurrencyNumberFormat(currency, Locale.ENGLISH);
        return tnf.parse(amountStr.replace(',', '.'));
    }

    public static String formatAmount(long amount, Currency currency, Locale locale) {
        CurrencyNumberFormat tnf = getCurrencyNumberFormat(currency, locale);
        return tnf.format(amount);
    }

    private static final ConcurrentMap<String, ThreadLocal<CurrencyNumberFormat>> numberFormats =
            new ConcurrentHashMap<>();

    @NotNull
    private static CurrencyNumberFormat getCurrencyNumberFormat(Currency currency, Locale locale) {
        return numberFormats.computeIfAbsent(currency.code() + "_" + locale, k -> {
            int minorUnits = currency.minorUnits();
            int exponent = exponent(minorUnits);

            return ThreadLocal.withInitial(() -> {
                NumberFormat nf = NumberFormat.getNumberInstance(locale);
                nf.setGroupingUsed(false);
                nf.setMinimumFractionDigits(minorUnits);
                return new CurrencyNumberFormat(nf, exponent);
            });
        }).get();
    }

    static int exponent(int minorUnits) {
        return pow(10, minorUnits);
    }

    private static int pow(int b, int k) {
        if (k < 0) {
            throw new IllegalArgumentException();
        }
        // copied from google guava IntMath.pow
        for (int accum = 1; ; k >>= 1) {
            switch (k) {
                case 0:
                    return accum;
                case 1:
                    return b * accum;
                default:
                    accum *= ((k & 1) == 0) ? 1 : b;
                    b *= b;
            }
        }
    }

    private static BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        } else if (number instanceof Long || number instanceof Integer) {
            return BigDecimal.valueOf(number.longValue());
        } else if (number instanceof Double) {
            return BigDecimal.valueOf((Double) number);
        } else {
            throw new IllegalArgumentException("Could not cast " + number.getClass() + " with value " + number +
                    " to BigDecimal");
        }
    }

    // non-thread-safe
    // using ThreadLocal<CurrencyNumberFormat>
    private static class CurrencyNumberFormat {
        private final NumberFormat numberFormat;
        private final int exponent;

        CurrencyNumberFormat(NumberFormat numberFormat, int exponent) {
            this.numberFormat = numberFormat;
            this.exponent = exponent;
        }

        String format(long amount) {
            // convert to double required for correct double evaluation
            return numberFormat.format((double) amount / exponent);
        }

        long parse(String amountStr) {
            try {
                BigDecimal value = toBigDecimal(numberFormat.parse(amountStr));
                return value.multiply(BigDecimal.valueOf(exponent)).longValue();
            } catch (ParseException e) {
                throw new IllegalArgumentException("Illegal amount value [" + amountStr + "]");
            }
        }

        @Override
        public String toString() {
            return "CurrencyNumberFormat{" +
                    "numberFormat=" + numberFormat +
                    ", exponent=" + exponent +
                    '}';
        }
    }
}
