import { Component, Inject, ViewChild, HostListener } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';
import { PdfViewerComponent as PdfViewer } from 'ng2-pdf-viewer';

@Component({
  selector: 'app-pdf-viewer',
  templateUrl: './pdf-viewer.component.html',
  styleUrls: ['./pdf-viewer.component.css']
})
export class PdfViewerComponent {

  urlCreator = window.URL;
  pdfSrc: string;
  zoom = 1;
  
  @ViewChild(PdfViewer, {static: false})
  private pdfComponent: PdfViewer;

  constructor(@Inject(MAT_DIALOG_DATA) private data: any) { 
    this.pdfSrc = this.urlCreator.createObjectURL(this.data);
  }

  @HostListener('mousewheel', ['$event']) onMousewheel(event) {
    
    if (event.ctrlKey == true) {

      event.preventDefault();
      
      if(event.wheelDelta > 0 && this.zoom <= 2){
        this.zoom += 0.1;
      } else if(event.wheelDelta < 0 && this.zoom >= 0.5){
        this.zoom -= 0.1;
      }
    }
  }
}
