import { ActivatedRoute } from '@angular/router';
import { NewMagazineDialogComponent } from './../new-magazine-dialog/new-magazine-dialog.component';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material';
import { MagazineService } from 'src/app/services/magazine/magazine.service';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { Util, Authority } from 'src/app/utils';
import { ChooseEditorsAndReviewersDialogComponent } from '../choose-editors-and-reviewers-dialog/choose-editors-and-reviewers-dialog.component';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';
import { SetMembershipPriceDialogComponent } from '../set-membership-price-dialog/set-membership-price-dialog.component';

@Component({
  selector: 'app-editor-magazines',
  templateUrl: './editor-magazines.component.html',
  styleUrls: ['./editor-magazines.component.css']
})
export class EditorMagazinesComponent implements OnInit, OnDestroy {

  magazines: any[];
  editor: string;
  processInstanceId: string;
  stompClient: any;

  constructor(private dialog: MatDialog,
    private magazineService: MagazineService,
    private authenticationService: AuthenticationService,
    private activatedRoute: ActivatedRoute,
    private util: Util) { 
      this.setupStompClient();
    }

  ngOnInit() {
    this.editor = this.authenticationService.getUsername();
    if (this.editor && this.authenticationService.getRoles().includes(Authority.EDITOR)) {
      this.magazineService.getMagazinesCreatedByEditor(this.editor).subscribe(
        (magazines: any[]) => {
          this.magazines = this.util.sortArray(magazines, 'name', true);

          const merchantId = this.activatedRoute.snapshot.paramMap.get('merchantId');
          if (merchantId) {
            const magazineCompleteRegistrationDto = {merchantId: merchantId, editorUsername: this.editor};
            this.magazineService.completeMagazineRegistration(magazineCompleteRegistrationDto).subscribe(
              (registrationCompleteDto: any) => {
                if (registrationCompleteDto) {
                  if (registrationCompleteDto.flag) {
                    this.magazines.forEach(magazine => {
                      if (magazine.merchantId === merchantId) {
                        magazine.enabledAsMerchant = true;
                      }
                    });
                  }
                  this.util.showSnackBar(registrationCompleteDto.message, registrationCompleteDto.flag);
                } else {
                  this.util.showSnackBar('Unexpected error! Please, try again later', false);
                }
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

  getMagazines() {
    this.magazineService.getMagazinesCreatedByEditor(this.editor).subscribe(
      (magazines: any[]) => {
        this.magazines = this.util.sortArray(magazines, 'name', true);
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

  openNewMagazineDialog() {
    this.magazineService.beginMagazineCreationProcess(this.authenticationService.getUsername()).subscribe(
      (formFieldsDto: any) => {

        this.authenticationService.setProcessInstanceId(formFieldsDto.processInstanceId);
        this.processInstanceId = formFieldsDto.processInstanceId;

        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogRef = this.dialog.open(NewMagazineDialogComponent,
        {
          data: {
            'formFieldsDto': formFieldsDto,
            'title': 'Please complete magazine creation form',
            'form': form
          },
          disableClose: true,
          autoFocus: true,
          width: '50%'
        });
        dialogRef.afterClosed().subscribe(
          (successFlag: any) => {
            if(successFlag) {
              this.getMagazines();
              this.util.showSnackBar('You have successfully created new magazine! But, it\' not active yet, because you have to set membership price and/or assign editors and reviewers.', true);
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

  openChooseEditorsAndReviewersDialog(magazine: any) {
    this.magazineService.getChooseEditorsAndReviewersFormFields(magazine.issn, this.authenticationService.getUsername()).subscribe(
      (formFieldsDto: any) => {

        let invalidFlag = false;
        formFieldsDto.formFields.forEach(formField => {
          if (formField.id == 'reviewers' && Object.keys(formField.type.values).length < 2) {
            invalidFlag = true;
          }
        });
        
        if (invalidFlag) {
          this.util.showSnackBar('Error! There are no enough reviewers for specific scientific areas! Please, try again later.', false);          
          return;
        }
        
        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogRef = this.dialog.open(ChooseEditorsAndReviewersDialogComponent,
        {
          data: {
            'editor': this.authenticationService.getUsername(),
            'magazine': magazine,
            'taskId': formFieldsDto.taskId,
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
              this.getMagazines();
              this.util.showSnackBar('You have chosen editors and reviewers successfully! Wait for administrator to approve magazine or request for changes.', true);
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

  openEditMagazineDialog(magazine: any) {
    this.magazineService.getMagazineFormTaskFormFields(magazine.issn, this.authenticationService.getUsername()).subscribe(
      (formFieldsDto: any) => {

        this.authenticationService.setProcessInstanceId(formFieldsDto.processInstanceId);
        this.processInstanceId = formFieldsDto.processInstanceId;

        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogRef = this.dialog.open(NewMagazineDialogComponent,
        {
          data: {
            'formFieldsDto': formFieldsDto,
            'title': 'Please update magazine named '.concat(magazine.name),
            'form': form
          },
          disableClose: true,
          autoFocus: true,
          width: '50%'
        });
        dialogRef.afterClosed().subscribe(
          (successFlag: any) => {
            if(successFlag) {
              this.getMagazines();
              this.util.showSnackBar('You have successfully updated magazine! But, it\' not active yet, because you have to assign editors and reviewers.', true);
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

  registerMagazineAsMerchant(magazine: any) {
    const magazineRegistrationDto = {magazineId: magazine.id, editorUsername: this.editor};
    this.magazineService.registerMagazineAsMerchant(magazineRegistrationDto).subscribe(
      (redirectionResponse: any) => {
        if (redirectionResponse) {
          window.location.href = redirectionResponse.redirectionUrl;
        } else {
          this.util.showSnackBar('Unexpected error! Please, try again later', false);
        }
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

  openSetMembershipPriceDialog(magazine: any) {
    this.magazineService.getSetMembershipPriceFormFields(magazine.issn, this.authenticationService.getUsername()).subscribe(
      (formFieldsDto: any) => {
        
        const form = this.util.createGenericForm(formFieldsDto.formFields);

        this.dialog.open(SetMembershipPriceDialogComponent,
        {
          data: {
            'editor': this.authenticationService.getUsername(),
            'magazine': magazine,
            'taskId': formFieldsDto.taskId,
            'formFields': formFieldsDto.formFields,
            'form': form
          },
          disableClose: true,
          autoFocus: true,
          width: '50%'
        });
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

  setupStompClient() {
    const webSocket = new SockJS(environment.api);
    this.stompClient = Stomp.over(webSocket);
    const _this = this;
    this.stompClient.connect({}, frame => {
      _this.stompClient.subscribe("/magazine/status", 
        (message) => {
          const statusMessage: string = message.body;
          _this.getMagazines();
          _this.util.showSnackBar(statusMessage, true);
        }
      );

      _this.stompClient.subscribe("/magazine/membership_price", 
        (message) => {
          const completionMessage: string = message.body;
          _this.getMagazines();
          _this.util.showSnackBar(completionMessage, true);
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
