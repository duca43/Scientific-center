import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewPdfDocumentTasksComponent } from './review-pdf-document-tasks.component';

describe('ReviewPdfDocumentTasksComponent', () => {
  let component: ReviewPdfDocumentTasksComponent;
  let fixture: ComponentFixture<ReviewPdfDocumentTasksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReviewPdfDocumentTasksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewPdfDocumentTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
