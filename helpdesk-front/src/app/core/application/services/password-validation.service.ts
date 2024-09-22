import {Injectable} from '@angular/core';
import {AbstractControl} from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class PasswordValidationService {
  validatePassword(control: AbstractControl): string[] {
    const errors: string[] = [];
    if (control.errors) {
      if (control.errors['required']) errors.push('required');
      if (control.errors['minlength']) errors.push('minlength');
      if (control.errors['maxlength']) errors.push('maxlength');
      if (control.errors['invalidPassword']) {
        const invalidPassword = control.errors['invalidPassword'];
        if (invalidPassword.upperCase) errors.push('upperCase');
        if (invalidPassword.lowerCase) errors.push('lowerCase');
        if (invalidPassword.numeric) errors.push('numeric');
        if (invalidPassword.specialChar) errors.push('specialChar');
      }
    }
    return errors;
  }
}
