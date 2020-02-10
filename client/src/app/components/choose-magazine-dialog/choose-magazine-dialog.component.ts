import { Component, OnInit, Inject, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { Util } from 'src/app/utils';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-choose-magazine-dialog',
  templateUrl: './choose-magazine-dialog.component.html',
  styleUrls: ['./choose-magazine-dialog.component.css']
})
export class ChooseMagazineDialogComponent implements OnDestroy {

  formFields: any;
  form: any;
  processInstanceId: string;
  stompClient: Stomp.Client;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<ChooseMagazineDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private scientificPaperService: ScientificPaperService,
    private util: Util) { 
      this.formFields = this.data.formFieldsDto.formFields;
      this.processInstanceId = this.data.formFieldsDto.processInstanceId;
      this.form = this.data.form;
      this.setupStompClient();
  }

  submit() {
    this.requestProcessing = true;
    const chooseMagazineDto = {
      magazineId: this.form.value['magazine'].id,
      processInstanceId: this.processInstanceId
    };
    
    this.scientificPaperService.chooseMagazine(chooseMagazineDto).subscribe(
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
      _this.stompClient.subscribe("/scientific_paper/magazine_chosen", 
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
