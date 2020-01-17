import { MagazineService } from 'src/app/services/magazine/magazine.service';
import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { RegistrationService } from 'src/app/services/registration/registration.service';
import { Util } from 'src/app/utils';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';

@Component({
  selector: 'app-check-magazine-data-dialog',
  templateUrl: './check-magazine-data-dialog.component.html',
  styleUrls: ['./check-magazine-data-dialog.component.css']
})
export class CheckMagazineDataDialogComponent {

  taskId: any;
  formFields: any;
  form: any;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<CheckMagazineDataDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private magazineService: MagazineService,
    private authenticationService: AuthenticationService,
    private util: Util) {
      this.taskId = this.data.taskId;
      this.formFields = this.data.formFields;
      this.form = this.data.form;
  }

  submit() {
    this.requestProcessing = true;
    const checkMagazineDto = {};
    checkMagazineDto['taskId'] = this.taskId;
    checkMagazineDto['admin'] = this.authenticationService.getUsername();
    checkMagazineDto['magazineActivated'] = this.form.get('magazineActivated').value;

    this.magazineService.checkMagazineData(checkMagazineDto).subscribe(
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
