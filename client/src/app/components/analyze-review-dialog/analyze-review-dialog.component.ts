import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-analyze-review-dialog',
  templateUrl: './analyze-review-dialog.component.html',
  styleUrls: ['./analyze-review-dialog.component.css']
})
export class AnalyzeReviewDialogComponent {

  taskId: string;
  formFields: any;
  form: FormGroup;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<AnalyzeReviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private scientificPaperService: ScientificPaperService,
    private util: Util) {
      this.taskId = this.data.taskId;
      this.formFields = this.data.formFields;
      this.form = this.data.form;
  }

  submit() {
    this.requestProcessing = true;

    this.scientificPaperService.analyzeReview(this.taskId).subscribe(
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
