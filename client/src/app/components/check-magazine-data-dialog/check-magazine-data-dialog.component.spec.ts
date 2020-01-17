import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckMagazineDataDialogComponent } from './check-magazine-data-dialog.component';

describe('CheckMagazineDataDialogComponent', () => {
  let component: CheckMagazineDataDialogComponent;
  let fixture: ComponentFixture<CheckMagazineDataDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CheckMagazineDataDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckMagazineDataDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
