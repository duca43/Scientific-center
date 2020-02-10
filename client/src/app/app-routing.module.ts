import { MakeAImmediateDecisionBottomSheetComponent } from './components/make-a-immediate-decision-bottom-sheet/make-a-immediate-decision-bottom-sheet.component';
import { EditorAndReviewerGuard } from './guards/editor-and-reviewer-guard/editor-and-reviewer.guard';
import { ReviewerGuard } from './guards/reviewer-guard/reviewer.guard';
import { PdfViewerComponent } from './components/pdf-viewer/pdf-viewer.component';
import { ConfirmAutoLoggingInBottomSheetComponent } from './components/confirm-auto-logging-in-bottom-sheet/confirm-auto-logging-in-bottom-sheet.component';
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
import { HomepageUserComponent } from './components/homepage-user/homepage-user.component';
import { ChooseMagazineDialogComponent } from './components/choose-magazine-dialog/choose-magazine-dialog.component';
import { SetMembershipPriceDialogComponent } from './components/set-membership-price-dialog/set-membership-price-dialog.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { UserGuard } from './guards/user-guard/user.guard';
import { AddScientificPaperComponent } from './components/add-scientific-paper/add-scientific-paper.component';
import { EnterScientificPaperInfoDialogComponent } from './components/enter-scientific-paper-info-dialog/enter-scientific-paper-info-dialog.component';
import { AddCoauthorDialogComponent } from './components/add-coauthor-dialog/add-coauthor-dialog.component';
import { ReviewPaperTasksComponent } from './components/review-paper-tasks/review-paper-tasks.component';
import { ReviewPaperDialogComponent } from './components/review-paper-dialog/review-paper-dialog.component';
import { TasksComponent } from './components/tasks/tasks.component';
import { ReviewPdfDocumentTasksComponent } from './components/review-pdf-document-tasks/review-pdf-document-tasks.component';
import { ReviewPdfDocumentTasksDialogComponent } from './components/review-pdf-document-tasks-dialog/review-pdf-document-tasks-dialog.component';
import { CorrectPaperDialogComponent } from './components/correct-paper-dialog/correct-paper-dialog.component';
import { ChooseReviewersTasksDialogComponent } from './components/choose-reviewers-tasks-dialog/choose-reviewers-tasks-dialog.component';
import { ChooseReviewersTasksComponent } from './components/choose-reviewers-tasks/choose-reviewers-tasks.component';
import { PaperReviewByReviewerTasksComponent } from './components/paper-review-by-reviewer-tasks/paper-review-by-reviewer-tasks.component';
import { PaperReviewByReviewerDialogComponent } from './components/paper-review-by-reviewer-dialog/paper-review-by-reviewer-dialog.component';
import { AnalyzeReviewTasksComponent } from './components/analyze-review-tasks/analyze-review-tasks.component';
import { AnalyzeReviewDialogComponent } from './components/analyze-review-dialog/analyze-review-dialog.component';
import { MakeADecisionBottomSheetComponent } from './components/make-a-decision-bottom-sheet/make-a-decision-bottom-sheet.component';
import { MakeADecisionDialogComponent } from './components/make-a-decision-dialog/make-a-decision-dialog.component';
import { MakeFinalDecisionDialogComponent } from './components/make-final-decision-dialog/make-final-decision-dialog.component';

const routes: Routes = [
  {path: '', component: HomepageComponent},
  {path: 'verify_account', component: VerifyAccountComponent},
  {path: 'check_reviewer', component: CheckReviewerTasksComponent, canActivate: [AdministratorGuard]},
  {path: 'users', component: UsersComponent, canActivate: [AdministratorGuard]},
  {path: 'my_magazines', component: EditorMagazinesComponent, canActivate: [EditorGuard]},
  {path: 'my_magazines/:merchantId', component: EditorMagazinesComponent, canActivate: [EditorGuard]},
  {path: 'check_magazine', component: CheckMagazineDataTasksComponent, canActivate: [AdministratorGuard]},
  {path: 'my_profile', component: UserProfileComponent, canActivate: [UserGuard]},
  {path: 'my_profile/:merchantOrderId', component: UserProfileComponent, canActivate: [UserGuard]},
  {path: 'review_paper', component: ReviewPaperTasksComponent, canActivate: [EditorGuard]},
  {path: 'review_pdf_document', component: ReviewPdfDocumentTasksComponent, canActivate: [EditorGuard]},
  {path: 'choose_reviewer', component: ChooseReviewersTasksComponent, canActivate: [EditorGuard]},
  {path: 'paper_review', component: PaperReviewByReviewerTasksComponent, canActivate: [EditorAndReviewerGuard]},
  {path: 'analyze_review', component: AnalyzeReviewTasksComponent, canActivate: [EditorGuard]}
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
    CheckMagazineDataDialogComponent,
    HomepageUserComponent,
    ChooseMagazineDialogComponent,
    SetMembershipPriceDialogComponent,
    ConfirmAutoLoggingInBottomSheetComponent,
    UserProfileComponent,
    AddScientificPaperComponent,
    EnterScientificPaperInfoDialogComponent,
    PdfViewerComponent,
    AddCoauthorDialogComponent,
    ReviewPaperTasksComponent,
    ReviewPaperDialogComponent,
    TasksComponent,
    ReviewPdfDocumentTasksComponent,
    ReviewPdfDocumentTasksDialogComponent,
    CorrectPaperDialogComponent,
    ChooseReviewersTasksComponent,
    ChooseReviewersTasksDialogComponent,
    PaperReviewByReviewerTasksComponent,
    PaperReviewByReviewerDialogComponent,
    AnalyzeReviewTasksComponent,
    AnalyzeReviewDialogComponent,
    MakeADecisionBottomSheetComponent,
    MakeADecisionDialogComponent,
    MakeAImmediateDecisionBottomSheetComponent,
    MakeFinalDecisionDialogComponent]
