import { MagazineService } from './../../services/magazine/magazine.service';
import { Component, Inject, OnDestroy } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Util } from 'src/app/utils';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-new-magazine-dialog',
  templateUrl: './new-magazine-dialog.component.html',
  styleUrls: ['./new-magazine-dialog.component.css']
})
export class NewMagazineDialogComponent implements OnDestroy {

  processInstanceId: string;
  taskId: string;
  formFields: any[];
  title: string;
  form: FormGroup;
  stompClient: Stomp.Client;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<NewMagazineDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private magazineService: MagazineService,
    private util: Util) { 
      this.setupStompClient();
      this.processInstanceId = this.data.formFieldsDto.processInstanceId;
      this.taskId = this.data.formFieldsDto.taskId;
      this.formFields = this.data.formFieldsDto.formFields;
      this.title = this.data.title;
      this.form = this.data.form;
  }

  submit() {
    this.requestProcessing = true;

    const magazine = { 
      issn: this.form.value['issn'],
      name: this.form.value['name'],
      payment: this.form.value['payment'].id,
      scientificAreas: this.form.value['scientificAreas']
    };  

    this.magazineService.createMagazine(magazine, this.processInstanceId).subscribe(
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
      _this.stompClient.subscribe("/magazine/creation", 
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
