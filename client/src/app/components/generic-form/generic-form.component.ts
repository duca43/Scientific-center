import { MatDialog } from '@angular/material';
import { Component, Input, Host } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ENTER, COMMA } from '@angular/cdk/keycodes';
import { PdfViewerComponent } from '../pdf-viewer/pdf-viewer.component';

@Component({
  selector: 'app-generic-form',
  templateUrl: './generic-form.component.html',
  styleUrls: ['./generic-form.component.css']
})
export class GenericFormComponent {

  separatorKeysCodes: number[] = [ENTER, COMMA];
  @Input() form: FormGroup;
  @Input() formFields: [];

  constructor(private dialog: MatDialog) { }

  fileInputClicked(id: string) {
    if (id && document.getElementById(id)) {
      document.getElementById(id).click();
    }
  }

  compareSelectObjects(object1: any, object2: any) {
    return object1 && object2 && object1.id == object2.id;
  }

  openPdfViewer(file: Blob) {
    if (file) {
      this.dialog.open(PdfViewerComponent, { 
        data: file,
        autoFocus: false, 
        width: '80%'
      });
    }
  }
}
