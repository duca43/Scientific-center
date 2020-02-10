import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewPaperTasksComponent } from './review-paper-tasks.component';

describe('ReviewPaperTasksComponent', () => {
  let component: ReviewPaperTasksComponent;
  let fixture: ComponentFixture<ReviewPaperTasksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReviewPaperTasksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewPaperTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
