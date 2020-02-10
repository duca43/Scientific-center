package org.scientificcenter.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaperReviewByReviewerCreateHandler implements TaskListener {

    private final FormService formService;
    private final static String RECOMMENDATION = "recommendation";

    @Autowired
    public PaperReviewByReviewerCreateHandler(final FormService formService) {
        this.formService = formService;
    }

    @Override
    public void notify(final DelegateTask delegateTask) {
        log.info("{} task - create listener", delegateTask.getName());

//        for (final FormField formField : this.formService.getTaskFormData(delegateTask.getId()).getFormFields()) {
//            if (formField.getId().equals(PaperReviewByReviewerCreateHandler.RECOMMENDATION)) {
//                final Map<String, String> recommendationsMap = Arrays.stream(Recommendation.values())
//                        .collect(Collectors.toMap(Recommendation::getType,
//                                recommendation -> {
//                                    final String name = recommendation.name();
//                                    return name.substring(0, 1).toUpperCase() + name.substring(1).replace("_", " ").toLowerCase();
//                                }));
//                ((EnumFormType) formField.getType()).getValues().putAll(recommendationsMap);
//                PaperReviewByReviewerCreateHandler.log.info("Added {} recommendations into enum form field '{}'", recommendationsMap.values().size(), RECOMMENDATION);
//                break;
//            }
//        }
    }
}