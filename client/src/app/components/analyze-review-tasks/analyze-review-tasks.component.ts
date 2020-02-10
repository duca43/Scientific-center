import { MakeADecisionDialogComponent } from './../make-a-decision-dialog/make-a-decision-dialog.component';
import { AnalyzeReviewDialogComponent } from './../analyze-review-dialog/analyze-review-dialog.component';
import { Component, OnInit } from '@angular/core';
import { MatDialog, MatBottomSheet } from '@angular/material';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { Util } from 'src/app/utils';
import { MakeADecisionBottomSheetComponent } from '../make-a-decision-bottom-sheet/make-a-decision-bottom-sheet.component';

@Component({
  selector: 'app-analyze-review-tasks',
  templateUrl: './analyze-review-tasks.component.html',
  styleUrls: ['./analyze-review-tasks.component.css']
})
export class AnalyzeReviewTasksComponent implements OnInit {

  tasks: any[];
  currentPage = 'Paper review tasks';
  taskName = 'paper review by reviewer';
  buttonName = 'Review';

  constructor(private dialog: MatDialog,
    private bottomSheet: MatBottomSheet,
    private scientificPaperService: ScientificPaperService,
    private authenticationService: AuthenticationService,
    private util: Util) { }

  ngOnInit() {
    this.getTasks();
  }

  getTasks() {
    this.scientificPaperService.getActiveAnalyzeReviewTasks(this.authenticationService.getUsername()).subscribe(
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
    this.scientificPaperService.getAnalyzeReviewTaskFormFields(task.id).subscribe(
      (formFieldsDto: any) => {

        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogRef = this.dialog.open(AnalyzeReviewDialogComponent,
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
          (response: any) => {
            if(response) {
              const index = this.tasks.findIndex(task => task.id == response.taskId);
              if (index != -1) {
                this.tasks.splice(index, 1);
                this.util.showSnackBar('Analyze of review is completed!', true);

                if (response.formFieldsDto) {
                  const makeADecisionFormFieldsDto = response.formFieldsDto;

                  const bottomSheetRef = this.bottomSheet.open(MakeADecisionBottomSheetComponent, {
                     data: makeADecisionFormFieldsDto.token,
                     disableClose: true
                  });

                  bottomSheetRef.afterDismissed().subscribe(
                    () => {

                      const form = this.util.createGenericForm(makeADecisionFormFieldsDto.formFields);

                      const dialogRef = this.dialog.open(MakeADecisionDialogComponent,
                      {
                        data: {
                          'title': makeADecisionFormFieldsDto.token,
                          'taskId': makeADecisionFormFieldsDto.taskId,
                          'formFields': makeADecisionFormFieldsDto.formFields,
                          'form': form
                        },
                        disableClose: true,
                        autoFocus: true,
                        width: '50%'
                      });
                      dialogRef.afterClosed().subscribe(
                        (successFlag: any) => {
                          if(successFlag) {
                            this.util.showSnackBar('You have made your decision successfully!', true);
                          }
                        }
                      );
                    }
                  );
                }
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
