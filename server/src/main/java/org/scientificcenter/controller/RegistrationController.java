package org.scientificcenter.controller;

import org.scientificcenter.dto.*;
import org.scientificcenter.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(final RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FormFieldsDto> beginRegistrationProcess() {
        return ResponseEntity.ok(this.registrationService.beginRegistrationProcess());
    }

    @PostMapping(value = "/{processInstanceId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public ResponseEntity<?> registerUser(@RequestBody final RegistrationUserDto registrationUserDto, @PathVariable final String processInstanceId) {
        this.registrationService.registerUser(registrationUserDto, processInstanceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/verify/{processInstanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FormFieldsDto> getConfirmRegistrationFormFields(@PathVariable final String processInstanceId) {
        return ResponseEntity.ok(this.registrationService.getConfirmRegistrationFormFields(processInstanceId));
    }

    @PostMapping(value = "/verify/{processInstanceId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public ResponseEntity<?> confirmRegistration(@RequestBody final AccountVerificationDto accountVerificationDto, @PathVariable final String processInstanceId) {
        this.registrationService.confirmRegistration(accountVerificationDto, processInstanceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/check_reviewer")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<TaskDto>> getActiveCheckReviewerTasks() {
        return ResponseEntity.ok(this.registrationService.getActiveCheckReviewerTasks());
    }

    @GetMapping(value = "/check_reviewer/{taskId}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<FormFieldsDto> getCheckReviewerFormFields(@PathVariable final String taskId) {
        return ResponseEntity.ok(this.registrationService.getCheckReviewerFormFields(taskId));
    }

    @PostMapping(value = "/check_reviewer")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> checkReviewer(@RequestBody final CheckReviewerDto checkReviewerDto) {
        this.registrationService.checkReviewer(checkReviewerDto);
        return ResponseEntity.ok().build();
    }
}