import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaperReviewByReviewerTasksComponent } from './paper-review-by-reviewer-tasks.component';

describe('PaperReviewByReviewerTasksComponent', () => {
  let component: PaperReviewByReviewerTasksComponent;
  let fixture: ComponentFixture<PaperReviewByReviewerTasksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaperReviewByReviewerTasksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaperReviewByReviewerTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
