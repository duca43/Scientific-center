package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.scientificcenter.model.ScientificArea;
import org.scientificcenter.model.ScientificPaper;
import org.scientificcenter.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ChooseSuitableEditorService implements JavaDelegate {

    private final ScientificPaperService scientificPaperService;
    private final UserService userService;
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private static final String SUITABLE_EDITOR_FOUND_FLAG = "suitableEditorFound";
    private static final String CHOSEN_EDITOR = "chosenEditor";

    @Autowired
    public ChooseSuitableEditorService(final ScientificPaperService scientificPaperService, final UserService userService) {
        this.scientificPaperService = scientificPaperService;
        this.userService = userService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        log.info("Executing service named 'Choose suitable editor'");

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateExecution.getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());
        final ScientificArea scientificArea = scientificPaper.getScientificArea();

        log.info("Finding editor suitable for scientific area '{}' in scientific paper with id '{}'", scientificArea.getName(), scientificPaper.getId());

        final Optional<User> suitableEditorOptional = scientificPaper.getMagazine()
                .getEditors()
                .stream()
                .filter(user -> user.getScientificAreas().contains(scientificArea))
                .findAny();

        delegateExecution.setVariable(SUITABLE_EDITOR_FOUND_FLAG, suitableEditorOptional.isPresent());

        if (suitableEditorOptional.isPresent()) {
            final User suitableEditor = suitableEditorOptional.get();
            log.info("Found suitable editor with username '{}'", suitableEditor.getUsername());
            delegateExecution.setVariable(CHOSEN_EDITOR, suitableEditor.getUsername());
            scientificPaper.setEditor(suitableEditor);
        } else {
            final String mainEditorUsername = (String) delegateExecution.getVariable("main_editor");
            log.info("Suitable editor is not found. Main editor with username '{}' will be set as a suitable editor", mainEditorUsername);
            delegateExecution.setVariable(CHOSEN_EDITOR, mainEditorUsername);
            scientificPaper.setEditor(this.userService.findByUsername(mainEditorUsername));
        }

        this.scientificPaperService.save(scientificPaper);
    }
}