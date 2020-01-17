import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseEditorsAndReviewersDialogComponent } from './choose-editors-and-reviewers-dialog.component';

describe('ChooseEditorsAndReviewersDialogComponent', () => {
  let component: ChooseEditorsAndReviewersDialogComponent;
  let fixture: ComponentFixture<ChooseEditorsAndReviewersDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChooseEditorsAndReviewersDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChooseEditorsAndReviewersDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
