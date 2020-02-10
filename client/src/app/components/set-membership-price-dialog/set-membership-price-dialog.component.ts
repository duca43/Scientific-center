import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { MagazineService } from 'src/app/services/magazine/magazine.service';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-set-membership-price-dialog',
  templateUrl: './set-membership-price-dialog.component.html',
  styleUrls: ['./set-membership-price-dialog.component.css']
})
export class SetMembershipPriceDialogComponent {

  editor: string;
  magazine: any;
  formFields: any;
  form: any;
  requestProcessing = false;
  taskId: any;

  constructor(private dialogRef: MatDialogRef<SetMembershipPriceDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private magazineService: MagazineService,
    private util: Util) {
      this.editor = this.data.editor;
      this.magazine = this.data.magazine;
      this.taskId = this.data.taskId;
      this.formFields = this.data.formFields;
      this.form = this.data.form;
  }

  submit() {
    this.requestProcessing = true;
    const membershipPriceDto = {
      issn: this.magazine.issn,
      price: this.form.value['price'],
      currency: this.form.value['currency'].id
    };
    
    this.magazineService.setMembershipPrice(membershipPriceDto, this.taskId).subscribe(
      () => {
        this.dialogRef.close();
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
