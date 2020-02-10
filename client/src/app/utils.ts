import { FilesService } from './services/files/files.service';
import { MatSnackBar } from '@angular/material';
import { NgModule } from '@angular/core';
import { FormGroup, FormControl, Validators, ValidatorFn, ValidationErrors } from '@angular/forms';
import { FileUploader, FileLikeObject, FileItem } from 'ng2-file-upload';
import { AuthenticationService } from './services/authentication/authentication.service';

@NgModule()
export class Util {

    constructor(private snackBar: MatSnackBar,
      private authenticationService: AuthenticationService,
      private filesService: FilesService) { }

    showSnackBar(message: string, success: boolean) {
        if (!message) {
            return;
        }
        const snackBarRef = this.snackBar.open(message, 'Close', { duration: 5000, verticalPosition: 'top', panelClass: [success ? 'snackbar-success' : 'snackbar-failure']});
        snackBarRef.onAction().subscribe(
            () => {
                snackBarRef.dismiss();
            }
        );
    }

    createGenericForm(formFields: any[]): FormGroup {
        console.dir(formFields);
        const form = new FormGroup({});
        formFields.forEach(formField => {
          const control = new FormControl;
          let formValidations = [];
          const formValidationConstraints: [] = formField.validationConstraints;
          if (formValidationConstraints && formValidationConstraints.length > 0) {
            formValidationConstraints.forEach((constraint: any) => {
              if(constraint.name === 'required') {
                formValidations.push(Validators.required);
              } else if(constraint.name === 'minlength') {
                formValidations.push(Validators.minLength(constraint.configuration));
              } else if(constraint.name === 'maxlength') {
                formValidations.push(Validators.maxLength(parseInt(constraint.configuration, 10) - 1));
              } else if(constraint.name === 'min') {
                formValidations.push(Validators.min(constraint.configuration));
              } else if(constraint.name === 'max') {
                formValidations.push(Validators.max(constraint.configuration));
              } else if(constraint.name === 'readonly') {
                control.disable();
              } else if(constraint.name === 'minselection') {
                form.setValidators(minSelection(formField.id, constraint.configuration));
              }
            });
          }
          const type: string = formField.type.name;
          if (type === 'email') {
            formValidations.push(Validators.email);
          } else if(type === 'string_list') {
            formField.type.values = new Array<String>();
          } else if(type === 'boolean') {
            control.setValue(false);
          } else if(type === 'file_upload') {
            const accessToken: string = this.authenticationService.getAccessToken();
            formField.uploader = new FileUploader({authToken: 'Bearer ' + accessToken, autoUpload: false, allowedFileType: ['pdf']});
            formField.uploader.onWhenAddingFileFailed = (item, filter) => this.onWhenAddingFileFailed(item, filter);
            formField.uploader.onAfterAddingFile = (fileItem) => this.onAfterAddingFile(fileItem);
            formField.isDropOver = false;
          } else if(type === 'file_view') {
            console.dir(formField.value.value);
            this.filesService.download(formField.value.value).subscribe(
              (blob: Blob) => {
                formField.file = blob;
              }
            )
          } else if(type === 'enum' || type.startsWith('multiple_enum_') && !Array.isArray(formField.type.values)) {
            let values = [];
            Object.keys(formField.type.values).forEach(key => {
              values.push({id: key, name: formField.type.values[key]});
            });
            formField.type.values = values;
          }
          control.setValidators(formValidations);
          if (formField.value) {
            if (type === 'enum') {
              control.setValue(formField.type.values.filter(value => value.id == formField.value.value)[0]);
            } else {
              control.setValue(formField.value.value);
            }
          }
          form.addControl(formField.id, control);   
        });

        return form;
    }

    private onAfterAddingFile(fileItem: FileItem): any {
      this.showSnackBar('File named \''.concat(fileItem.file.name).concat('\' added successfully!'), true);
    };

    private onWhenAddingFileFailed(item: FileLikeObject, filter: any): any {
      if (filter.name === 'fileType') {
        this.showSnackBar('File named \''.concat(item.name).concat('\' is not in PDF format!'), false);
      }
    };

    sortArray(array: any, sortBy: string, asc: boolean) {
      if (!Array.isArray(array) || array.length === 0) {
        return [];
      }
      array.sort((a: any, b: any) => {
        let val1 = a[sortBy];
        let val2 = b[sortBy];
        if (typeof a[sortBy] === 'string') {
          val1 = val1.toLowerCase();
          val2 = val2.toLowerCase();
        }
        if (val1 < val2) {
          return asc ? -1 : 1;
        } else if (val1 > val2) {
          return asc ? 1 : -1;
        } else {
          return 0;
        }
      });
      return array;
    }

    initLocations(formFields: any[]) {
      formFields.forEach(formField => {
        if (formField.type.name === 'location') {
          console.log('Create location autocomplete field for form field with id \''.concat(formField.id).concat('\''));
          const places = require('places.js');
          const placesAutocomplete = places({
            appId: 'pl14EZX3IQNN',
            apiKey: 'ad1257b86ef3f77014a0b7f168c417f7',
            container: document.querySelector('#' + formField.id),
            aroundLatLng: ''
          }).configure({
            type: 'address',
            aroundLatLngViaIP: true
          });;
    
          placesAutocomplete.on('change', e => {
            const location: any = {};
            location.address = e.suggestion.value;
            location.country = e.suggestion.country;
            location.city = e.suggestion.city ? e.suggestion.city : e.suggestion.name;
            location.latitude = e.suggestion.latlng.lat;
            location.longitude = e.suggestion.latlng.lng;
    
            formField.locationValue = location;
          });
        }
      });
    }
}

export enum Authority {
  ADMIN = 'ROLE_ADMINISTRATOR',
  AUTHOR = 'ROLE_AUTHOR',
  EDITOR = 'ROLE_EDITOR',
  GUEST = 'ROLE_GUEST',
  REVIEWER = 'ROLE_REVIEWER',
  USER = 'ROLE_USER'
}

export function minSelection(controlName: string, minSelection: number): ValidatorFn {
  return (formGroup: FormGroup): ValidationErrors => {
      const control = formGroup.controls[controlName];

      if (!control.value || control.errors && !control.errors.minSelection) { return; }
      const value: [] = control.value;
      if (value.length < minSelection) {
          control.setErrors({ minSelection: {isError: true, value: minSelection} });
      } else {
          control.setErrors(null);
      }
  };
}

export enum HttpStatus {
  OK = 200,
  BAD_REQUEST = 400,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  CONFLICT = 409
}
