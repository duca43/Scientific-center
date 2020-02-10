package org.scientificcenter.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.modelmapper.ModelMapper;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.scientificcenter.dto.MagazineDto;
import org.scientificcenter.dto.ScientificPaperDto;
import org.scientificcenter.dto.ValidationDto;
import org.scientificcenter.model.*;
import org.scientificcenter.repository.KeywordRepository;
import org.scientificcenter.service.MagazineService;
import org.scientificcenter.service.ScientificAreasService;
import org.scientificcenter.service.ScientificPaperService;
import org.scientificcenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class EnterScientificPaperInfoCompleteHandler implements TaskListener {

    private final MagazineService magazineService;
    private final ScientificPaperService scientificPaperService;
    private final UserService userService;
    private final ScientificAreasService scientificAreasService;
    private final KeywordRepository keywordRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ModelMapper modelMapper;
    private final static String MAGAZINE_FIELD_ID = "magazine";
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private static final String SCIENTIFIC_PAPER_CREATED = "scientific_paper_created";

    @Autowired
    public EnterScientificPaperInfoCompleteHandler(final MagazineService magazineService, final ScientificPaperService scientificPaperService, final UserService userService, final ScientificAreasService scientificAreasService, final KeywordRepository keywordRepository, final SimpMessagingTemplate simpMessagingTemplate, final ModelMapper modelMapper) {
        this.magazineService = magazineService;
        this.scientificPaperService = scientificPaperService;
        this.userService = userService;
        this.scientificAreasService = scientificAreasService;
        this.keywordRepository = keywordRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.modelMapper = modelMapper;
    }

    @Override
    public void notify(final DelegateTask delegateTask) {
        log.info("{} task - complete listener", delegateTask.getName());

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateTask.getExecution().getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());

        if (scientificPaper == null) {
            log.error("Scientific paper with id '{}' does not exist", createScientificPaperDto.getId());
            delegateTask.getExecution().setVariable(SCIENTIFIC_PAPER_CREATED, false);
            final ValidationDto validationDto = ValidationDto.builder()
                    .valid(false)
                    .errorMessage("Creation of scientific paper has failed. Please, try again.")
                    .build();
            this.simpMessagingTemplate.convertAndSend("/scientific_paper/created", validationDto);
            return;
        }

        final String username = delegateTask.getAssignee();
        final String magazineIdString = (String) delegateTask.getExecution().getVariable(MAGAZINE_FIELD_ID);
        final Long magazineId = Long.valueOf(magazineIdString);

        log.info("Save scientific paper with id '{}' created by author '{}' in magazine with id '{}'", createScientificPaperDto.getId(), username, magazineId);

        final Magazine magazine = this.magazineService.findById(magazineId);
        final User author = this.userService.findByUsername(username);
        final ScientificArea scientificArea = this.scientificAreasService.findById(Long.valueOf(createScientificPaperDto.getScientificArea()));

        final List<String> keywords = createScientificPaperDto.getKeywords();

        final Set<Keyword> keywordList = new HashSet<>();
        if (keywords != null && !keywords.isEmpty()) {
            keywords.forEach(keywordName -> {
                Keyword keyword = this.keywordRepository.findByName(keywordName);
                if (keyword == null)
                    keyword = this.keywordRepository.save(Keyword.builder().name(keywordName).build());
                keywordList.add(keyword);
            });
        }

        scientificPaper.setEnabled(false);
        scientificPaper.setChosenCoauthors(false);
        scientificPaper.setApprovedByMainEditor(false);
        scientificPaper.setPdfFormattedWell(false);
        scientificPaper.setRequestedChanges(false);
        scientificPaper.setTitle(createScientificPaperDto.getTitle());
        scientificPaper.setPaperAbstract(createScientificPaperDto.getPaperAbstract());
        scientificPaper.setKeywords(keywordList);
        scientificPaper.setScientificArea(scientificArea);
        scientificPaper.setAuthor(author);
        scientificPaper.setMagazine(magazine);

        this.scientificPaperService.save(scientificPaper);

        delegateTask.getExecution().setVariable(SCIENTIFIC_PAPER_CREATED, true);
        this.simpMessagingTemplate.convertAndSend("/scientific_paper/created", ValidationDto.builder().valid(true).build());

        final ScientificPaperDto scientificPaperDto = this.modelMapper.map(scientificPaper, ScientificPaperDto.class);
        final MagazineDto magazineDto = this.modelMapper.map(scientificPaper.getMagazine(), MagazineDto.class);
        scientificPaperDto.setMagazineDto(magazineDto);
        this.simpMessagingTemplate.convertAndSend("/scientific_paper/added", scientificPaperDto);
    }
}