import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaperReviewByReviewerDialogComponent } from './paper-review-by-reviewer-dialog.component';

describe('PaperReviewByReviewerDialogComponent', () => {
  let component: PaperReviewByReviewerDialogComponent;
  let fixture: ComponentFixture<PaperReviewByReviewerDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaperReviewByReviewerDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaperReviewByReviewerDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
