import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { RegistrationService } from 'src/app/services/registration/registration.service';
import { Util } from 'src/app/utils';
import *  as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-verify-account-dialog',
  templateUrl: './verify-account-dialog.component.html',
  styleUrls: ['./verify-account-dialog.component.css']
})
export class VerifyAccountDialogComponent {
  
  username: any;
  processInstanceId: any;
  formFields: any;
  form: any;
  stompClient: Stomp.Client;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<VerifyAccountDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private registrationService: RegistrationService,
    private util: Util) {
      this.setupStompClient();
      this.username = this.data.username;
      this.processInstanceId = this.data.processInstanceId;
      this.formFields = this.data.formFields;
      this.form = this.data.form;
  }

    submit() {
      this.requestProcessing = true;
      const accountVerification = this.form.value;
      accountVerification['username'] = this.username;

      this.registrationService.confirmRegistration(accountVerification, this.processInstanceId).subscribe(
        () => { },
        (response) => {
          this.util.showSnackBar(response.error.message, false);
          this.requestProcessing = false;
        }
      );
    }

    setupStompClient() {
      const webSocket = new SockJS(environment.api);
      this.stompClient = Stomp.over(webSocket);
      const _this = this;
      this.stompClient.connect({}, frame => {
        _this.stompClient.subscribe("/registration/verification", 
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
}
