import { MakeFinalDecisionDialogComponent } from './../make-final-decision-dialog/make-final-decision-dialog.component';
import { Component } from '@angular/core';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { Util } from 'src/app/utils';
import { ReviewPdfDocumentTasksDialogComponent } from '../review-pdf-document-tasks-dialog/review-pdf-document-tasks-dialog.component';
import { MatDialog, MatBottomSheet } from '@angular/material';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { MakeAImmediateDecisionBottomSheetComponent } from '../make-a-immediate-decision-bottom-sheet/make-a-immediate-decision-bottom-sheet.component';
import { MakeADecisionDialogComponent } from '../make-a-decision-dialog/make-a-decision-dialog.component';

@Component({
  selector: 'app-review-pdf-document-tasks',
  templateUrl: './review-pdf-document-tasks.component.html',
  styleUrls: ['./review-pdf-document-tasks.component.css']
})
export class ReviewPdfDocumentTasksComponent{

  tasks: any[];
  currentPage = 'Review PDF document tasks';
  taskName = 'review PDF document';
  buttonName = 'Review PDF document';

  constructor(private dialog: MatDialog,
    private bottomSheet: MatBottomSheet,
    private scientificPaperService: ScientificPaperService,
    private authenticationService: AuthenticationService,
    private util: Util) { }

  ngOnInit() {
    this.getTasks();
  }

  getTasks() {
    this.scientificPaperService.getActiveReviewPdfDocumentTasks(this.authenticationService.getUsername()).subscribe(
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

        const dialogRef = this.dialog.open(ReviewPdfDocumentTasksDialogComponent,
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
          (response: any) => {
            if(response) {
              const index = this.tasks.findIndex(task => task.id == response.taskId);
              if (index != -1) {
                this.tasks.splice(index, 1);
                this.util.showSnackBar('You have reviewed pdf document successfully!', true);

                if (response.formFieldsDto && !task.afterCorrections) {
                  const makeADecisionFormFieldsDto = response.formFieldsDto;

                  const bottomSheetRef = this.bottomSheet.open(MakeAImmediateDecisionBottomSheetComponent, {
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
                } else if (response.formFieldsDto && task.afterCorrections) {
                  const makeFinalDecisionFormFieldsDto = response.formFieldsDto;
  
                  const form = this.util.createGenericForm(makeFinalDecisionFormFieldsDto.formFields);
  
                  const dialogRef = this.dialog.open(MakeFinalDecisionDialogComponent,
                  {
                    data: {
                      'title': makeFinalDecisionFormFieldsDto.token,
                      'taskId': makeFinalDecisionFormFieldsDto.taskId,
                      'formFields': makeFinalDecisionFormFieldsDto.formFields,
                      'form': form
                    },
                    disableClose: true,
                    autoFocus: true,
                    width: '50%'
                  });
                  dialogRef.afterClosed().subscribe(
                    (successFlag: any) => {
                      if(successFlag) {
                        this.util.showSnackBar('You have made your final decision successfully!', true);
                      }
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
