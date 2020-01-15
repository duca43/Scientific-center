package org.scientificcenter.util;

import org.camunda.bpm.engine.impl.form.type.StringFormType;

public class PasswordFormType extends StringFormType {

    private static final String PASSWORD = "password";

    @Override
    public String getName() {
        return PasswordFormType.PASSWORD;
    }
}