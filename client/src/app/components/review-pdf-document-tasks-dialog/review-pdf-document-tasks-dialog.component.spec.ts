import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewPdfDocumentTasksDialogComponent } from './review-pdf-document-tasks-dialog.component';

describe('ReviewPdfDocumentTasksDialogComponent', () => {
  let component: ReviewPdfDocumentTasksDialogComponent;
  let fixture: ComponentFixture<ReviewPdfDocumentTasksDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReviewPdfDocumentTasksDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewPdfDocumentTasksDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
