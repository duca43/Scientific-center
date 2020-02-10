import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MakeFinalDecisionDialogComponent } from './make-final-decision-dialog.component';

describe('MakeFinalDecisionDialogComponent', () => {
  let component: MakeFinalDecisionDialogComponent;
  let fixture: ComponentFixture<MakeFinalDecisionDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MakeFinalDecisionDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MakeFinalDecisionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
