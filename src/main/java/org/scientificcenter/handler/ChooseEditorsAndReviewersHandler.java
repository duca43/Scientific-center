package org.scientificcenter.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.FormField;
import org.scientificcenter.dto.MagazineDto;
import org.scientificcenter.model.Authority;
import org.scientificcenter.model.Magazine;
import org.scientificcenter.model.ScientificArea;
import org.scientificcenter.model.User;
import org.scientificcenter.service.AuthorityService;
import org.scientificcenter.service.MagazineService;
import org.scientificcenter.service.UserService;
import org.scientificcenter.util.MultivaluedEnumFormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChooseEditorsAndReviewersHandler implements TaskListener {

    private final FormService formService;
    private final MagazineService magazineService;
    private final UserService userService;
    private final AuthorityService authorityService;
    private final static String EDITORS = "editors";
    private final static String REVIEWERS = "reviewers";
    private static final String MAGAZINE_DTO = "magazineDto";
    private static final String ROLE_EDITOR = "ROLE_EDITOR";
    private static final String ROLE_REVIEWER = "ROLE_REVIEWER";

    @Autowired
    public ChooseEditorsAndReviewersHandler(final FormService formService, final MagazineService magazineService, final UserService userService, final AuthorityService authorityService) {
        this.formService = formService;
        this.magazineService = magazineService;
        this.userService = userService;
        this.authorityService = authorityService;
    }

    @Override
    public void notify(final DelegateTask delegateTask) {
        ChooseEditorsAndReviewersHandler.log.info("{} task - create listener", delegateTask.getName());

        final MagazineDto magazineDto = (MagazineDto) delegateTask.getExecution().getVariable(ChooseEditorsAndReviewersHandler.MAGAZINE_DTO);
        ChooseEditorsAndReviewersHandler.log.info("Retrieved execution variable 'magazineDto' -> {}", magazineDto);

        final Magazine magazine = this.magazineService.findByIssn(magazineDto.getIssn());

        if (magazine == null) return;

        for (final FormField formField : this.formService.getTaskFormData(delegateTask.getId()).getFormFields()) {
            if (formField.getId().equals(ChooseEditorsAndReviewersHandler.EDITORS)) {
                this.fillMultipleEnumFormField(magazine, formField, ChooseEditorsAndReviewersHandler.ROLE_EDITOR, ChooseEditorsAndReviewersHandler.EDITORS);
            } else if (formField.getId().equals(ChooseEditorsAndReviewersHandler.REVIEWERS)) {
                this.fillMultipleEnumFormField(magazine, formField, ChooseEditorsAndReviewersHandler.ROLE_REVIEWER, ChooseEditorsAndReviewersHandler.REVIEWERS);
            }
        }
    }

    private void fillMultipleEnumFormField(final Magazine magazine, final FormField formField, final String role, final String field) {
        final List<ScientificArea> magazineScientificAreas = new ArrayList<>(magazine.getScientificAreas());
        final Authority authority = this.authorityService.findByName(role);
        final Map<String, String> users = this.userService
                .findAllByUsernameNotAndAuthoritiesContainsAndScientificAreasIsIn(magazine.getMainEditor().getUsername(), authority, magazineScientificAreas)
                .stream()
                .collect(Collectors.toMap(user -> user.getId().toString(), User::getUsername));
        ((MultivaluedEnumFormType) formField.getType()).getValues().clear();
        ((MultivaluedEnumFormType) formField.getType()).getValues().putAll(users);
        ChooseEditorsAndReviewersHandler.log.info("Added {} users into multivalued enum form field '{}'", users.values().size(), field);
    }
}