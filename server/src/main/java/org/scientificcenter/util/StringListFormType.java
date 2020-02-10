package org.scientificcenter.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.form.type.SimpleFormFieldType;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class StringListFormType extends SimpleFormFieldType {

    private static final String STRING_LIST = "string_list";

    @Getter
    private List<String> values;

    @Override
    public String getName() {
        return STRING_LIST;
    }

    @Override
    public TypedValue convertValue(final TypedValue propertyValue) {
        if (propertyValue == null) return null;
        final Object value = propertyValue.getValue();
        if (value == null) return null;
        if (value instanceof List) {
            return Variables.objectValue(value, propertyValue.isTransient()).create();
        } else {
            throw new ProcessEngineException("Value '" + value + "' is not of type List.");
        }
    }

    @Override
    public Object convertFormValueToModelValue(final Object o) {
        return o;
    }

    @Override
    public String convertModelValueToFormValue(final Object o) {
        return o.toString();
    }
}