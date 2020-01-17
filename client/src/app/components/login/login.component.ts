import { Router } from '@angular/router';
import { LoginDialogComponent } from './../login-dialog/login-dialog.component';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  constructor(private dialog: MatDialog,
    private util: Util,
    private router: Router) { }

    openLoginDialog() {
      const dialogRef = this.dialog.open(LoginDialogComponent,
        {
          data: undefined,
          disableClose: true,
          autoFocus: true,
          width: '50%'
        });
        dialogRef.afterClosed().subscribe(
          (successFlag: any) => {
            if(successFlag) {
              this.router.navigateByUrl('/');
              this.util.showSnackBar('You have logged in successfully!', true);
            }
          }
        );
    }
}
