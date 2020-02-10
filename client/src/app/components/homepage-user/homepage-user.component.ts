import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';

@Component({
  selector: 'app-homepage-user',
  templateUrl: './homepage-user.component.html',
  styleUrls: ['./homepage-user.component.css']
})
export class HomepageUserComponent implements OnInit {

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
