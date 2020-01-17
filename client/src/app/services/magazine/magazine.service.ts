import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MagazineService {

  constructor(private http: HttpClient) { }

  beginMagazineCreationProcess(editor: string) {
    return this.http.get('/api/magazine/'.concat(editor));
  }

  getMagazineFormTaskFormFields(issn: string, editor: string) {
    return this.http.get('/api/magazine/'.concat(issn).concat('/').concat(editor));
  }

  createMagazine(magazine: any, taskId: string) {
    return this.http.post('/api/magazine/'.concat(taskId), magazine);
  }

  checkMagazineCreation(processInstanceId: string) {
    return this.http.get('/api/magazine/check/'.concat(processInstanceId));
  }

  getMagazinesCreatedByEditor(editor: string) {
    return this.http.get('/api/magazine/editor/'.concat(editor));
  }

  getChooseEditorsAndReviewersFormFields(issn: string, editor: string) {
    return this.http.get('/api/magazine/choose_editors_and_reviewers/'.concat(issn).concat('/').concat(editor));
  }
  
  chooseEditorsAndReviewers(editorsAndReviewersDto: any, taskId: string) {
    return this.http.post('/api/magazine/choose_editors_and_reviewers/'.concat(taskId), editorsAndReviewersDto);
  }

  getActiveCheckMagazineDataTasks() {
    return this.http.get('/api/magazine/check_data');
  }

  getCheckMagazineDataFormFields(taskId: string) {
    return this.http.get('/api/magazine/check_data/'.concat(taskId));
  }

  checkMagazineData(checkMagazineDto: any) {
    return this.http.post('/api/magazine/check_data', checkMagazineDto);
  }
}
