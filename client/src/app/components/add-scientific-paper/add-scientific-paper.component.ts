import { FormGroup } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { Util } from 'src/app/utils';
import { ChooseMagazineDialogComponent } from '../choose-magazine-dialog/choose-magazine-dialog.component';
import { RegistrationDialogComponent } from '../registration-dialog/registration-dialog.component';
import { EnterScientificPaperInfoDialogComponent } from '../enter-scientific-paper-info-dialog/enter-scientific-paper-info-dialog.component';

@Component({
  selector: 'app-add-scientific-paper',
  templateUrl: './add-scientific-paper.component.html',
  styleUrls: ['./add-scientific-paper.component.css']
})
export class AddScientificPaperComponent implements OnInit {

  user: string;
  processInstanceId: any;

  constructor(private dialog: MatDialog,
    private scientificPaperService: ScientificPaperService,
    private authenticationService: AuthenticationService,
    private util: Util) { }

  ngOnInit() {
    this.user = this.authenticationService.getUsername();
    this.authenticationService.authSubject.subscribe(
      (data) => {
        if (data.key === this.authenticationService.usernameKey) {
          this.user = data.value;
        }
      }
    );
  }

  addScientificPaper() {
    const basicUserInfoDto = {name: this.user};
    this.scientificPaperService.beginProcessingOfSubmittedTextProcess(basicUserInfoDto).subscribe(
      (beginProcessForSubmittingTextDto: any) => {

        const formFieldsDto = beginProcessForSubmittingTextDto.formFieldsDto;

        this.authenticationService.setProcessInstanceId(formFieldsDto.processInstanceId);
        this.processInstanceId = formFieldsDto.processInstanceId;

        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogPayload = this.createDialogPayload(formFieldsDto, form);

        if (!beginProcessForSubmittingTextDto.userExists) {
          this.authenticationService.setAccessToken(formFieldsDto.token);
        }

        const dialogRef = beginProcessForSubmittingTextDto.userExists ? this.dialog.open(ChooseMagazineDialogComponent, dialogPayload)
          : this.dialog.open(RegistrationDialogComponent, dialogPayload);

        dialogRef.afterClosed().subscribe(
          (successFlag: any) => {
            if(successFlag) {
              if (beginProcessForSubmittingTextDto.userExists) {
                this.util.showSnackBar('You have chosen magazine successfully!', true);
                
                this.scientificPaperService.getEnterScientificPaperInfoFormFields(this.processInstanceId).subscribe(
                  (formFieldsDto: any) => {
                    const form = this.util.createGenericForm(formFieldsDto.formFields);
                    const nextDialogRef = this.dialog.open(EnterScientificPaperInfoDialogComponent, this.createDialogPayload(formFieldsDto, form));
                    nextDialogRef.afterClosed().subscribe(
                      (successFlag: any) => {
                        if (successFlag) {
                          this.util.showSnackBar('You initially created scientific paper!', true);
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
              } else {
                this.util.showSnackBar('You have successfully registered, but it\'s not over yet. Open the email and confirm that it is you.', true);
              }
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

  createDialogPayload(formFieldsDto: any, form: FormGroup) {
    return {
      data: {
        'formFieldsDto': formFieldsDto,
        'form': form
      },
      disableClose: true,
      autoFocus: true,
      width: '50%'
    };
  }
}
