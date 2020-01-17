import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditorMagazinesComponent } from './editor-magazines.component';

describe('EditorMagazinesComponent', () => {
  let component: EditorMagazinesComponent;
  let fixture: ComponentFixture<EditorMagazinesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditorMagazinesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditorMagazinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
