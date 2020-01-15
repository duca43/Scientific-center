package org.scientificcenter.util;

import org.camunda.bpm.engine.impl.form.validator.FormFieldValidator;
import org.camunda.bpm.engine.impl.form.validator.FormFieldValidatorContext;

import java.util.List;

public class MinSelectionValidator implements FormFieldValidator {

    @Override
    public boolean validate(final Object submittedValue, final FormFieldValidatorContext formFieldValidatorContext) {
        final int minSelection = Integer.parseInt(formFieldValidatorContext.getConfiguration());
        return submittedValue instanceof List<?> && ((List<?>) submittedValue).size() >= minSelection;
    }
}
