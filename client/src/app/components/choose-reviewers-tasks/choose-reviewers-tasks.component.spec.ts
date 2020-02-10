import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseReviewersTasksComponent } from './choose-reviewers-tasks.component';

describe('ChooseReviewersTasksComponent', () => {
  let component: ChooseReviewersTasksComponent;
  let fixture: ComponentFixture<ChooseReviewersTasksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChooseReviewersTasksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChooseReviewersTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
