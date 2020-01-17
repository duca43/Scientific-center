import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewMagazineDialogComponent } from './new-magazine-dialog.component';

describe('NewMagazineDialogComponent', () => {
  let component: NewMagazineDialogComponent;
  let fixture: ComponentFixture<NewMagazineDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewMagazineDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewMagazineDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
