import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddScientificPaperComponent } from './add-scientific-paper.component';

describe('AddScientificPaperComponent', () => {
  let component: AddScientificPaperComponent;
  let fixture: ComponentFixture<AddScientificPaperComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddScientificPaperComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddScientificPaperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
