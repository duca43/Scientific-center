package org.scientificcenter.controller;

import org.scientificcenter.dto.*;
import org.scientificcenter.service.MagazineCreationService;
import org.scientificcenter.service.MagazineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/magazine")
public class MagazineController {

    private final MagazineCreationService magazineCreationService;
    private final MagazineService magazineService;

    @Autowired
    public MagazineController(final MagazineCreationService magazineCreationService, final MagazineService magazineService) {
        this.magazineCreationService = magazineCreationService;
        this.magazineService = magazineService;
    }

    @GetMapping(value = "/{editor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<FormFieldsDto> beginMagazineCreationProcess(@PathVariable final String editor) {
        return ResponseEntity.ok(this.magazineCreationService.beginMagazineCreationProcess(editor));
    }

    @GetMapping(value = "/{issn}/{editor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<FormFieldsDto> getMagazineFormTaskFormFields(@PathVariable final String issn, @PathVariable final String editor) {
        return ResponseEntity.ok(this.magazineCreationService.getMagazineFormTaskFormFields(issn, editor));
    }

    @PostMapping(value = "/{processInstanceId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<?> createMagazine(@RequestBody final MagazineDto magazineDto, @PathVariable final String processInstanceId) {
        this.magazineCreationService.createMagazine(magazineDto, processInstanceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/check/{processInstanceId}")
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<?> checkMagazineCreation(@PathVariable final String processInstanceId) {
        this.magazineCreationService.checkMagazineCreation(processInstanceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/editor/{editor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<List<MagazineDto>> getMagazinesCreatedByEditor(@PathVariable final String editor) {
        return ResponseEntity.ok(this.magazineService.findByMainEditor(editor));
    }

    @GetMapping(value = "/choose_editors_and_reviewers/{issn}/{editor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<FormFieldsDto> getChooseEditorsAndReviewersFormFields(@PathVariable final String issn, @PathVariable final String editor) {
        return ResponseEntity.ok(this.magazineCreationService.getChooseEditorsAndReviewersFormFields(issn, editor));
    }

    @PostMapping(value = "/choose_editors_and_reviewers/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity<?> chooseEditorsAndReviewers(@RequestBody final EditorsAndReviewersDto editorsAndReviewersDto, @PathVariable final String taskId) {
        this.magazineCreationService.chooseEditorsAndReviewers(editorsAndReviewersDto, taskId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/check_data")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<TaskDto>> getActiveCheckMagazineDataTasks() {
        return ResponseEntity.ok(this.magazineCreationService.getActiveCheckMagazineDataTasks());
    }

    @GetMapping(value = "/check_data/{taskId}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<FormFieldsDto> getCheckMagazineDataFormFields(@PathVariable final String taskId) {
        return ResponseEntity.ok(this.magazineCreationService.getCheckMagazineDataFormFields(taskId));
    }

    @PostMapping(value = "/check_data")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> checkMagazineData(@RequestBody final CheckMagazineDto checkMagazineDto) {
        this.magazineCreationService.checkReviewer(checkMagazineDto);
        return ResponseEntity.ok().build();
    }
}