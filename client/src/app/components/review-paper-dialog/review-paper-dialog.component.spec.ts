import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewPaperDialogComponent } from './review-paper-dialog.component';

describe('ReviewPaperDialogComponent', () => {
  let component: ReviewPaperDialogComponent;
  let fixture: ComponentFixture<ReviewPaperDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReviewPaperDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewPaperDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
