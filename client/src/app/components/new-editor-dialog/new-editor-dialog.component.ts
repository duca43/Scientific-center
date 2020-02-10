import { ScientificAreasService } from './../../services/scientific-areas/scientific-areas.service';
import { UsersService } from './../../services/users/users.service';
import { Component, AfterViewInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Util } from 'src/app/utils';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-new-editor-dialog',
  templateUrl: './new-editor-dialog.component.html',
  styleUrls: ['./new-editor-dialog.component.css']
})
export class NewEditorDialogComponent implements AfterViewInit {

  form: FormGroup;
  formFields: any[];
  requestProcessing = false;

  constructor(private dialogRef: MatDialogRef<NewEditorDialogComponent>,
    private userService: UsersService,
    private scientificAreasService: ScientificAreasService,
    private util: Util) { 
      this.initFormFields();
    }

  ngAfterViewInit() { 
    console.dir("HEEEEEEEEEEI");
    this.util.initLocations(this.formFields); 
  }

  submit() {
    this.requestProcessing = true;

    const editor = this.form.value;
    
    this.formFields.forEach(formField => {
      if (formField.type.name === 'location') {
        editor[formField.id] = formField.locationValue;
      }
    }); 

    this.userService.addEditor(editor).subscribe(
      () => {
        this.dialogRef.close(editor);
      },
      (response: any) => {
        this.requestProcessing = false;
        if (response && response.error) {
          this.util.showSnackBar(response.error.message, false);
        } else {
          this.util.showSnackBar('Unexpected error! Please, try again later', false);
        }
      }
    );
  }

  initFormFields() {
    this.scientificAreasService.getScientificAreas().subscribe(
      (areas) => {
        this.formFields = [
          {
            "id": "username",
            "label": "Username",
            "type": {
              "name": "string"
            },
            "validationConstraints": [
              {
                "name": "required",
                "configuration": null
              },
              {
                "name": "minlength",
                "configuration": "4"
              },
              {
                "name": "maxlength",
                "configuration": "32"
              }
            ]
          },
          {
            "id": "password",
            "label": "Password",
            "type": {
              "name": "password"
            },
            "validationConstraints": [
              {
                "name": "required",
                "configuration": null
              },
              {
                "name": "minlength",
                "configuration": "8"
              },
              {
                "name": "maxlength",
                "configuration": "32"
              }
            ]
          },
          {
            "id": "firstname",
            "label": "First name",
            "type": {
              "name": "string"
            },
            "validationConstraints": [
              {
                "name": "required",
                "configuration": null
              }
            ]
          },
          {
            "id": "lastname",
            "label": "Last name",
            "type": {
              "name": "string"
            },
            "validationConstraints": [
              {
                "name": "required",
                "configuration": null
              }
            ]
          },
          {
            "id": "title",
            "label": "Title",
            "type": {
              "name": "string"
            }
          },
          {
            "id": "email",
            "label": "Email",
            "type": {
              "name": "email"
            },
            "validationConstraints": [
              {
                "name": "required",
                "configuration": null
              }
            ]
          },
          {
            "id": "location",
            "label": "Location",
            "type": {
              "name": "location"
            },
            "validationConstraints": [
              {
                "name": "required",
                "configuration": null
              }
            ]
          },
          {
            "id": "scientificAreas",
            "label": "Scientific areas",
            "type": {
              "values": areas,
              "name": "multiple_enum_scientific_areas"
            },
            "validationConstraints": [
              {
                "name": "required",
                "configuration": null
              }
            ]
          }
        ];
        this.form = this.util.createGenericForm(this.formFields);
        console.dir("IHE");
      },
      () => {
        this.formFields = []; 
      }
    );
  }
}
