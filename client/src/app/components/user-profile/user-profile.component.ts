import { PdfViewerComponent } from './../pdf-viewer/pdf-viewer.component';
import { MatDialog, MatTableDataSource, MatPaginator } from '@angular/material';
import { Component, OnInit, OnDestroy, ViewChildren, QueryList, AfterViewInit } from '@angular/core';
import { ScientificPaperService } from 'src/app/services/scientific-paper/scientific-paper.service';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { Util } from 'src/app/utils';
import { ActivatedRoute } from '@angular/router';
import { PaymentService } from 'src/app/services/payment/payment.service';
import { UsersService } from 'src/app/services/users/users.service';
import { environment } from 'src/environments/environment';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { AddCoauthorDialogComponent } from '../add-coauthor-dialog/add-coauthor-dialog.component';
import { CorrectPaperDialogComponent } from '../correct-paper-dialog/correct-paper-dialog.component';
import { EnterScientificPaperInfoDialogComponent } from '../enter-scientific-paper-info-dialog/enter-scientific-paper-info-dialog.component';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit, OnDestroy {

  username: string;
  user: any;
  pdfSrc: string;
  scientificPapers: any[];
  stompClient: Stomp.Client;
  paginator: QueryList<MatPaginator>;
  paginatorAreSetFlag = false;

  @ViewChildren(MatPaginator) set setPaginator(paginator : QueryList<MatPaginator>) {
    this.paginator = paginator;
    if (!this.paginatorAreSetFlag && this.scientificPapers) {
      this.assignPaginators();
      this.paginatorAreSetFlag = true;
    }
  }

  constructor(private dialog: MatDialog,
    private scientificPaperService: ScientificPaperService,
    private authenticationService: AuthenticationService,
    private paymentService: PaymentService,
    private usersService: UsersService,
    private activatedRoute: ActivatedRoute,
    private util: Util) {
      this.setupStompClient();
  }

  ngOnInit() {
    this.username = this.authenticationService.getUsername();
    this.authenticationService.authSubject.subscribe(
      (data) => {
        if (data.key === this.authenticationService.usernameKey) {
          this.username = data.value;
        }
      }
    );

    if (this.username) {
      this.usersService.getByUsername(this.username).subscribe(
        (userDto: any) => {
          this.user = userDto;
        },
        (response: any) => {
          if (response && response.error) {
            this.util.showSnackBar(response.error.message, false);
          } else {
            this.util.showSnackBar('Unexpected error! Please, try again later', false);
          }
        }
      );

      this.retrieveScientificPapers();
    }

    const merchantOrderId = this.activatedRoute.snapshot.paramMap.get('merchantOrderId');
    if (merchantOrderId) {
      const membershipPaymentCompleteDto = {merchantOrderId: merchantOrderId, authorUsername: this.username};
      this.paymentService.completePayment(membershipPaymentCompleteDto).subscribe(
        (paymentCompleteDto: any) => {
          if (paymentCompleteDto) {
            if (paymentCompleteDto.flag) {
              // update membership payment

              if (paymentCompleteDto.processInstanceId) {
                this.scientificPaperService.getEnterScientificPaperInfoFormFields(paymentCompleteDto.processInstanceId).subscribe(
                  (formFieldsDto: any) => {
                    const form = this.util.createGenericForm(formFieldsDto.formFields);
                    const dialogRef = this.dialog.open(EnterScientificPaperInfoDialogComponent, {
                      data: {
                        'formFieldsDto': formFieldsDto,
                        'form': form
                      },
                      disableClose: true,
                      autoFocus: true,
                      width: '50%'
                    });
                    
                    dialogRef.afterClosed().subscribe(
                      (successFlag: any) => {
                        if (successFlag) {
                          this.util.showSnackBar('You initially created scientific paper!', true);
                        }
                      }
                    );
                  },
                  (response: any) => {
                    if (response && response.error) {
                      this.util.showSnackBar(response.error.message, false);
                    } else {
                      this.util.showSnackBar('Unexpected error! Please, try again later', false);
                    }
                  }
                );
              }
            }
            this.util.showSnackBar(paymentCompleteDto.message, paymentCompleteDto.flag);
          } else {
            this.util.showSnackBar('Unexpected error! Please, try again later', false);
          }
        },
        (response: any) => {
          if (response && response.error) {
            this.util.showSnackBar(response.error.message, false);
          } else {
            this.util.showSnackBar('Unexpected error! Please, try again later', false);
          }
        }
      );
    }
  }

  retrieveScientificPapers() {
    this.scientificPaperService.findAllByAuthor(this.username).subscribe(
      (scientificPapers: any[]) => {

        this.scientificPapers = scientificPapers;

        this.scientificPapers.forEach((scientificPaper, index) => {
          for (const i in scientificPaper.coauthorDtos) {
            const position: number = parseInt(i, 10) + 1;
            scientificPaper.coauthorDtos[i].position = position;
          }
  
          scientificPaper.columns = [
            { columnDef: 'position', header: 'No.',    cell: (element: any) => element.position},
            { columnDef: 'name', header: 'Name',       cell: (element: any) => element.name},
            { columnDef: 'email', header: 'Email',     cell: (element: any) => element.email},
            { columnDef: 'city', header: 'City',       cell: (element: any) => element.location.city},
            { columnDef: 'country', header: 'Country', cell: (element: any) => element.location.country},
            { columnDef: 'address', header: 'Address', cell: (element: any) => element.location.address}
          ];
          
          scientificPaper.displayedColumns = scientificPaper.columns.map(c => c.columnDef);
          scientificPaper.dataSource = new MatTableDataSource<Element[]>();
          scientificPaper.dataSource.data = scientificPaper.coauthorDtos;
        });
      },
      (response: any) => {
        if (response && response.error) {
          this.util.showSnackBar(response.error.message, false);
        } else {
          this.util.showSnackBar('Unexpected error! Please, try again later', false);
        }
      }
    );
  }

  openPdfViewer(paper) {
    console.dir(this.paginator.first);
    if (paper) {
      this.scientificPaperService.download(this.authenticationService.getUsername(), paper.id).subscribe(
        (file) => {
          this.dialog.open(PdfViewerComponent, { 
            data: file,
            autoFocus: false, 
            width: '80%'
          });
        }
      );
    }
  }

  openAddCoauthorsDialog(paper) {
    if (paper) {
      this.scientificPaperService.getAddCoauthorsFormFields(paper.id).subscribe(
        (formFieldsDto: any) => {
  
          this.authenticationService.setProcessInstanceId(formFieldsDto.processInstanceId);
  
          const form = this.util.createGenericForm(formFieldsDto.formFields);
  
          const dialogRef = this.dialog.open(AddCoauthorDialogComponent,
          {
            data: {
              'formFieldsDto': formFieldsDto,
              'form': form
            },
            disableClose: true,
            autoFocus: true,
            width: '50%'
          });
          dialogRef.afterClosed().subscribe(
            (addCoauthorResponseDto: any) => {
              if(addCoauthorResponseDto) {
                if (addCoauthorResponseDto.wantMoreCoauthors) {
                  this.openAddCoauthorsDialog(paper);
                  this.util.showSnackBar('You haved added co-author successfully! Please enter the details of the next co-author.', true);
                } else {
                  this.retrieveScientificPapers();
                  this.assignPaginators();
                  this.util.showSnackBar('You haved completed addition of co-authors successfully!', true);
                }
              }
            }
          );
        },
        (response: any) => {
          if (response && response.error) {
            this.util.showSnackBar(response.error.message, false);
          } else {
            this.util.showSnackBar('Unexpected error! Please, try again later', false);
          }
        }
      );
    }
  }

  openCorrectDialog(paper) {
    if (paper) {
      this.scientificPaperService.getCorrectPaperFormFields(paper.id).subscribe(
        (formFieldsDto: any) => {
  
          this.authenticationService.setProcessInstanceId(formFieldsDto.processInstanceId);
  
          const form = this.util.createGenericForm(formFieldsDto.formFields);
  
          const dialogRef = this.dialog.open(CorrectPaperDialogComponent,
          {
            data: {
              'formFieldsDto': formFieldsDto,
              'form': form,
              'scientificPaperId': paper.id
            },
            disableClose: true,
            autoFocus: true,
            width: '50%'
          });
          dialogRef.afterClosed().subscribe(
            (successFlag: any) => {
              if(successFlag) {
                this.retrieveScientificPapers();
                this.util.showSnackBar('You haved corrected your scientific paper! Wait for an editor to review corrections.', true);
              }
            }
          );
        },
        (response: any) => {
          if (response && response.error) {
            this.util.showSnackBar(response.error.message, false);
          } else {
            this.util.showSnackBar('Unexpected error! Please, try again later', false);
          }
        }
      );
    }
  }

  assignPaginators() {
    this.scientificPapers.forEach((scientificPaper, index) => {
      scientificPaper.dataSource.paginator = this.paginator.toArray()[index];
    });
  }

  setupStompClient() {
    const webSocket = new SockJS(environment.api);
    this.stompClient = Stomp.over(webSocket);
    const _this = this;
    this.stompClient.connect({}, frame => {
      _this.stompClient.subscribe("/scientific_paper/added", 
        (message) => {
          const scientificPaperDto: any = JSON.parse(message.body);
          _this.scientificPapers.push(scientificPaperDto);
        }
      );

      _this.stompClient.subscribe("/scientific_paper/review_paper", 
        (message) => {
          const reviewPaperResponseDto: any = JSON.parse(message.body);
          const paper = _this.scientificPapers.find(paper => paper.id == reviewPaperResponseDto.scientificPaperId);
          if (paper) {
            paper.approvedByMainEditor = reviewPaperResponseDto.approved;
            paper.requestedChanges = false;
            _this.util.showSnackBar(reviewPaperResponseDto.message, reviewPaperResponseDto.approved);
          }
        }
      );

      _this.stompClient.subscribe("/scientific_paper/review_pdf_document", 
        (message) => {
          const reviewPdfDocumentResponseDto: any = JSON.parse(message.body);
          const paper = _this.scientificPapers.find(paper => paper.id == reviewPdfDocumentResponseDto.scientificPaperId);
          if (paper) {
            paper.pdfFormattedWell = reviewPdfDocumentResponseDto.formattedWell;
            paper.requestedChanges = reviewPdfDocumentResponseDto.requestedChanges;
            _this.util.showSnackBar(reviewPdfDocumentResponseDto.message, reviewPdfDocumentResponseDto.formattedWell);
          }
        }
      );

      _this.stompClient.subscribe("/scientific_paper/enabled", 
        (message) => {
          const responseDto: any = JSON.parse(message.body);
          const paper = _this.scientificPapers.find(paper => paper.id == responseDto.scientificPaperId);
          if (paper) {
            paper.enabled = responseDto.approved;
            _this.util.showSnackBar(responseDto.message, responseDto.approved);
          }
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
