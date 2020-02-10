package org.scientificcenter.service;

import org.scientificcenter.dto.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProcessingOfSubmittedTextService {

    BeginProcessForSubmittingTextDto beginProcessingOfSubmittedTextProcess(BasicUserInfoDto authorInfo);

    FormFieldsDto getChooseMagazineFormFields(String processInstanceId);

    void chooseMagazine(ChooseMagazineDto chooseMagazineDto);

    FormFieldsDto getEnterScientificPaperInfoFormFields(String processInstanceId);

    void enterScientificPaperInfo(CreateScientificPaperDto createScientificPaperDto, String processInstanceId);

    CreateScientificPaperDto uploadScientificPaper(MultipartFile file, String author);

    CreateScientificPaperDto reUploadScientificPaper(MultipartFile file, String author, Long scientificPaperId);

    Resource downloadScientificPaper(String author, Long scientificPaperId);

    FormFieldsDto getAddCoauthorsFormFields(Long scientificPaperId);

    void addCoauthor(AddCoauthorDto addCoauthorDto, String processInstanceId);

    List<TaskDto> getActiveReviewPaperTasks(String mainEditor);

    FormFieldsDto getTaskFormFields(String taskId);

    void reviewPaper(ReviewPaperDto reviewPaperDto);

    List<TaskDto> getActiveReviewPdfDocumentTasks(String mainEditor);

    FormFieldsDto reviewPdfDocument(ReviewPdfDto reviewPdfDto);

    FormFieldsDto getCorrectPaperFormFields(Long scientificPaperId);

    void correctPaper(CorrectScientificPaperDto correctScientificPaperDto);

    List<TaskDto> getActiveChooseReviewersTasks(String chosenEditor);

    FormFieldsDto getChooseReviewersTaskFormFields(String taskId);

    void chooseReviewers(ChooseReviewersDto chooseReviewersDto);

    List<TaskDto> getActivePaperReviewByReviewerTasks(String reviewer);

    FormFieldsDto getPaperReviewByReviewerTaskFormFields(String taskId);

    void paperReviewByReviewer(PaperReviewByReviewerDto paperReviewByReviewerDto, String taskId);

    List<TaskDto> getActiveAnalyzeReviewTasks(String chosenEditor);

    FormFieldsDto getAnalyzeReviewTaskFormFields(String taskId);

    FormFieldsDto analyzeReview(String taskId);

    FormFieldsDto getMakeADecisionFormFields(String processInstanceId);

    void makeADecision(MakeADecisionDto makeADecisionDto);

    void makeFinalDecision(MakeFinalDecisionDto makeFinalDecisionDto);
}