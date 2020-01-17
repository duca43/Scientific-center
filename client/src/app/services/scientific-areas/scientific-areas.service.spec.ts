import { TestBed } from '@angular/core/testing';

import { ScientificAreasService } from './scientific-areas.service';

describe('ScientificAreasService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ScientificAreasService = TestBed.get(ScientificAreasService);
    expect(service).toBeTruthy();
  });
});
