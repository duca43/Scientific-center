import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseMagazineDialogComponent } from './choose-magazine-dialog.component';

describe('ChooseMagazineDialogComponent', () => {
  let component: ChooseMagazineDialogComponent;
  let fixture: ComponentFixture<ChooseMagazineDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChooseMagazineDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChooseMagazineDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
