import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SetMembershipPriceDialogComponent } from './set-membership-price-dialog.component';

describe('SetMembershipPriceDialogComponent', () => {
  let component: SetMembershipPriceDialogComponent;
  let fixture: ComponentFixture<SetMembershipPriceDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SetMembershipPriceDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SetMembershipPriceDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
