import {AbstractControl, FormGroup, ValidationErrors, ValidatorFn} from '@angular/forms';

export class PasswordStrengthValidator {

  private static readonly RULES: Array<[RegExp, string, string, string]> = [
    [/^.{10,15}$/, 'length', 'A senha deve ter entre 10 e 15 caracteres.', 'Entre 10 e 15 caracteres'],
    [/\d/, 'requireDigit', 'A senha deve conter pelo menos um número.', 'Número'],
    [/[A-Z]/, 'requireUppercase', 'A senha deve conter pelo menos uma letra maiúscula.', 'Letra maiúscula'],
    [/[a-z]/, 'requireLowercase', 'A senha deve conter pelo menos uma letra minúscula.', 'Letra minúscula'],
    [/[!@#$%^&*(),.?":{}|<>]/, 'requireSpecialChar', 'A senha deve conter pelo menos um caractere especial.', 'Caractere especial']
  ];

  static validate(control: AbstractControl): ValidationErrors | null {
    const password = control.value as string;
    if (!password) return null;
    const errors = PasswordStrengthValidator.RULES.reduce((acc, [regex, key, message, shortMessage]) => {
      if (!regex.test(password)) {
        acc[key] = {message, shortMessage};
      }
      return acc;
    }, {} as ValidationErrors);

    return Object.keys(errors).length > 0 ? errors : null;
  }

  static getPasswordStrength(password: string): { strength: number, errors: string[], passedRules: string[], isStrong: boolean } {
    if (!password) return {strength: 0, errors: [], passedRules: [], isStrong: false};

    const passedRules = PasswordStrengthValidator.RULES.filter(([regex]) => regex.test(password));
    const errors = PasswordStrengthValidator.RULES
      .filter(([regex]) => !regex.test(password))
      .map(([, , , shortMessage]) => shortMessage);
    const passedRuleMessages = passedRules.map(([, , , shortMessage]) => shortMessage);
    let strength = 0;
    if (password.length >= 10) {
      strength = ((passedRules.length - 1) / (PasswordStrengthValidator.RULES.length - 1)) * 100;
    } else {
      strength = (passedRules.length / PasswordStrengthValidator.RULES.length) * (password.length / 10) * 100;
    }
    strength = Math.min(Math.round(strength), 100);
    const isStrong = passedRules.length === PasswordStrengthValidator.RULES.length;
    return {strength, errors, passedRules: passedRuleMessages, isStrong};
  }

  static passwordsMatch: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    const formGroup = control as FormGroup;
    const newPassword = formGroup.get('newPassword')?.value;
    const confirmNewPassword = formGroup.get('confirmNewPassword')?.value;
    if (newPassword !== confirmNewPassword) {
      formGroup.get('confirmNewPassword')?.setErrors({passwordsMismatch: true});
      return {passwordsMismatch: true};
    } else {
      formGroup.get('confirmNewPassword')?.setErrors(null);
      return null;
    }
  }
}
