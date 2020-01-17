import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  constructor(private http: HttpClient) { }

  beginRegistrationProcess() {
    return this.http.get('/api/registration');
  }

  registerUser(user: any, processInstanceId: string) {
    return this.http.post('/api/registration/'.concat(processInstanceId), user);
  }

  getConfirmRegistrationFormFields(processInstanceId: string) {
    return this.http.get('/api/registration/verify/'.concat(processInstanceId));
  }

  confirmRegistration(accountVerification: any, processInstanceId: string) {
    return this.http.post('/api/registration/verify/'.concat(processInstanceId), accountVerification);
  }

  getActiveCheckReviewerTasks() {
    return this.http.get('/api/registration/check_reviewer');
  }

  getCheckReviewerFormFields(taskId: string) {
    return this.http.get('/api/registration/check_reviewer/'.concat(taskId));
  }

  checkReviewer(checkReviewerDto: any) {
    return this.http.post('/api/registration/check_reviewer', checkReviewerDto);
  }
}