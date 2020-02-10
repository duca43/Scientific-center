package org.scientificcenter.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.modelmapper.ModelMapper;
import org.scientificcenter.dto.AddCoauthorDto;
import org.scientificcenter.dto.AddCoauthorResponseDto;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.scientificcenter.model.Coauthor;
import org.scientificcenter.model.Location;
import org.scientificcenter.model.ScientificPaper;
import org.scientificcenter.repository.CoauthorRepository;
import org.scientificcenter.service.LocationService;
import org.scientificcenter.service.ScientificPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AddCoauthorCompleteHandler implements TaskListener {

    private final ScientificPaperService scientificPaperService;
    private final LocationService locationService;
    private final CoauthorRepository coauthorRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ModelMapper modelMapper;
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private static final String ADD_COAUTHOR_DTO = "addCoauthorDto";

    @Autowired
    public AddCoauthorCompleteHandler(final ScientificPaperService scientificPaperService, final LocationService locationService, final CoauthorRepository coauthorRepository, final SimpMessagingTemplate simpMessagingTemplate, final ModelMapper modelMapper) {
        this.scientificPaperService = scientificPaperService;
        this.locationService = locationService;
        this.coauthorRepository = coauthorRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.modelMapper = modelMapper;
    }

    @Override
    public void notify(final DelegateTask delegateTask) {
        log.info("{} task - complete listener", delegateTask.getName());

        final AddCoauthorDto addCoauthorDto = (AddCoauthorDto) delegateTask.getExecution().getVariable(ADD_COAUTHOR_DTO);
        log.info("Retrieved execution variable addCoauthorDto -> {}", addCoauthorDto);

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateTask.getExecution().getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());

        log.info("Adding co-author named '{}' into scientific paper with id '{}'", addCoauthorDto.getName(), createScientificPaperDto.getId());

        final Location location = this.locationService.save(addCoauthorDto.getLocation());

        final Coauthor coauthor = this.modelMapper.map(addCoauthorDto, Coauthor.class);
        coauthor.setScientificPaper(scientificPaper);
        coauthor.setLocation(location);
        this.coauthorRepository.save(coauthor);
        log.info("Co-author named '{}' is saved successfully with id '{}'", coauthor.getName(), coauthor.getId());

        log.info("Want more co-authors flag: {}", addCoauthorDto.getWantMoreCoauthors());
        if (addCoauthorDto.getWantMoreCoauthors() == null || !addCoauthorDto.getWantMoreCoauthors()) {
            scientificPaper.setChosenCoauthors(true);
            this.scientificPaperService.save(scientificPaper);

            delegateTask.getExecution().setVariable("main_editor", scientificPaper.getMagazine().getMainEditor().getUsername());
            delegateTask.getExecution().setVariable("main_editor_email", scientificPaper.getMagazine().getMainEditor().getEmail());
        }

        final AddCoauthorResponseDto addCoauthorResponseDto = AddCoauthorResponseDto.builder()
                .scientificPaperId(scientificPaper.getId())
                .wantMoreCoauthors(addCoauthorDto.getWantMoreCoauthors())
                .build();

        this.simpMessagingTemplate.convertAndSend("/scientific_paper/added_coauthor", addCoauthorResponseDto);
    }
}