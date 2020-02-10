import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalyzeReviewTasksComponent } from './analyze-review-tasks.component';

describe('AnalyzeReviewTasksComponent', () => {
  let component: AnalyzeReviewTasksComponent;
  let fixture: ComponentFixture<AnalyzeReviewTasksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AnalyzeReviewTasksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalyzeReviewTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
