package org.scientificcenter.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.scientificcenter.model.PaymentType;
import org.scientificcenter.repository.MagazineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChooseMagazineCreateHandler implements TaskListener {

    private final FormService formService;
    private final MagazineRepository magazineRepository;
    private final static String MAGAZINE_FIELD_ID = "magazine";

    @Autowired
    public ChooseMagazineCreateHandler(final FormService formService, final MagazineRepository magazineRepository) {
        this.formService = formService;
        this.magazineRepository = magazineRepository;
    }

    @Override
    public void notify(final DelegateTask delegateTask) {
        log.info("{} task - create listener", delegateTask.getName());

        for (final FormField formField : this.formService.getTaskFormData(delegateTask.getId()).getFormFields()) {
            if (formField.getId().equals(ChooseMagazineCreateHandler.MAGAZINE_FIELD_ID)) {
                final Map<String, String> magazines = this.magazineRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(magazine -> String.valueOf(magazine.getId()),
                                magazine -> {
                                    String value = magazine.getName().concat(" (").concat(magazine.getIssn()).concat(")");
                                    if (magazine.getPayment().equals(PaymentType.AUTHOR)) {
                                        value = value.concat(" - open access");
                                    }
                                    return value;
                                }));
                ((EnumFormType) formField.getType()).getValues().putAll(magazines);
                log.info("Added {} magazines into enum form field '{}'", magazines.values().size(), MAGAZINE_FIELD_ID);
                break;
            }
        }
    }
}