import { Component, OnDestroy, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { Util, HttpStatus } from 'src/app/utils';
import { FileUploader } from 'ng2-file-upload';

@Component({
  selector: 'app-correct-paper-dialog',
  templateUrl: './correct-paper-dialog.component.html',
  styleUrls: ['./correct-paper-dialog.component.css']
})
export class CorrectPaperDialogComponent {

  formFields: any;
  form: any;
  processInstanceId: string;
  scientificPaperId: any;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<CorrectPaperDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private scientificPaperService: ScientificPaperService,
    private authenticationService: AuthenticationService,
    private util: Util) { 
      this.formFields = this.data.formFieldsDto.formFields;
      this.processInstanceId = this.data.formFieldsDto.processInstanceId;
      this.form = this.data.form;
      this.scientificPaperId = this.data.scientificPaperId;
  }

  submit() {
    this.requestProcessing = true;
    const correctScientificPaperDto: any = {
       processInstanceId: this.processInstanceId,
       authorReply: this.form.value['authorReply']
    };

    this.formFields.forEach(formField => {
      if (formField.type.name === 'file_upload') {
        const uploader: FileUploader = formField.uploader;
        const fileItem = uploader.queue[uploader.queue.length - 1];
        if (!fileItem) {
          this.requestProcessing = false;
          this.util.showSnackBar('No file attached! Please attach PDF document.', false);
          return;
        }
        fileItem.url = '/api/scientific_paper/re_upload/'.concat(this.authenticationService.getUsername()).concat('/').concat(this.scientificPaperId);
        fileItem.upload();
        fileItem.onComplete = (response, status) => {
          if (status == HttpStatus.OK) {
            const responseDto = JSON.parse(response); 
            correctScientificPaperDto.pdfFile = responseDto.pdfFile;

            this.scientificPaperService.correctPaper(correctScientificPaperDto).subscribe(
              () => { 
                this.dialogRef.close(true);
              },
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
}
