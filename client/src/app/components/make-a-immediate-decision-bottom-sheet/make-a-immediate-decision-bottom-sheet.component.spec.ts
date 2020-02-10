import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MakeAImmediateDecisionBottomSheetComponent } from './make-a-immediate-decision-bottom-sheet.component';

describe('MakeAImmediateDecisionBottomSheetComponent', () => {
  let component: MakeAImmediateDecisionBottomSheetComponent;
  let fixture: ComponentFixture<MakeAImmediateDecisionBottomSheetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MakeAImmediateDecisionBottomSheetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MakeAImmediateDecisionBottomSheetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
