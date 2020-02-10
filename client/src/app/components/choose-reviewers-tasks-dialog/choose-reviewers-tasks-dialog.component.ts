import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-choose-reviewers-tasks-dialog',
  templateUrl: './choose-reviewers-tasks-dialog.component.html',
  styleUrls: ['./choose-reviewers-tasks-dialog.component.css']
})
export class ChooseReviewersTasksDialogComponent {

  title: string;
  taskId: string;
  formFields: any;
  form: FormGroup;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<ChooseReviewersTasksDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private scientificPaperService: ScientificPaperService,
    private util: Util) {
      this.title = this.data.title;
      this.taskId = this.data.taskId;
      this.formFields = this.data.formFields;
      this.form = this.data.form;
  }

  submit() {
    this.requestProcessing = true;
    const chooseReviewersDto = {
      taskId: this.taskId,
      reviewers: this.form.value['reviewers'],
      reviewsDeadline: this.form.value['reviewsDeadline']
    };

    this.scientificPaperService.chooseReviewers(chooseReviewersDto).subscribe(
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
