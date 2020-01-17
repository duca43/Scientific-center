import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formFields'
})
export class FormFieldsPipe implements PipeTransform {

  transform(formFields: [], contain: boolean): any {
    if (!formFields) return null;
    return formFields.filter((formField: any) => contain == false
      ? formField.type.name !== 'boolean'
      : formField.type.name === 'boolean'); 
      // ? formField.type.name !== 'boolean' && formField.type.name !== 'location' 
      // : formField.type.name === 'boolean' || formField.type.name === 'location');
  }
}
