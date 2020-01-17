import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  authSubject = new Subject<{ key: string, value: any }>();
  tokenKey = 'userToken';
  processInstanceId ='processInstanceId';
  usernameKey = 'usernameKey';
  rolesKey = 'rolesKey';

  constructor(private http: HttpClient) { }

  setAccessToken(token: string) {
    localStorage.setItem(this.tokenKey, token);
    this.authSubject.next({ key: this.tokenKey, value: token });
  }

  removeAccessToken() {
    localStorage.removeItem(this.tokenKey);
    this.authSubject.next({ key: this.tokenKey, value: null });
  }

  getAccessToken(): string {
    return localStorage.getItem(this.tokenKey);
  }

  setProcessInstanceId(processInstanceId: string) {
    localStorage.setItem(this.processInstanceId, processInstanceId);
    this.authSubject.next({ key: this.processInstanceId, value: processInstanceId });
  }

  removeProcessInstanceId() {
    localStorage.removeItem(this.processInstanceId);
    this.authSubject.next({ key: this.processInstanceId, value: null });
  }

  getProcessInstanceId(): string {
    return localStorage.getItem(this.processInstanceId);
  }

  setUsername(username: string) {
    localStorage.setItem(this.usernameKey, username);
    this.authSubject.next({ key: this.usernameKey, value: username });
  }

  removeUsername() {
    localStorage.removeItem(this.usernameKey);
    this.authSubject.next({ key: this.usernameKey, value: null });
  }

  getUsername(): string {
    return localStorage.getItem(this.usernameKey);
  }

  setRoles(roles: []) {
    localStorage.setItem(this.rolesKey, JSON.stringify(roles));
    this.authSubject.next({ key: this.rolesKey, value: roles });
  }

  removeRoles() {
    localStorage.removeItem(this.rolesKey);
    this.authSubject.next({ key: this.rolesKey, value: null });
  }

  getRoles(): string[] {
    return JSON.parse(localStorage.getItem(this.rolesKey));
  }

  login(authenticationRequest: any) {
    return this.http.post('/api/login', authenticationRequest);
  }
}
