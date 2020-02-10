import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmAutoLoggingInBottomSheetComponent } from './confirm-auto-logging-in-bottom-sheet.component';

describe('ConfirmAutoLoggingInBottomSheetComponent', () => {
  let component: ConfirmAutoLoggingInBottomSheetComponent;
  let fixture: ComponentFixture<ConfirmAutoLoggingInBottomSheetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfirmAutoLoggingInBottomSheetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmAutoLoggingInBottomSheetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
