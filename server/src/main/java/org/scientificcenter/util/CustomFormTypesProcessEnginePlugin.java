package org.scientificcenter.util;

import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.stereotype.Service;

@Service
public class CustomFormTypesProcessEnginePlugin extends AbstractProcessEnginePlugin {

    @Override
    public void preInit(final ProcessEngineConfigurationImpl processEngineConfiguration) {
        processEngineConfiguration.getCustomFormTypes().add(new LocationFormType());
        processEngineConfiguration.getCustomFormTypes().add(new PasswordFormType());
        processEngineConfiguration.getCustomFormTypes().add(new EmailFormType());
        processEngineConfiguration.getCustomFormTypes().add(new DoubleFormType());
        processEngineConfiguration.getCustomFormTypes().add(new StringListFormType());
        processEngineConfiguration.getCustomFormTypes().add(new MultivaluedEnumFormType("scientific_areas"));
        processEngineConfiguration.getCustomFormTypes().add(new MultivaluedEnumFormType("editors"));
        processEngineConfiguration.getCustomFormTypes().add(new MultivaluedEnumFormType("reviewers"));
        processEngineConfiguration.getCustomFormTypes().add(new MultivaluedEnumFormType("available_reviewers"));
        processEngineConfiguration.getCustomFormTypes().add(new FileFormType("upload"));
        processEngineConfiguration.getCustomFormTypes().add(new FileFormType("view"));
        processEngineConfiguration.getCustomFormFieldValidators().put("minselection", MinSelectionValidator.class);
    }
}