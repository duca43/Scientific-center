package org.scientificcenter.util;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultivaluedEnumFormType extends EnumFormType {

    private String typeName;
    private static final String MULTIPLE_ENUM = "multiple_enum_";

    public MultivaluedEnumFormType(final Map<String, String> values) {
        super(values);
    }

    public MultivaluedEnumFormType(final String typeName) {
        super(new HashMap<>());
        this.typeName = typeName;
    }

    @Override
    public TypedValue convertValue(final TypedValue propertyValue) {
        if (propertyValue == null) return null;
        final Object value = propertyValue.getValue();
        if (value == null) return null;
        if (value instanceof List) {
//            this.validateValue((List<?>) value);
            return Variables.objectValue(value, propertyValue.isTransient()).create();
        } else {
            throw new ProcessEngineException("Value '" + value + "' is not of type List.");
        }
    }

    @Override
    public String convertModelValueToFormValue(final Object modelValue) {
        if (modelValue == null) return null;
        if (modelValue instanceof List) {
//            this.validateValue((List<?>) modelValue);
            return ((List<?>) modelValue).stream().map(String::valueOf).collect(Collectors.joining(","));
        } else {
            throw new ProcessEngineException("Value '" + modelValue + "' is not of type List.");
        }
    }

//    protected void validateValue(final List<?> value) {
//        value.forEach(o -> {
//            if (o != null && this.values != null && !this.values.containsKey(o.toString())) {
//                throw new ProcessEngineException("Invalid value for enum form property: " + value);
//            }
//        });
//    }

    @Override
    public String getName() {
        return MultivaluedEnumFormType.MULTIPLE_ENUM.concat(this.typeName);
    }
}