import { FormGroup } from '@angular/forms';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Util } from 'src/app/utils';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-review-paper-dialog',
  templateUrl: './review-paper-dialog.component.html',
  styleUrls: ['./review-paper-dialog.component.css']
})
export class ReviewPaperDialogComponent implements OnDestroy {

  editor: string;
  taskId: string;
  formFields: any;
  form: FormGroup;
  stompClient: Stomp.Client;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<ReviewPaperDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private scientificPaperService: ScientificPaperService,
    private util: Util) {
      this.editor = this.data.editor;
      this.taskId = this.data.taskId;
      this.formFields = this.data.formFields;
      this.form = this.data.form;
      this.setupStompClient();
  }

  submit() {
    this.requestProcessing = true;
    const reviewPaperDto = {
      editor: this.editor,
      taskId: this.taskId,
      approveScientificPaper: this.form.value['approveScientificPaper']
    };

    this.scientificPaperService.reviewPaper(reviewPaperDto).subscribe(
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
      _this.stompClient.subscribe("/scientific_paper/review_paper", 
        () => {
          _this.dialogRef.close(_this.taskId);
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
