import { CheckReviewerDialogComponent } from './../check-reviewer-dialog/check-reviewer-dialog.component';
import { Component, OnInit } from '@angular/core';
import { Util } from 'src/app/utils';
import { MatDialog } from '@angular/material';
import { RegistrationService } from 'src/app/services/registration/registration.service';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';

@Component({
  selector: 'app-check-reviewer-tasks',
  templateUrl: './check-reviewer-tasks.component.html',
  styleUrls: ['./check-reviewer-tasks.component.css']
})
export class CheckReviewerTasksComponent implements OnInit {

  tasks: any[];

  constructor(private dialog: MatDialog,
    private registrationService: RegistrationService,
    private util: Util) { }

  ngOnInit() {
    this.getTasks();
  }

  private getTasks() {
    this.registrationService.getActiveCheckReviewerTasks().subscribe((tasks: any[]) => {
      this.tasks = this.util.sortArray(tasks, 'createTime', true);
    }, (response) => {
      this.util.showSnackBar(response.error.message, false);
    });
  }

  openCheckReviewerDialog(task) {
    this.registrationService.getCheckReviewerFormFields(task.id).subscribe(
      (formFieldsDto: any) => {

        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogRef = this.dialog.open(CheckReviewerDialogComponent,
        {
          data: {
            'taskId': formFieldsDto.taskId,
            'formFields': formFieldsDto.formFields,
            'form': form
          },
          disableClose: true,
          autoFocus: true,
          width: '50%'
        });
        dialogRef.afterClosed().subscribe(
          (successFlag: any) => {
            if(successFlag) {
              this.getTasks();
              this.util.showSnackBar('You have checked reviewer successfully!', true);
            }
          }
        );
      },
      (response) => {
        this.util.showSnackBar(response.error.message, false);
      }
    );
  }
}
