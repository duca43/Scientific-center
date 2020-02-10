import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { Util } from 'src/app/utils';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-review-pdf-document-tasks-dialog',
  templateUrl: './review-pdf-document-tasks-dialog.component.html',
  styleUrls: ['./review-pdf-document-tasks-dialog.component.css']
})
export class ReviewPdfDocumentTasksDialogComponent {

  editor: string;
  taskId: string;
  formFields: any;
  form: FormGroup;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<ReviewPdfDocumentTasksDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private scientificPaperService: ScientificPaperService,
    private util: Util) {
      this.editor = this.data.editor;
      this.taskId = this.data.taskId;
      this.formFields = this.data.formFields;
      this.form = this.data.form;
  }

  submit() {
    this.requestProcessing = true;
    const reviewPdfDto = {
      editor: this.editor,
      taskId: this.taskId,
      comment: this.form.value['comment'],
      correctionTime: this.form.value['correctionTime'],
      isPaperFormattedWell: this.form.value['isPaperFormattedWell']
    };

    this.scientificPaperService.reviewPdfDocument(reviewPdfDto).subscribe(
      (formFieldsDto: any) => { 
        const response = {
          taskId: this.taskId,
          formFieldsDto: formFieldsDto
        }
        this.dialogRef.close(response);
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
  }
}
