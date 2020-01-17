import { NewEditorDialogComponent } from './../new-editor-dialog/new-editor-dialog.component';
import { UsersService } from './../../services/users/users.service';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource, MatPaginator, MatDialog } from '@angular/material';
import { Util } from 'src/app/utils';
import {animate, state, style, transition, trigger} from '@angular/animations';
import *  as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ]
})
export class UsersComponent implements OnInit {

  displayedColumns: string[];
  dataSource = new MatTableDataSource<Element[]>();
  users: any[];
  expandedElement: any | null;

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  columns: any[];
  stompClient: Stomp.Client;

  constructor(private dialog: MatDialog,
    private userService: UsersService,
    private util: Util) { 
      this.setupStompClient();
    }

  ngOnInit() {
    this.dataSource.paginator = this.paginator;
    this.userService.getUsers().subscribe(
      (users: any[]) => {
        for (const i in users) {
          const position: number = parseInt(i, 10) + 1;
          users[i].position = position;
        }

        this.columns = [
          { columnDef: 'position', header: 'No.',     cell: (element: any) => element.position, type: 'string'},
          { columnDef: 'username',  header: 'Username', cell: (element: any) => element.username, type: 'string'  },
          { columnDef: 'firstname',  header: 'First name', cell: (element: any) => element.firstname, type: 'string'  },
          { columnDef: 'lastname',  header: 'Last name', cell: (element: any) => element.lastname, type: 'string'  },
          { columnDef: 'email',  header: 'Email', cell: (element: any) => element.email, type: 'string'  },
          { columnDef: 'title',  header: 'Title', cell: (element: any) => element.title ? element.title : '/', type: 'string'  },
          { columnDef: 'Enabled',  header: 'Enabled?', cell: (element: any) => element.enabled, type: 'boolean'  }
        ];
        
        this.users = users;
        this.displayedColumns = this.columns.map(c => c.columnDef);
        this.dataSource.data = users;
      },
      (response) => {
        if (response && response.error) {
          this.util.showSnackBar(response.error.message, false);
        }
      }
    );
  }

  openNewEditorDialog() {
    const dialogRef = this.dialog.open(NewEditorDialogComponent,
    {
      data: undefined,
      disableClose: true,
      autoFocus: true,
      width: '50%'
    });
    dialogRef.afterClosed().subscribe(
      (editor: any) => {
        if(editor) {
          editor.position = this.users.length + 1;
          editor.enabled = true;
          this.users.push(editor);
          this.dataSource.data = this.users;
          this.util.showSnackBar('You have successfully added new editor!', true);
        }
      }
    );
  }

  setupStompClient() {
    const webSocket = new SockJS(environment.api);
    this.stompClient = Stomp.over(webSocket);
    const _this = this;
    this.stompClient.connect({}, frame => {
      _this.stompClient.subscribe("/registration/new", 
        (message) => {
          const newUser: any = JSON.parse(message.body);
          console.dir(newUser);
          newUser.position = _this.users.length + 1;
          _this.users.push(newUser);
          _this.dataSource.data = _this.users;
          _this.util.showSnackBar('Hey, there is new user with username \'' + newUser.username + '\'', true);
        }
      );
    });
  }

  ngOnDestroy(): void {
    this.stompClient.disconnect(() => {
      console.log('stomp client destroyed');
    });
  }
}
