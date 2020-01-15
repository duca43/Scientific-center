package org.scientificcenter.util;

import org.camunda.bpm.engine.impl.form.type.StringFormType;

public class EmailFormType extends StringFormType {

    private static final String EMAIL = "email";

    @Override
    public String getName() {
        return EmailFormType.EMAIL;
    }
}