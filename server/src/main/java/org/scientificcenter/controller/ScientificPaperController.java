package org.scientificcenter.controller;

import org.scientificcenter.dto.*;
import org.scientificcenter.service.ProcessingOfSubmittedTextService;
import org.scientificcenter.service.ScientificPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping(value = "/api/scientific_paper")
public class ScientificPaperController {

    private final ProcessingOfSubmittedTextService processingOfSubmittedTextService;
    private final ScientificPaperService scientificPaperService;

    @Autowired
    public ScientificPaperController(final ProcessingOfSubmittedTextService processingOfSubmittedTextService, final ScientificPaperService scientificPaperService) {
        this.processingOfSubmittedTextService = processingOfSubmittedTextService;
        this.scientificPaperService = scientificPaperService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BeginProcessForSubmittingTextDto> beginProcessingOfSubmittedTextProcess(@RequestBody final BasicUserInfoDto authorInfo) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.beginProcessingOfSubmittedTextProcess(authorInfo));
    }

    @GetMapping(value = "/choose_magazine/{processInstanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<FormFieldsDto> getChooseMagazineFormFields(@PathVariable final String processInstanceId) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getChooseMagazineFormFields(processInstanceId));
    }

    @PostMapping(value = "/choose_magazine", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> chooseMagazine(@RequestBody final ChooseMagazineDto chooseMagazineDto) {
        this.processingOfSubmittedTextService.chooseMagazine(chooseMagazineDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/enter_info/{processInstanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<FormFieldsDto> getEnterScientificPaperInfoFormFields(@PathVariable final String processInstanceId) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getEnterScientificPaperInfoFormFields(processInstanceId));
    }

    @PostMapping(value = "/enter_info/{processInstanceId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> enterScientificPaperInfo(@RequestBody final CreateScientificPaperDto createScientificPaperDto, @PathVariable final String processInstanceId) {
        this.processingOfSubmittedTextService.enterScientificPaperInfo(createScientificPaperDto, processInstanceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/upload/{author}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreateScientificPaperDto> upload(@RequestParam("file") final MultipartFile file, @PathVariable final String author) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.uploadScientificPaper(file, author));
    }

    @PostMapping(value = "/re_upload/{author}/{scientificPaperId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreateScientificPaperDto> reUpload(@RequestParam("file") final MultipartFile file, @PathVariable final String author, @PathVariable final Long scientificPaperId) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.reUploadScientificPaper(file, author, scientificPaperId));
    }

    @GetMapping("/download/{author}/{scientificPaperId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> download(@PathVariable final String author, @PathVariable final Long scientificPaperId) {
        final Resource resource = this.processingOfSubmittedTextService.downloadScientificPaper(author, scientificPaperId);

        if (resource == null || resource.getFilename() == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8))
                .body(resource);
    }

    @GetMapping(value = "/{author}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<ScientificPaperDto>> findAllByAuthor(@PathVariable final String author) {
        return ResponseEntity.ok(this.scientificPaperService.findAllByAuthor(author));
    }

    @GetMapping(value = "/add_coauthor/{scientificPaperId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<FormFieldsDto> getAddCoauthorsFormFields(@PathVariable final Long scientificPaperId) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getAddCoauthorsFormFields(scientificPaperId));
    }

    @PostMapping(value = "/add_coauthor/{processInstanceId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addCoauthor(@RequestBody final AddCoauthorDto addCoauthorDto, @PathVariable final String processInstanceId) {
        this.processingOfSubmittedTextService.addCoauthor(addCoauthorDto, processInstanceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/task/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<FormFieldsDto> getTaskFormFields(@PathVariable final String taskId) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getTaskFormFields(taskId));
    }

    @GetMapping(value = "/review_paper/{mainEditor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<List<TaskDto>> getActiveReviewPaperTasks(@PathVariable final String mainEditor) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getActiveReviewPaperTasks(mainEditor));
    }

    @PostMapping(value = "/review_paper", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<?> reviewPaper(@RequestBody final ReviewPaperDto reviewPaperDto) {
        this.processingOfSubmittedTextService.reviewPaper(reviewPaperDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/review_pdf/{mainEditor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<List<TaskDto>> getActiveReviewPdfDocumentTasks(@PathVariable final String mainEditor) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getActiveReviewPdfDocumentTasks(mainEditor));
    }

    @PostMapping(value = "/review_pdf", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<FormFieldsDto> reviewPdfDocument(@RequestBody final ReviewPdfDto reviewPdfDto) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.reviewPdfDocument(reviewPdfDto));
    }

    @GetMapping(value = "/correct_paper/{scientificPaperId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<FormFieldsDto> getCorrectPaperFormFields(@PathVariable final Long scientificPaperId) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getCorrectPaperFormFields(scientificPaperId));
    }

    @PostMapping(value = "/correct_paper", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> correctPaper(@RequestBody final CorrectScientificPaperDto correctScientificPaperDto) {
        this.processingOfSubmittedTextService.correctPaper(correctScientificPaperDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/choose_reviewers/all/{chosenEditor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<List<TaskDto>> getActiveChooseReviewersTasks(@PathVariable final String chosenEditor) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getActiveChooseReviewersTasks(chosenEditor));
    }

    @GetMapping(value = "/choose_reviewers/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<FormFieldsDto> getChooseReviewersTaskFormFields(@PathVariable final String taskId) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getChooseReviewersTaskFormFields(taskId));
    }

    @PostMapping(value = "/choose_reviewers", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<?> chooseReviewers(@RequestBody final ChooseReviewersDto chooseReviewersDto) {
        this.processingOfSubmittedTextService.chooseReviewers(chooseReviewersDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/paper_review_by_reviewer/all/{reviewer}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_EDITOR', 'ROLE_REVIEWER')")
    public ResponseEntity<List<TaskDto>> getActivePaperReviewByReviewerTasks(@PathVariable final String reviewer) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getActivePaperReviewByReviewerTasks(reviewer));
    }

    @GetMapping(value = "/paper_review_by_reviewer/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_EDITOR', 'ROLE_REVIEWER')")
    public ResponseEntity<FormFieldsDto> getPaperReviewByReviewerTaskFormFields(@PathVariable final String taskId) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getPaperReviewByReviewerTaskFormFields(taskId));
    }

    @PostMapping(value = "/paper_review_by_reviewer/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_EDITOR', 'ROLE_REVIEWER')")
    public ResponseEntity<?> paperReviewByReviewer(@RequestBody final PaperReviewByReviewerDto paperReviewByReviewerDto, @PathVariable final String taskId) {
        this.processingOfSubmittedTextService.paperReviewByReviewer(paperReviewByReviewerDto, taskId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/analyze_review/all/{chosenEditor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<List<TaskDto>> getActiveAnalyzeReviewTasks(@PathVariable final String chosenEditor) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getActiveAnalyzeReviewTasks(chosenEditor));
    }

    @GetMapping(value = "/analyze_review/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<FormFieldsDto> getAnalyzeReviewTaskFormFields(@PathVariable final String taskId) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getAnalyzeReviewTaskFormFields(taskId));
    }

    @PostMapping(value = "/analyze_review", consumes = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<FormFieldsDto> analyzeReview(@RequestBody final String taskId) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.analyzeReview(taskId));
    }

    @GetMapping(value = "/make_decision/{processInstanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<FormFieldsDto> getMakeADecisionFormFields(@PathVariable final String processInstanceId) {
        return ResponseEntity.ok(this.processingOfSubmittedTextService.getMakeADecisionFormFields(processInstanceId));
    }

    @PostMapping(value = "/make_decision", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<FormFieldsDto> makeADecision(@RequestBody final MakeADecisionDto makeADecisionDto) {
        this.processingOfSubmittedTextService.makeADecision(makeADecisionDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/make_final_decision", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<FormFieldsDto> makeFinalDecision(@RequestBody final MakeFinalDecisionDto makeFinalDecisionDto) {
        this.processingOfSubmittedTextService.makeFinalDecision(makeFinalDecisionDto);
        return ResponseEntity.ok().build();
    }
}