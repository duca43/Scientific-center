package org.scientificcenter.model;

import java.util.Arrays;

public enum Recommendation {

    REJECT("reject"),
    ACCEPT_MAJOR_CORRECTIONS("acceptWithMajorCorrections"),
    ACCEPT_MINOR_CORRECTIONS("acceptWithMinorCorrections"),
    ACCEPT("accept");

    private final String type;

    Recommendation(final String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static Recommendation valueOfGivenString(final String value) {
        return Arrays.stream(Recommendation.values()).filter(recommendation -> recommendation.type.equalsIgnoreCase(value)).findFirst().orElse(null);
    }
}