import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckReviewerTasksComponent } from './check-reviewer-tasks.component';

describe('CheckReviewerTasksComponent', () => {
  let component: CheckReviewerTasksComponent;
  let fixture: ComponentFixture<CheckReviewerTasksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CheckReviewerTasksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckReviewerTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
