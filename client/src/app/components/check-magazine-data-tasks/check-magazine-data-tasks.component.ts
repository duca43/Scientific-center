import { MagazineService } from 'src/app/services/magazine/magazine.service';
import { Component, OnInit } from '@angular/core';
import { CheckMagazineDataDialogComponent } from '../check-magazine-data-dialog/check-magazine-data-dialog.component';
import { MatDialog } from '@angular/material';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-check-magazine-data-tasks',
  templateUrl: './check-magazine-data-tasks.component.html',
  styleUrls: ['./check-magazine-data-tasks.component.css']
})
export class CheckMagazineDataTasksComponent implements OnInit {

  tasks: any[];

  constructor(private dialog: MatDialog,
    private magazineService: MagazineService,
    private util: Util) { }

  ngOnInit() {
    this.getTasks();
  }

  private getTasks() {
    this.magazineService.getActiveCheckMagazineDataTasks().subscribe((tasks: any[]) => {
      this.tasks = this.util.sortArray(tasks, 'createTime', true);
    }, (response) => {
      this.util.showSnackBar(response.error.message, false);
    });
  }

  openCheckMagazineDataDialog(task) {
    this.magazineService.getCheckMagazineDataFormFields(task.id).subscribe(
      (formFieldsDto: any) => {

        const form = this.util.createGenericForm(formFieldsDto.formFields);

        const dialogRef = this.dialog.open(CheckMagazineDataDialogComponent,
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
              this.util.showSnackBar('You have checked magazine successfully!', true);
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
