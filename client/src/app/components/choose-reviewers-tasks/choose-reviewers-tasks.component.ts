import { Component, OnInit } from '@angular/core';
import { ChooseReviewersTasksDialogComponent } from '../choose-reviewers-tasks-dialog/choose-reviewers-tasks-dialog.component';
import { MatDialog } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-choose-reviewers-tasks',
  templateUrl: './choose-reviewers-tasks.component.html',
  styleUrls: ['./choose-reviewers-tasks.component.css']
})
export class ChooseReviewersTasksComponent implements OnInit {

  tasks: any[];
  currentPage = 'Choose reviewers tasks';
  taskName = 'choose reviewers';
  buttonName = 'Choose reviewers';

  constructor(private dialog: MatDialog,
    private scientificPaperService: ScientificPaperService,
    private authenticationService: AuthenticationService,
    private util: Util) { }

  ngOnInit() {
    this.getTasks();
  }

  getTasks() {
    this.scientificPaperService.getActiveChooseReviewersTasks(this.authenticationService.getUsername()).subscribe(
      (tasks: any[]) => {
        this.tasks = this.util.sortArray(tasks, 'createTime', true);
      },
      (response: any) => {
        if (response && response.error) {
          this.util.showSnackBar(response.error.message, false);
        } else {
          this.util.showSnackBar('Unexpected error! Please, try again later', false);
        }
      }
    );
  }

  openDialog(task) {
    this.scientificPaperService.getChooseReviewersTaskFormFields(task.id).subscribe(
      (formFieldsDto: any) => {

        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogRef = this.dialog.open(ChooseReviewersTasksDialogComponent,
        {
          data: {
            'title': formFieldsDto.token,
            'taskId': formFieldsDto.taskId,
            'formFields': formFieldsDto.formFields,
            'form': form
          },
          disableClose: true,
          autoFocus: true,
          width: '50%'
        });
        dialogRef.afterClosed().subscribe(
          (taskId: string) => {
            if(taskId) {
              const index = this.tasks.findIndex(task => task.id == taskId);
              if (index != -1) {
                this.tasks.splice(index, 1);
                this.util.showSnackBar('You have chosen reviewers successfully!', true);
              }
            }
          }
        );
      },
      (response: any) => {
        if (response && response.error) {
          this.util.showSnackBar(response.error.message, false);
        } else {
          this.util.showSnackBar('Unexpected error! Please, try again later', false);
        }
      }
    );
  }
}
