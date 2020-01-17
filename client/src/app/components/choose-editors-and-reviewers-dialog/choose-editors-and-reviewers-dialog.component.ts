import { MagazineService } from 'src/app/services/magazine/magazine.service';
import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-choose-editors-and-reviewers-dialog',
  templateUrl: './choose-editors-and-reviewers-dialog.component.html',
  styleUrls: ['./choose-editors-and-reviewers-dialog.component.css']
})
export class ChooseEditorsAndReviewersDialogComponent {

  editor: string;
  magazine: any;
  formFields: any;
  form: any;
  requestProcessing = false;
  taskId: any;

  constructor(private dialogRef: MatDialogRef<ChooseEditorsAndReviewersDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private magazineService: MagazineService,
    private util: Util) {
      console.dir(data);
      this.editor = this.data.editor;
      this.magazine = this.data.magazine;
      this.taskId = this.data.taskId;
      this.formFields = this.data.formFields;
      this.form = this.data.form;
  }

  submit() {
    this.requestProcessing = true;
    const editorsAndReviewersDto = {
      issn: this.magazine.issn,
      editors: this.form.value['editors'],
      reviewers: this.form.value['reviewers']
    };
    
    this.magazineService.chooseEditorsAndReviewers(editorsAndReviewersDto, this.taskId).subscribe(
      () => {
        this.dialogRef.close(true);
      },
      (response) => {
        this.util.showSnackBar(response.error.message, false);
        this.requestProcessing = false;
      }
    );
  }
}
