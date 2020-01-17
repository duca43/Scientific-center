package org.scientificcenter.util;

import org.camunda.bpm.engine.impl.form.type.StringFormType;

public class LocationFormType extends StringFormType {

    private static final String LOCATION = "location";

    @Override
    public String getName() {
        return LocationFormType.LOCATION;
    }
}