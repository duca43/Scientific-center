import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifyAccountDialogComponent } from './verify-account-dialog.component';

describe('VerifyAccountDialogComponent', () => {
  let component: VerifyAccountDialogComponent;
  let fixture: ComponentFixture<VerifyAccountDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VerifyAccountDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VerifyAccountDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
