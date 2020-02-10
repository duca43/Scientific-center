import { HttpStatus } from './../../utils';
import { AuthenticationService } from './../../services/authentication/authentication.service';
import { Component, Inject, OnDestroy } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { Util } from 'src/app/utils';
import { FileUploader } from 'ng2-file-upload';

@Component({
  selector: 'app-enter-scientific-paper-info-dialog',
  templateUrl: './enter-scientific-paper-info-dialog.component.html',
  styleUrls: ['./enter-scientific-paper-info-dialog.component.css']
})
export class EnterScientificPaperInfoDialogComponent implements OnDestroy {

  formFields: any;
  form: any;
  processInstanceId: string;
  stompClient: Stomp.Client;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<EnterScientificPaperInfoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private scientificPaperService: ScientificPaperService,
    private authenticationService: AuthenticationService,
    private util: Util) { 
      this.formFields = this.data.formFieldsDto.formFields;
      this.processInstanceId = this.data.formFieldsDto.processInstanceId;
      this.form = this.data.form;
      this.setupStompClient();
  }

  submit() {
    this.requestProcessing = true;
    const createScientificPaperDto = this.form.value;
    createScientificPaperDto.scientificArea = createScientificPaperDto.scientificArea.id; 
    this.formFields.forEach(formField => {
      if (formField.type.name === 'string_list') {
        createScientificPaperDto[formField.id] = formField.type.values;
      }
    });

    this.formFields.forEach(formField => {
      if (formField.type.name === 'file_upload') {
        const uploader: FileUploader = formField.uploader;
        const fileItem = uploader.queue[uploader.queue.length - 1];
        if (!fileItem) {
          this.requestProcessing = false;
          this.util.showSnackBar('No file attached! Please attach PDF document.', false);
          return;
        }
        fileItem.url = '/api/scientific_paper/upload/'.concat(this.authenticationService.getUsername());
        fileItem.upload();
        fileItem.onComplete = (response, status) => {
          if (status == HttpStatus.OK) {
            const responseDto = JSON.parse(response); 
            createScientificPaperDto.id = responseDto.id;
            createScientificPaperDto.pdfFile = responseDto.pdfFile;

            this.scientificPaperService.enterScientificPaperInfo(createScientificPaperDto, this.processInstanceId).subscribe(
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
          } else {
            this.util.showSnackBar('File could not be uploaded! Please, try again later', false);
          }
        }
        return;
      }
    });
  }

  setupStompClient() {
    const webSocket = new SockJS(environment.api);
    this.stompClient = Stomp.over(webSocket);
    const _this = this;
    this.stompClient.connect({}, frame => {
      _this.stompClient.subscribe("/scientific_paper/created", 
        (message) => {
          const validationDto: any = JSON.parse(message.body);
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
