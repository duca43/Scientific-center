import { MagazineService } from './../../services/magazine/magazine.service';
import { Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-new-magazine-dialog',
  templateUrl: './new-magazine-dialog.component.html',
  styleUrls: ['./new-magazine-dialog.component.css']
})
export class NewMagazineDialogComponent {

  processInstanceId: string;
  taskId: string;
  formFields: any[];
  title: string;
  form: FormGroup;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<NewMagazineDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private magazineService: MagazineService,
    private util: Util) { 
      this.processInstanceId = this.data.formFieldsDto.processInstanceId;
      this.taskId = this.data.formFieldsDto.taskId;
      this.formFields = this.data.formFieldsDto.formFields;
      this.title = this.data.title;
      this.form = this.data.form;
    }

  submit() {
    this.requestProcessing = true;

    const magazine = { 
      issn: this.form.value['issn'],
      name: this.form.value['name'],
      payment: this.form.value['payment'].id,
      scientificAreas: this.form.value['scientificAreas']
    };  

    this.magazineService.createMagazine(magazine, this.processInstanceId).subscribe(
      () => {
        
        this.magazineService.checkMagazineCreation(this.processInstanceId).subscribe(
          () => {
            this.dialogRef.close(true);
          },
          (response) => {
            this.util.showSnackBar(response.error.message, false);
            this.requestProcessing = false;
          }
        );
      },
      (response) => {
        this.util.showSnackBar(response.error.message, false);
        this.requestProcessing = false;
      }
    );
  }
}
