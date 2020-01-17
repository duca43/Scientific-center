import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { RegistrationService } from 'src/app/services/registration/registration.service';
import { Util } from 'src/app/utils';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';

@Component({
  selector: 'app-check-reviewer-dialog',
  templateUrl: './check-reviewer-dialog.component.html',
  styleUrls: ['./check-reviewer-dialog.component.css']
})
export class CheckReviewerDialogComponent {
  
  taskId: any;
  formFields: any;
  form: any;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<CheckReviewerDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private registrationService: RegistrationService,
    private authenticationService: AuthenticationService,
    private util: Util) {
      this.taskId = this.data.taskId;
      this.formFields = this.data.formFields;
      this.form = this.data.form;
  }

  submit() {
    this.requestProcessing = true;
    const checkReviewerDto = {};
    checkReviewerDto['taskId'] = this.taskId;
    checkReviewerDto['admin'] = this.authenticationService.getUsername();
    checkReviewerDto['acceptReviewer'] = this.form.get('acceptReviewer').value;

    this.registrationService.checkReviewer(checkReviewerDto).subscribe(
      () => {
        this.dialogRef.close(true);
      },
      (response) => {
        this.util.showSnackBar(response.error.message, false);
        this.requestProcessing = false;
      }
    );
  }
}
