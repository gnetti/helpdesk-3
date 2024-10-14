import { FormControl } from '@angular/forms';
import { UserProfileGet } from "./user-profile.model";
import { Theme } from "@enums//theme.enum";

export type UserProfileFormControlModel = {
  [K in keyof UserProfileGet]: FormControl<UserProfileGet[K]>;
} & {
  currentPassword: FormControl<string>;
  newPassword: FormControl<string>;
  confirmNewPassword: FormControl<string>;
};

export interface PasswordStrength {
  strength: number;
  errors: string[];
  passedRules: string[];
  isStrong: boolean;
}

export interface ThemeOption {
  value: Theme;
  name: string;
}

export class ProfileValidator {
  static validatePasswordFields(currentPassword: string, newPassword: string, confirmNewPassword: string): void {
    if (!currentPassword || !newPassword || !confirmNewPassword) {
      throw new Error('Preencha todos os campos de senha para atualizar.');
    }
    if (newPassword !== confirmNewPassword) {
      throw new Error('As senhas não coincidem.');
    }
  }
}

export class ProfileHelper {
  static readonly PASSWORD_PLACEHOLDER = '**********';

  static initializePasswordStrength(): PasswordStrength {
    return {
      strength: 0,
      errors: [],
      passedRules: [],
      isStrong: false
    };
  }

  static getPasswordPlaceholders(): { [key: string]: string } {
    return {
      currentPassword: this.PASSWORD_PLACEHOLDER,
      newPassword: '',
      confirmNewPassword: ''
    };
  }
}
