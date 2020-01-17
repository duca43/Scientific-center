package org.scientificcenter.service;

import org.scientificcenter.dto.*;

import java.util.List;

public interface MagazineCreationService {

    FormFieldsDto beginMagazineCreationProcess(String editor);

    void createMagazine(MagazineDto magazineDto, String processInstanceId);

    FormFieldsDto getChooseEditorsAndReviewersFormFields(String issn, String editor);

    void checkMagazineCreation(String processInstanceId);

    void chooseEditorsAndReviewers(EditorsAndReviewersDto editorsAndReviewersDto, String taskId);

    List<TaskDto> getActiveCheckMagazineDataTasks();

    FormFieldsDto getCheckMagazineDataFormFields(String taskId);

    void checkReviewer(CheckMagazineDto checkMagazineDto);

    FormFieldsDto getMagazineFormTaskFormFields(String issn, String editor);
}
