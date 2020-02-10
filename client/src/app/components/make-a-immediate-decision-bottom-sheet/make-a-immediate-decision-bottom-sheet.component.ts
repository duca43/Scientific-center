import { Component, OnInit, Inject } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material';

@Component({
  selector: 'app-make-a-immediate-decision-bottom-sheet',
  templateUrl: './make-a-immediate-decision-bottom-sheet.component.html',
  styleUrls: ['./make-a-immediate-decision-bottom-sheet.component.css']
})
export class MakeAImmediateDecisionBottomSheetComponent {

  constructor(private bottomSheetRef: MatBottomSheetRef<MakeAImmediateDecisionBottomSheetComponent>,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any) { }

  closeSheet() {
    this.bottomSheetRef.dismiss();
  }
}
