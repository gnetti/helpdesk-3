import {ThemeOption} from "@model//settings-form-control.model";
import {Theme, ThemeNames} from "@enums//theme.enum";
import {Profile} from "@enums//profile.enum";
import {UserSettingsPut} from "@model//user-settings.model";


export class SettingsUtils {
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

  static getUpdatedSettings(formValue: any, originalTheme: Theme, isPasswordFieldsVisible: boolean): UserSettingsPut {
    const updatedSettings: UserSettingsPut = {};

    if (originalTheme !== formValue.theme) {
      updatedSettings.theme = formValue.theme;
    }

    if (isPasswordFieldsVisible) {
      if (formValue.currentPassword && formValue.newPassword && formValue.confirmNewPassword) {
        if (formValue.newPassword !== formValue.confirmNewPassword) {
          throw new Error('As senhas não coincidem.');
        }
        updatedSettings.currentPassword = formValue.currentPassword;
        updatedSettings.newPassword = formValue.newPassword;
      } else if (formValue.currentPassword || formValue.newPassword || formValue.confirmNewPassword) {
        throw new Error('Preencha todos os campos de senha para atualizar.');
      }
    }

    return updatedSettings;
  }
}
