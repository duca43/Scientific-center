import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalyzeReviewDialogComponent } from './analyze-review-dialog.component';

describe('AnalyzeReviewDialogComponent', () => {
  let component: AnalyzeReviewDialogComponent;
  let fixture: ComponentFixture<AnalyzeReviewDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AnalyzeReviewDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalyzeReviewDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
