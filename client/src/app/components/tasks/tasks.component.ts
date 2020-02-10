import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.css']
})
export class TasksComponent {

  @Input() tasks: any[];
  @Input() currentPage: string;
  @Input() taskName: string;
  @Input() buttonName: string;
  @Input() parentComponent: any;
}
