import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ScientificPaperService {

  constructor(private http: HttpClient) { }

  beginProcessingOfSubmittedTextProcess(basicUserInfoDto: any) {
    return this.http.post('/api/scientific_paper', basicUserInfoDto);
  }

  getChooseMagazineFormFields(processInstanceId: string) {
    return this.http.get('/api/scientific_paper/choose_magazine/'.concat(processInstanceId));
  }

  chooseMagazine(chooseMagazineDto: any) {
    return this.http.post('/api/scientific_paper/choose_magazine', chooseMagazineDto);
  }

  getEnterScientificPaperInfoFormFields(processInstanceId: string) {
    return this.http.get('/api/scientific_paper/enter_info/'.concat(processInstanceId));
  }

  enterScientificPaperInfo(createScientificPaperDto: any, processInstanceId: string) {
    return this.http.post('/api/scientific_paper/enter_info/'.concat(processInstanceId), createScientificPaperDto);
  }

  download(author: string, scientificPaperId: number) {
    let headers = new HttpHeaders();
    headers = headers.set('Accept', 'application/pdf');
    return this.http.get('/api/scientific_paper/download/'.concat(author).concat('/').concat(scientificPaperId.toString()), {
        headers: headers,
        responseType: 'blob'
    });
  }

  findAllByAuthor(author: string) {
    return this.http.get('/api/scientific_paper/'.concat(author));
  }

  getAddCoauthorsFormFields(processInstanceId: string) {
    return this.http.get('/api/scientific_paper/add_coauthor/'.concat(processInstanceId));
  }

  addCoauthor(addCoauthorDto: any, processInstanceId: string) {
    return this.http.post('/api/scientific_paper/add_coauthor/'.concat(processInstanceId), addCoauthorDto);
  }

  getTaskFormFields(taskId: string) {
    return this.http.get('/api/scientific_paper/task/'.concat(taskId));
  }

  getActiveReviewPaperTasks(mainEditor: string) {
    return this.http.get('/api/scientific_paper/review_paper/'.concat(mainEditor));
  }

  reviewPaper(reviewPaperDto: any) {
    return this.http.post('/api/scientific_paper/review_paper', reviewPaperDto);
  }

  getActiveReviewPdfDocumentTasks(mainEditor: string) {
    return this.http.get('/api/scientific_paper/review_pdf/'.concat(mainEditor));
  }

  reviewPdfDocument(reviewPdfDto: any) {
    return this.http.post('/api/scientific_paper/review_pdf', reviewPdfDto);
  }

  getCorrectPaperFormFields(scientificPaperId: number) {
    return this.http.get('/api/scientific_paper/correct_paper/'.concat(scientificPaperId.toString()));
  }

  correctPaper(correctScientificPaperDto: any) {
    return this.http.post('/api/scientific_paper/correct_paper', correctScientificPaperDto);
  }
  
  getActiveChooseReviewersTasks(chosenEditor: string) {
    return this.http.get('/api/scientific_paper/choose_reviewers/all/'.concat(chosenEditor));
  }

  getChooseReviewersTaskFormFields(taskId: string) {
    return this.http.get('/api/scientific_paper/choose_reviewers/'.concat(taskId));
  }

  chooseReviewers(chooseReviewersDto: any) {
    return this.http.post('/api/scientific_paper/choose_reviewers', chooseReviewersDto);
  }

  getActivePaperReviewByReviewerTasks(reviewer: string) {
    return this.http.get('/api/scientific_paper/paper_review_by_reviewer/all/'.concat(reviewer));
  }

  getPaperReviewByReviewerTaskFormFields(taskId: string) {
    return this.http.get('/api/scientific_paper/paper_review_by_reviewer/'.concat(taskId));
  }

  paperReviewByReviewer(paperReviewByReviewerDto: any, taskId: string) {
    return this.http.post('/api/scientific_paper/paper_review_by_reviewer/'.concat(taskId), paperReviewByReviewerDto);
  }

  getActiveAnalyzeReviewTasks(chosenEditor: string) {
    return this.http.get('/api/scientific_paper/analyze_review/all/'.concat(chosenEditor));
  }

  getAnalyzeReviewTaskFormFields(taskId: string) {
    return this.http.get('/api/scientific_paper/analyze_review/'.concat(taskId));
  }

  analyzeReview(taskId: string) {
    return this.http.post('/api/scientific_paper/analyze_review', taskId);
  }

  getMakeADecisionFormFields(processInstanceId: string) {
    return this.http.get('/api/scientific_paper/make_decision/'.concat(processInstanceId));
  }

  makeADecision(makeADecisionDto: any) {
    return this.http.post('/api/scientific_paper/make_decision', makeADecisionDto);
  }

  makeFinalDecision(makeFinalDecisionDto: any) {
    return this.http.post('/api/scientific_paper/make_final_decision', makeFinalDecisionDto);
  }
}
