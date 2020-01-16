package org.scientificcenter.service;

import org.scientificcenter.dto.*;

import java.util.List;

public interface RegistrationService {

    FormFieldsDto beginRegistrationProcess();

    void registerUser(RegistrationUserDto registrationUserDto, final String processInstanceId);

    FormFieldsDto getConfirmRegistrationFormFields(String processInstanceId);

    void confirmRegistration(AccountVerificationDto accountVerificationDto, String processInstanceId);

    List<TaskDto> getActiveCheckReviewerTasks();

    FormFieldsDto getCheckReviewerFormFields(String taskId);

    void checkReviewer(CheckReviewerDto checkReviewerDto);
}