import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseReviewersTasksDialogComponent } from './choose-reviewers-tasks-dialog.component';

describe('ChooseReviewersTasksDialogComponent', () => {
  let component: ChooseReviewersTasksDialogComponent;
  let fixture: ComponentFixture<ChooseReviewersTasksDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChooseReviewersTasksDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChooseReviewersTasksDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
