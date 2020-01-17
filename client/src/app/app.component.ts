import { Component, OnInit, HostListener, ElementRef } from '@angular/core';
import { RegistrationComponent } from './components/registration/registration.component';
import { MatDialog } from '@angular/material/dialog';
import { Util } from './utils';
import { RegistrationService } from './services/registration/registration.service';
import { Validators, FormControl, FormGroup, ValidationErrors, AsyncValidator } from '@angular/forms';
import { AuthenticationService } from './services/authentication/authentication.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  username: string;
  title = 'scientific-center';
  navBackground = false;
  
  constructor(private authenticationService: AuthenticationService,
    private util: Util,
    private router: Router) { }
  
  @HostListener('window:scroll') onScrollEvent(){
    this.navBackground = window.scrollY > 50 ? true : false;
  }

  ngOnInit() {
    this.username = this.authenticationService.getUsername();
    this.authenticationService.authSubject.subscribe(
      (data) => {
        if (data.key === this.authenticationService.usernameKey) {
          this.username = data.value;
        }
      }
    );
  }

  logout() {
    setTimeout(() => {
      this.router.navigateByUrl('/');
      this.authenticationService.removeUsername();
      this.authenticationService.removeAccessToken();
      this.authenticationService.removeRoles();
      this.util.showSnackBar('You have logged out successfully!', true);
    }, 500);
  }
}
