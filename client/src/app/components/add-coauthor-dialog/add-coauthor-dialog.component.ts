import { Component, Inject, AfterViewInit, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { Util } from 'src/app/utils';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-add-coauthor-dialog',
  templateUrl: './add-coauthor-dialog.component.html',
  styleUrls: ['./add-coauthor-dialog.component.css']
})
export class AddCoauthorDialogComponent implements AfterViewInit, OnDestroy {

  formFields: any;
  form: any;
  processInstanceId: string;
  stompClient: Stomp.Client;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<AddCoauthorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private scientificPaperService: ScientificPaperService,
    private util: Util) { 
      this.formFields = this.data.formFieldsDto.formFields;
      this.processInstanceId = this.data.formFieldsDto.processInstanceId;
      this.form = this.data.form;
      this.setupStompClient();
  }

  ngAfterViewInit() { 
    this.util.initLocations(this.formFields); 
  }

  submit() {
    this.requestProcessing = true;
    const addCoauthorDto = this.form.value;
    this.formFields.forEach(formField => {
      if (formField.type.name === 'location') {
        addCoauthorDto[formField.id] = formField.locationValue;
      }
    }); 

    this.scientificPaperService.addCoauthor(addCoauthorDto, this.processInstanceId).subscribe(
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
      _this.stompClient.subscribe("/scientific_paper/added_coauthor", 
        (message) => {
          const addCoauthorResponseDto: any = JSON.parse(message.body);
          _this.dialogRef.close(addCoauthorResponseDto);
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
