import { PaperReviewByReviewerDialogComponent } from './../paper-review-by-reviewer-dialog/paper-review-by-reviewer-dialog.component';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-paper-review-by-reviewer-tasks',
  templateUrl: './paper-review-by-reviewer-tasks.component.html',
  styleUrls: ['./paper-review-by-reviewer-tasks.component.css']
})
export class PaperReviewByReviewerTasksComponent implements OnInit {

  tasks: any[];
  currentPage = 'Paper review tasks';
  taskName = 'paper review by reviewer';
  buttonName = 'Review';

  constructor(private dialog: MatDialog,
    private scientificPaperService: ScientificPaperService,
    private authenticationService: AuthenticationService,
    private util: Util) { }

  ngOnInit() {
    this.getTasks();
  }

  getTasks() {
    this.scientificPaperService.getActivePaperReviewByReviewerTasks(this.authenticationService.getUsername()).subscribe(
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
    this.scientificPaperService.getPaperReviewByReviewerTaskFormFields(task.id).subscribe(
      (formFieldsDto: any) => {

        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogRef = this.dialog.open(PaperReviewByReviewerDialogComponent,
        {
          data: {
            'title': formFieldsDto.token,
            'reviewer': this.authenticationService.getUsername(),
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
                this.util.showSnackBar('You have done revision of scientific paper successfully!', true);
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
