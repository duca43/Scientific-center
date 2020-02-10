import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-paper-review-by-reviewer-dialog',
  templateUrl: './paper-review-by-reviewer-dialog.component.html',
  styleUrls: ['./paper-review-by-reviewer-dialog.component.css']
})
export class PaperReviewByReviewerDialogComponent {

  title: string;
  reviewer: any;
  taskId: string;
  formFields: any;
  form: FormGroup;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<PaperReviewByReviewerDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private scientificPaperService: ScientificPaperService,
    private util: Util) {
      this.title = this.data.title;
      this.reviewer = this.data.reviewer;
      this.taskId = this.data.taskId;
      this.formFields = this.data.formFields;
      this.form = this.data.form;
  }

  submit() {
    this.requestProcessing = true;
    const paperReviewByReviewerDto = {
      reviewer: this.reviewer,
      commentAboutPaper: this.form.value['commentAboutPaper'],
      recommendation: this.form.value['recommendation'].id,
      commentForEditor: this.form.value['commentForEditor']
    };

    this.scientificPaperService.paperReviewByReviewer(paperReviewByReviewerDto, this.taskId).subscribe(
      () => { 
        this.dialogRef.close(this.taskId);
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
