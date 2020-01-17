import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { MatDialogRef } from '@angular/material';
import { Util } from 'src/app/utils';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.css']
})
export class LoginDialogComponent implements OnInit {

  loginForm: FormGroup;
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<LoginDialogComponent>,
    private authenticationService: AuthenticationService,
    private util: Util) { }

  ngOnInit() {
    this.loginForm = new FormGroup({ 
      username: new FormControl('', Validators.required), 
      password: new FormControl('', Validators.required) 
    });
  }

  submit() {
    this.requestProcessing = true;
    this.authenticationService.login(this.loginForm.value).subscribe(
      (userState: any) => {
        this.authenticationService.setUsername(userState.username);
        this.authenticationService.setAccessToken(userState.token);
        this.authenticationService.setRoles(userState.roles);
        this.dialogRef.close(true);
      },
      (response) => {
        this.requestProcessing = false;
        console.log(response);
        // this.util.showSnackBar(response.error.message);
      }
    );
  }
}
