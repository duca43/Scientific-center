import { ReviewPaperDialogComponent } from './../review-paper-dialog/review-paper-dialog.component';
import { AuthenticationService } from './../../services/authentication/authentication.service';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-review-paper-tasks',
  templateUrl: './review-paper-tasks.component.html',
  styleUrls: ['./review-paper-tasks.component.css']
})
export class ReviewPaperTasksComponent implements OnInit {

  tasks: any[];
  currentPage = 'Review papers tasks';
  taskName = 'review paper';
  buttonName = 'Review paper';

  constructor(private dialog: MatDialog,
    private scientificPaperService: ScientificPaperService,
    private authenticationService: AuthenticationService,
    private util: Util) { }

  ngOnInit() {
    this.getTasks();
  }

  getTasks() {
    this.scientificPaperService.getActiveReviewPaperTasks(this.authenticationService.getUsername()).subscribe(
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
    this.scientificPaperService.getTaskFormFields(task.id).subscribe(
      (formFieldsDto: any) => {

        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogRef = this.dialog.open(ReviewPaperDialogComponent,
        {
          data: {
            'editor': this.authenticationService.getUsername(),
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
                this.util.showSnackBar('You have reviewed scientific paper successfully!', true);
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
