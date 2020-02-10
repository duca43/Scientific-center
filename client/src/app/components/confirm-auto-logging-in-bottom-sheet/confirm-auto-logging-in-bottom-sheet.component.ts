import { Component, OnInit, Inject } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material';

@Component({
  selector: 'app-confirm-auto-logging-in-bottom-sheet',
  templateUrl: './confirm-auto-logging-in-bottom-sheet.component.html',
  styleUrls: ['./confirm-auto-logging-in-bottom-sheet.component.css']
})
export class ConfirmAutoLoggingInBottomSheetComponent {

  constructor(private bottomSheetRef: MatBottomSheetRef<ConfirmAutoLoggingInBottomSheetComponent>) { }

  closeSheet() {
    this.bottomSheetRef.dismiss();
  }
}
