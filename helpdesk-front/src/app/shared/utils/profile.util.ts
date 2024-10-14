import {ThemeOption} from "@model//user-profile-form-control.model";
import {Theme, ThemeNames} from "@enums//theme.enum";
import {Profile} from "@enums//profile.enum";
import {UserProfilePut} from "@model//user-profile.model";


export class ProfileUtils {
  static getAvailableThemes(): ThemeOption[] {
    return Object.values(Theme)
      .filter((value): value is Theme => typeof value === 'number')
      .map(theme => ({value: theme, name: ThemeNames[theme]}));
  }

  static getProfileName(profile: Profile | null | undefined): string {
    switch (profile) {
      case Profile.ADMIN:
        return 'Administrador';
      case Profile.USER:
        return 'Usuário';
      case Profile.TECHNICIAN:
        return 'Técnico';
      case null:
      case undefined:
        throw new Error('Perfil não pode ser nulo ou indefinido');
      default:
        throw new Error(`Perfil inválido: ${profile}`);
    }
  }

  static getUpdatedProfile(formValue: any, originalTheme: Theme, isPasswordFieldsVisible: boolean): UserProfilePut {
    const updatedProfile: UserProfilePut = {};

    if (originalTheme !== formValue.theme) {
      updatedProfile.theme = formValue.theme;
    }

    if (isPasswordFieldsVisible) {
      if (formValue.currentPassword && formValue.newPassword && formValue.confirmNewPassword) {
        if (formValue.newPassword !== formValue.confirmNewPassword) {
          throw new Error('As senhas não coincidem.');
        }
        updatedProfile.currentPassword = formValue.currentPassword;
        updatedProfile.newPassword = formValue.newPassword;
      } else if (formValue.currentPassword || formValue.newPassword || formValue.confirmNewPassword) {
        throw new Error('Preencha todos os campos de senha para atualizar.');
      }
    }

    return updatedProfile;
  }
}
