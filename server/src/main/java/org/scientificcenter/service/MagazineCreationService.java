package org.scientificcenter.service;

import org.scientificcenter.dto.*;

import java.util.List;

public interface MagazineCreationService {

    FormFieldsDto beginMagazineCreationProcess(String editor);

    void createMagazine(MagazineDto magazineDto, String processInstanceId);

    FormFieldsDto getSetMembershipPriceFormFields(String issn, String editor);

    void setMembershipPrice(MembershipPriceDto membershipPriceDto, String taskId);

    FormFieldsDto getChooseEditorsAndReviewersFormFields(String issn, String editor);

    void chooseEditorsAndReviewers(EditorsAndReviewersDto editorsAndReviewersDto, String taskId);

    List<TaskDto> getActiveCheckMagazineDataTasks();

    FormFieldsDto getCheckMagazineDataFormFields(String taskId);

    void checkMagazineData(CheckMagazineDto checkMagazineDto);

    FormFieldsDto getMagazineFormTaskFormFields(String issn, String editor);
}
