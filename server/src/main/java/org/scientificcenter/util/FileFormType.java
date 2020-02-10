package org.scientificcenter.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.impl.form.type.SimpleFormFieldType;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.camunda.bpm.engine.variable.value.TypedValue;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class FileFormType extends SimpleFormFieldType {

    private static final String FILE = "file_";
    private String fileType;

    @Override
    public String getName() {
        return FileFormType.FILE.concat(this.fileType);
    }

    @Override
    public Object convertFormValueToModelValue(final Object o) {
        return null;
    }

    @Override
    public String convertModelValueToFormValue(final Object o) {
        return null;
    }

    @Override
    public TypedValue convertValue(final TypedValue propertyValue) {
        if (propertyValue instanceof FileValue) {
            return propertyValue;
        } else {
            final Object value = propertyValue.getValue();
            return Variables.stringValue((String) value);
        }
    }
}