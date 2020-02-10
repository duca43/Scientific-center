import { environment } from './../../../environments/environment';
import { Component, OnInit, Inject, AfterViewInit, OnDestroy } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { RegistrationService } from 'src/app/services/registration/registration.service';
import { Util } from 'src/app/utils';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

@Component({
  selector: 'app-registration-dialog',
  templateUrl: './registration-dialog.component.html',
  styleUrls: ['./registration-dialog.component.css']
})
export class RegistrationDialogComponent implements AfterViewInit, OnDestroy {

  processInstanceId: string;
  taskId: string;
  formFields: any[];
  form: FormGroup;
  stompClient: Stomp.Client;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<RegistrationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private registrationService: RegistrationService,
    private util: Util) {
      this.setupStompClient();
      this.processInstanceId = this.data.formFieldsDto.processInstanceId;
      this.taskId = this.data.formFieldsDto.taskId;
      this.formFields = this.data.formFieldsDto.formFields;
      this.form = this.data.form;
    }
    
  ngAfterViewInit() { 
    this.util.initLocations(this.formFields); 
  }

  submit() {
    this.requestProcessing = true;
    const user = this.form.value;
    
    this.formFields.forEach(formField => {
      if (formField.type.name === 'location') {
        user[formField.id] = formField.locationValue;
      }
    }); 

    this.registrationService.registerUser(user, this.processInstanceId).subscribe(
      () => { },
      (response: any) => {
        this.requestProcessing = false;
        if (response && response.error) {
          this.util.showSnackBar(response.error.message, false);
        } else {
          this.util.showSnackBar('Unexpected error! Please, try again later', false);
        }
      }
    ); 
  }

  setupStompClient() {
    const webSocket = new SockJS(environment.api);
    this.stompClient = Stomp.over(webSocket);
    const _this = this;
    this.stompClient.connect({}, frame => {
      _this.stompClient.subscribe("/registration/validation", 
        (message) => {
          const validationDto: any = JSON.parse(message.body);
          console.dir(validationDto);
          if (validationDto.valid) {
            _this.dialogRef.close(true);
          } else {
            _this.util.showSnackBar(validationDto.errorMessage, false);
            _this.requestProcessing = false;
          }
        }
      );
    });
  }

  ngOnDestroy(): void {
    this.stompClient.disconnect(() => {
      console.log('stomp client destroyed');
    });
  }
}
