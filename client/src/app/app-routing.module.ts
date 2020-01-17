import { EditorMagazinesComponent } from './components/editor-magazines/editor-magazines.component';
import { NewEditorDialogComponent } from './components/new-editor-dialog/new-editor-dialog.component';
import { EditorGuard } from './guards/editor-guard/editor.guard';
import { NewMagazineDialogComponent } from './components/new-magazine-dialog/new-magazine-dialog.component';
import { LoginDialogComponent } from './components/login-dialog/login-dialog.component';
import { LoginComponent } from './components/login/login.component';
import { RegistrationDialogComponent } from './components/registration-dialog/registration-dialog.component';
import { CheckReviewerDialogComponent } from './components/check-reviewer-dialog/check-reviewer-dialog.component';
import { AdministratorGuard } from './guards/administrator-guard/administrator.guard';
import { CheckReviewerTasksComponent } from './components/check-reviewer-tasks/check-reviewer-tasks.component';
import { VerifyAccountDialogComponent } from './components/verify-account-dialog/verify-account-dialog.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomepageComponent } from './components/homepage/homepage.component';
import { RegistrationComponent } from './components/registration/registration.component';
import { VerifyAccountComponent } from './components/verify-account/verify-account.component';
import { GenericFormComponent } from './components/generic-form/generic-form.component';
import { UsersComponent } from './components/users/users.component';
import { ChooseEditorsAndReviewersDialogComponent } from './components/choose-editors-and-reviewers-dialog/choose-editors-and-reviewers-dialog.component';
import { CheckMagazineDataTasksComponent } from './components/check-magazine-data-tasks/check-magazine-data-tasks.component';
import { CheckMagazineDataDialogComponent } from './components/check-magazine-data-dialog/check-magazine-data-dialog.component';

const routes: Routes = [
  {path: '', component: HomepageComponent},
  {path: 'verify_account', component: VerifyAccountComponent},
  {path: 'check_reviewer', component: CheckReviewerTasksComponent, canActivate: [AdministratorGuard]},
  {path: 'users', component: UsersComponent, canActivate: [AdministratorGuard]},
  {path: 'my_magazines', component: EditorMagazinesComponent, canActivate: [EditorGuard]},
  {path: 'check_magazine', component: CheckMagazineDataTasksComponent, canActivate: [AdministratorGuard]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: false})],
  exports: [RouterModule]
})
export class AppRoutingModule { }

export const RoutingComponents = [HomepageComponent, 
    RegistrationComponent,
    RegistrationDialogComponent,
    VerifyAccountComponent,
    VerifyAccountDialogComponent,
    GenericFormComponent,
    CheckReviewerTasksComponent,
    CheckReviewerDialogComponent,
    LoginComponent,
    LoginDialogComponent,
    UsersComponent,
    EditorMagazinesComponent,
    NewMagazineDialogComponent,
    NewEditorDialogComponent,
    ChooseEditorsAndReviewersDialogComponent,
    CheckMagazineDataTasksComponent,
    CheckMagazineDataDialogComponent]
