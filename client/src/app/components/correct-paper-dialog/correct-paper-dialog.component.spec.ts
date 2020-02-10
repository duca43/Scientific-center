import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CorrectPaperDialogComponent } from './correct-paper-dialog.component';

describe('CorrectPaperDialogComponent', () => {
  let component: CorrectPaperDialogComponent;
  let fixture: ComponentFixture<CorrectPaperDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CorrectPaperDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CorrectPaperDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
