import { TestBed, async, inject } from '@angular/core/testing';

import { EditorAndReviewerGuard } from './editor-and-reviewer.guard';

describe('EditorAndReviewerGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [EditorAndReviewerGuard]
    });
  });

  it('should ...', inject([EditorAndReviewerGuard], (guard: EditorAndReviewerGuard) => {
    expect(guard).toBeTruthy();
  }));
});
