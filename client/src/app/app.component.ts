import { Component, OnInit, HostListener, ElementRef, OnDestroy } from '@angular/core';
import { Util } from './utils';
import { AuthenticationService } from './services/authentication/authentication.service';
import { Router } from '@angular/router';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';
import { ConfirmAutoLoggingInBottomSheetComponent } from './components/confirm-auto-logging-in-bottom-sheet/confirm-auto-logging-in-bottom-sheet.component';
import { MatBottomSheet, MatDialog } from '@angular/material';
import { ScientificPaperService } from './services/scientific-paper/scientific-paper.service';
import { ChooseMagazineDialogComponent } from './components/choose-magazine-dialog/choose-magazine-dialog.component';
import { EnterScientificPaperInfoDialogComponent } from './components/enter-scientific-paper-info-dialog/enter-scientific-paper-info-dialog.component';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  
  title = 'scientific-center';
  username: string;
  roles: string[];
  navBackground = false;
  stompClient: any;
  processInstanceId: string;
  
  constructor(private dialog: MatDialog,
    private bottomSheet: MatBottomSheet,
    private authenticationService: AuthenticationService,
    private scientificPaperService: ScientificPaperService,
    private util: Util,
    private router: Router) { 
      this.setupStompClient();
    }
  
  @HostListener('window:scroll') onScrollEvent(){
    this.navBackground = window.scrollY > 50 ? true : false;
  }

  ngOnInit() {
    this.username = this.authenticationService.getUsername();
    this.authenticationService.authSubject.subscribe(
      (data) => {
        if (data.key === this.authenticationService.usernameKey) {
          this.username = data.value;
        }
      }
    );
    this.roles = this.authenticationService.getRoles();
    this.authenticationService.authSubject.subscribe(
      (data) => {
        if (data.key === this.authenticationService.rolesKey) {
          this.roles = data.value;
        }
      }
    );
  }

  logout() {
    setTimeout(() => {
      this.router.navigateByUrl('/');
      this.authenticationService.removeUsername();
      this.authenticationService.removeAccessToken();
      this.authenticationService.removeRoles();
      this.util.showSnackBar('You have logged out successfully!', true);
    }, 500);
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

  setupStompClient() {
    const webSocket = new SockJS(environment.api);
    this.stompClient = Stomp.over(webSocket);
    const _this = this;
    this.stompClient.connect({}, frame => {
      _this.stompClient.subscribe("/registration/completed", 
        (message) => {
          const registrationSubProcessResponseDto: any = JSON.parse(message.body);
          const userState = registrationSubProcessResponseDto.userState;
          const bottomSheetRef = _this.bottomSheet.open(ConfirmAutoLoggingInBottomSheetComponent, { disableClose: true });
          bottomSheetRef.afterDismissed().subscribe(
            () => {
              _this.authenticationService.setUsername(userState.username);
              _this.authenticationService.setAccessToken(userState.token);
              _this.authenticationService.setRoles(userState.roles);

              _this.scientificPaperService.getChooseMagazineFormFields(registrationSubProcessResponseDto.superProcessInstanceId).subscribe(
                (formFieldsDto: any) => {

                  _this.authenticationService.setProcessInstanceId(formFieldsDto.processInstanceId);
                  _this.processInstanceId = formFieldsDto.processInstanceId;

                  const form = _this.util.createGenericForm(formFieldsDto.formFields);

                  const dialogRef = _this.dialog.open(ChooseMagazineDialogComponent, _this.createDialogPayload(formFieldsDto, form));
    
                  dialogRef.afterClosed().subscribe(
                    (successFlag: any) => {
                      if(successFlag) {
                        _this.util.showSnackBar('You have chosen magazine successfully!', true);

                        _this.scientificPaperService.getEnterScientificPaperInfoFormFields(_this.processInstanceId).subscribe(
                          (formFieldsDto: any) => {
                            const form = _this.util.createGenericForm(formFieldsDto.formFields);
                            const nextDialogRef = _this.dialog.open(EnterScientificPaperInfoDialogComponent, _this.createDialogPayload(formFieldsDto, form));
                            nextDialogRef.afterClosed().subscribe(
                              (successFlag: any) => {
                                if (successFlag) {
                                  _this.util.showSnackBar('You initially created scientific paper!', true);
                                }
                              }
                            );
                          },
                          (response: any) => {
                            if (response && response.error) {
                              _this.util.showSnackBar(response.error.message, false);
                            } else {
                              _this.util.showSnackBar('Unexpected error! Please, try again later', false);
                            }
                          }
                        );
                      }
                    }
                  );
                },
                (response: any) => {
                  if (response && response.error) {
                    _this.util.showSnackBar(response.error.message, false);
                  } else {
                    _this.util.showSnackBar('Unexpected error! Please, try again later', false);
                  }
                }
              );
            }
          );
        }
      );

      _this.stompClient.subscribe("/magazine/membership_payment", 
        (message) => {
          const orderResponseDto: any = JSON.parse(message.body);
          if (orderResponseDto.successFlag) {
            window.location.href = orderResponseDto.redirectionUrl;
          } else {
            _this.util.showSnackBar(orderResponseDto.message, false);
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
