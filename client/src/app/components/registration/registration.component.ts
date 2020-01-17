import { RegistrationDialogComponent } from './../registration-dialog/registration-dialog.component';
import { Component, AfterViewInit, ViewChild, Inject} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Util } from '../../utils';
import { RegistrationService } from 'src/app/services/registration/registration.service';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})

export class RegistrationComponent {

  constructor(private dialog: MatDialog,
    private registrationService: RegistrationService,
    private authenticationService: AuthenticationService,
    private util: Util) { }
 
  openRegistrationDialog() {
    this.registrationService.beginRegistrationProcess().subscribe(
      (formFieldsDto: any) => {

        this.authenticationService.setAccessToken(formFieldsDto.token);
        this.authenticationService.setProcessInstanceId(formFieldsDto.processInstanceId);

        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogRef = this.dialog.open(RegistrationDialogComponent,
        {
          data: {
            'formFieldsDto': formFieldsDto,
            'form': form
          },
          disableClose: true,
          autoFocus: true,
          width: '50%'
        });
        dialogRef.afterClosed().subscribe(
          (successFlag: any) => {
            if(successFlag) {
              this.util.showSnackBar('You have successfully registered, but it\'s not over yet. Open the email and confirm that it is you.', true);
            }
          }
        );
      },
      () => {
        this.util.showSnackBar('Error while creating registration form. Please try again later.', false);
      }
    );
  }
}
