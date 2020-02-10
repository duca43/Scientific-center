package org.scientificcenter.model;

import java.util.Arrays;

public enum PaymentType {

    AUTHOR("author"),
    READER("reader");

    private final String type;

    PaymentType(final String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static PaymentType valueOfGivenString(final String value) {
        return Arrays.stream(PaymentType.values()).filter(paymentType -> paymentType.type.equalsIgnoreCase(value)).findFirst().orElse(null);
    }
}