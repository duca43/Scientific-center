import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckMagazineDataTasksComponent } from './check-magazine-data-tasks.component';

describe('CheckMagazineDataTasksComponent', () => {
  let component: CheckMagazineDataTasksComponent;
  let fixture: ComponentFixture<CheckMagazineDataTasksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CheckMagazineDataTasksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckMagazineDataTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
