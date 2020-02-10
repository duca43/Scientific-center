import { TestBed } from '@angular/core/testing';

import { ScientificPaperService } from './scientific-paper.service';

describe('ScientificPaperService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ScientificPaperService = TestBed.get(ScientificPaperService);
    expect(service).toBeTruthy();
  });
});
