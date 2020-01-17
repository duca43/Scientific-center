import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckReviewerDialogComponent } from './check-reviewer-dialog.component';

describe('CheckReviewerDialogComponent', () => {
  let component: CheckReviewerDialogComponent;
  let fixture: ComponentFixture<CheckReviewerDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CheckReviewerDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckReviewerDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
