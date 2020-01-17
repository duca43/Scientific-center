package org.scientificcenter.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.FormField;
import org.scientificcenter.model.ScientificArea;
import org.scientificcenter.repository.ScientificAreaRepository;
import org.scientificcenter.util.MultivaluedEnumFormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RegistrationFormHandler implements TaskListener {

    private final FormService formService;
    private final ScientificAreaRepository scientificAreaRepository;
    private final static String SCIENTIFIC_AREAS = "scientificAreas";

    @Autowired
    public RegistrationFormHandler(final FormService formService, final ScientificAreaRepository scientificAreaRepository) {
        this.formService = formService;
        this.scientificAreaRepository = scientificAreaRepository;
    }

    @Override
    public void notify(final DelegateTask delegateTask) {
        RegistrationFormHandler.log.info("{} task - create listener", delegateTask.getName());

        for (final FormField formField : this.formService.getTaskFormData(delegateTask.getId()).getFormFields()) {
            if (formField.getId().equals(RegistrationFormHandler.SCIENTIFIC_AREAS)) {
                final Map<String, String> scientificAreas = this.scientificAreaRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(scientificArea -> scientificArea.getId().toString(), ScientificArea::getName));
                ((MultivaluedEnumFormType) formField.getType()).getValues().putAll(scientificAreas);
                RegistrationFormHandler.log.info("Added {} scientific areas into multivalued enum form field '{}'", scientificAreas.values().size(), RegistrationFormHandler.SCIENTIFIC_AREAS);
                break;
            }
        }
    }
}