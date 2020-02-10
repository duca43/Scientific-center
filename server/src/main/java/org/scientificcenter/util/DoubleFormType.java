package org.scientificcenter.util;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.form.type.SimpleFormFieldType;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.DoubleValue;
import org.camunda.bpm.engine.variable.value.TypedValue;

public class DoubleFormType extends SimpleFormFieldType {

    private static final String DOUBLE = "double";

    @Override
    public String getName() {
        return DoubleFormType.DOUBLE;
    }

    @Override
    public Object convertFormValueToModelValue(final Object o) {
        return o != null && !"".equals(o) ? Double.parseDouble(o.toString()) : null;
    }

    @Override
    public String convertModelValueToFormValue(final Object o) {
        return o == null ? null : o.toString();
    }

    @Override
    protected TypedValue convertValue(final TypedValue typedValue) {
        if (typedValue instanceof DoubleValue) {
            return typedValue;
        }

        final Object value = typedValue.getValue();
        if (value == null) {
            return Variables.doubleValue(null, typedValue.isTransient());
        } else if (!(value instanceof Number) && !(value instanceof String)) {
            throw new ProcessEngineException("Value '" + value + "' is not of type Double.");
        } else {
            return Variables.doubleValue(Double.parseDouble(value.toString()), typedValue.isTransient());
        }
    }
}