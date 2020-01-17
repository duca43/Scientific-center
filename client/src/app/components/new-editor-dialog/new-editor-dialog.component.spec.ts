import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewEditorDialogComponent } from './new-editor-dialog.component';

describe('NewEditorDialogComponent', () => {
  let component: NewEditorDialogComponent;
  let fixture: ComponentFixture<NewEditorDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewEditorDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewEditorDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
