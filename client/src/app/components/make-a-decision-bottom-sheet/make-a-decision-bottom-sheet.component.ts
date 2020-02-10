import { Component, Inject } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material';

@Component({
  selector: 'app-make-a-decision-bottom-sheet',
  templateUrl: './make-a-decision-bottom-sheet.component.html',
  styleUrls: ['./make-a-decision-bottom-sheet.component.css']
})
export class MakeADecisionBottomSheetComponent {

  constructor(private bottomSheetRef: MatBottomSheetRef<MakeADecisionBottomSheetComponent>,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any) { }

  closeSheet() {
    this.bottomSheetRef.dismiss();
  }
}
