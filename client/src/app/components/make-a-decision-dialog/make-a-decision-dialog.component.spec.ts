import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MakeADecisionDialogComponent } from './make-a-decision-dialog.component';

describe('MakeADecisionDialogComponent', () => {
  let component: MakeADecisionDialogComponent;
  let fixture: ComponentFixture<MakeADecisionDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MakeADecisionDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MakeADecisionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
