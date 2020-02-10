import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCoauthorDialogComponent } from './add-coauthor-dialog.component';

describe('AddCoauthorDialogComponent', () => {
  let component: AddCoauthorDialogComponent;
  let fixture: ComponentFixture<AddCoauthorDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddCoauthorDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddCoauthorDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
