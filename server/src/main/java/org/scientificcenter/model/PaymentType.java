package org.scientificcenter.model;

import java.util.Arrays;

public enum PaymentType {

    EDITOR("editor"),
    USER("user");

    private final String type;

    PaymentType(final String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static PaymentType valueOfGivenString(final String value) {
        return Arrays.asList(PaymentType.values()).stream().filter(paymentType -> paymentType.type.equalsIgnoreCase(value)).findFirst().orElse(null);
    }

}
