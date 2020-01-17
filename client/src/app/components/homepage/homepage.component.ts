import { Component, OnInit, ElementRef, HostListener } from '@angular/core';
import { $ } from 'protractor';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { Util } from 'src/app/utils';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {
 
  roles: string[];

  constructor(private authenticationService: AuthenticationService) { }

  ngOnInit() {
    this.roles = this.authenticationService.getRoles();
    this.authenticationService.authSubject.subscribe(
      (data) => {
        if (data.key === this.authenticationService.rolesKey) {
          this.roles = data.value;
        }
      }
    );
  }
}
