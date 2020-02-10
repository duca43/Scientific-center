import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material";
import { RegistrationService } from "src/app/services/registration/registration.service";
import { Util } from "src/app/utils";
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { ActivatedRoute, Router } from '@angular/router';
import { VerifyAccountDialogComponent } from '../verify-account-dialog/verify-account-dialog.component';

@Component({
  selector: "app-verify-account",
  templateUrl: "./verify-account.component.html",
  styleUrls: ["./verify-account.component.css"]
})
export class VerifyAccountComponent implements OnInit {
  
  errorMessage = 'Error! Your account is perhaps verified already or there are no account with the given username.';
  username: any;

  constructor(private dialog: MatDialog,
    private registrationService: RegistrationService,
    private authenticationService: AuthenticationService,
    private util: Util,
    private router: ActivatedRoute,
    private route: Router) { }

  ngOnInit() {
    const processInstanceId = this.authenticationService.getProcessInstanceId();
    if (!processInstanceId) {
      this.util.showSnackBar(this.errorMessage, false);
      return;
    }
    this.router.queryParams.subscribe(
      params => {
        this.username = params['user'];
      }
    );
    if (!this.username) {
      this.util.showSnackBar(this.errorMessage, false);
      return;
    }
    this.registrationService.getConfirmRegistrationFormFields(processInstanceId).subscribe(
      (formFieldsDto: any) => {
        
        this.authenticationService.setAccessToken(formFieldsDto.token);

        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogRef = this.dialog.open(VerifyAccountDialogComponent,
        {
          data: {
            'username': this.username,
            'processInstanceId': processInstanceId,
            'formFields': formFieldsDto.formFields,
            'form': form
          },
          disableClose: true,
          autoFocus: true,
          width: '50%'
        });
        dialogRef.afterClosed().subscribe(
          (successFlag: any) => {
            if(successFlag) {
              this.route.navigateByUrl('/');
              this.util.showSnackBar('You have verified your account successfully!', true);
            }
          }
        );
      },
      (response: any) => {
        if (response && response.error) {
          this.util.showSnackBar(response.error.message, false);
        } else {
          this.util.showSnackBar('Unexpected error! Please, try again later', false);
        }
      }
    );
  }
}
