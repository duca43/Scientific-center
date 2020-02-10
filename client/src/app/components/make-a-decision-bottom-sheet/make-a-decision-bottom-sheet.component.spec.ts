import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MakeADecisionBottomSheetComponent } from './make-a-decision-bottom-sheet.component';

describe('MakeADecisionBottomSheetComponent', () => {
  let component: MakeADecisionBottomSheetComponent;
  let fixture: ComponentFixture<MakeADecisionBottomSheetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MakeADecisionBottomSheetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MakeADecisionBottomSheetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
