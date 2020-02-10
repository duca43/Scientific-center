import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EnterScientificPaperInfoDialogComponent } from './enter-scientific-paper-info-dialog.component';

describe('EnterScientificPaperInfoDialogComponent', () => {
  let component: EnterScientificPaperInfoDialogComponent;
  let fixture: ComponentFixture<EnterScientificPaperInfoDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EnterScientificPaperInfoDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterScientificPaperInfoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
