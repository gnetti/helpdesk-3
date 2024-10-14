import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

export class TokenTimeValidator {
  static validateTokenExpiration(control: AbstractControl): ValidationErrors | null {
    const value = control.value as number;

    if (value == null) {
      return { "tokenExpirationTimeMinutesRequired": "O campo Tempo de Expiração do Token é obrigatório." };
    }
    if (value < 60) {
      return { "tokenExpirationTimeMinutesMin": "O valor deve ser pelo menos 60 minutos." };
    }
    if (value > 1440) {
      return { "tokenExpirationTimeMinutesMax": "O valor não pode ser maior que 1440 minutos." };
    }
    return null;
  }

  static validateTimeToShowDialog(control: AbstractControl): ValidationErrors | null {
    const value = control.value as number;

    if (value == null) {
      return { "timeToShowDialogMinutesRequired": "O campo Tempo para Exibir Diálogo de Token é obrigatório." };
    }
    if (value < 15) {
      return { "timeToShowDialogMinutesMin": "O tempo para exibir o diálogo deve ser pelo menos 15 minutos." };
    }
    if (value > 30) {
      return { "timeToShowDialogMinutesMax": "O tempo para exibir o diálogo não pode ser maior que 30 minutos." };
    }
    return null;
  }

  static validateTokenUpdateInterval(control: AbstractControl): ValidationErrors | null {
    const value = control.value as number;

    if (value == null) {
      return { "tokenUpdateIntervalMinutesRequired": "O campo Intervalo de Atualização do Token é obrigatório." };
    }
    if (value < 1) {
      return { "tokenUpdateIntervalMinutesMin": "O intervalo de atualização do token deve ser pelo menos 1 minuto." };
    }
    if (value > 5) {
      return { "tokenUpdateIntervalMinutesMax": "O intervalo de atualização do token não pode ser maior que 5 minutos." };
    }
    return null;
  }

  static validateTokenTime(): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const tokenExpirationTimeControl = formGroup.get("tokenExpirationTimeMinutes");
      const timeToShowDialogControl = formGroup.get("timeToShowDialogMinutes");
      const tokenUpdateIntervalControl = formGroup.get("tokenUpdateIntervalMinutes");

      if (!tokenExpirationTimeControl || !timeToShowDialogControl || !tokenUpdateIntervalControl) {
        return null;
      }

      const tokenExpirationTime = tokenExpirationTimeControl.value as number;
      const timeToShowDialog = timeToShowDialogControl.value as number;
      const errors: ValidationErrors = {};

      const tokenExpirationTimeErrors = TokenTimeValidator.validateTokenExpiration(tokenExpirationTimeControl);
      const timeToShowDialogErrors = TokenTimeValidator.validateTimeToShowDialog(timeToShowDialogControl);
      const tokenUpdateIntervalErrors = TokenTimeValidator.validateTokenUpdateInterval(tokenUpdateIntervalControl);

      if (tokenExpirationTimeErrors) {
        Object.assign(errors, tokenExpirationTimeErrors);
      }
      if (timeToShowDialogErrors) {
        Object.assign(errors, timeToShowDialogErrors);
      }
      if (tokenUpdateIntervalErrors) {
        Object.assign(errors, tokenUpdateIntervalErrors);
      }

      if (tokenExpirationTime != null && timeToShowDialog != null && tokenExpirationTime - timeToShowDialog < 15) {
        errors["inadequateWarningTime"] = "O tempo para exibir o diálogo deve garantir pelo menos 15 minutos antes da expiração do Token.";
        timeToShowDialogControl.setErrors({
          ...timeToShowDialogControl.errors,
          "inadequateWarningTime": "O tempo para exibir o diálogo deve garantir pelo menos 15 minutos antes da expiração do Token."
        });
      } else {
        if (timeToShowDialogControl.errors && timeToShowDialogControl.errors["inadequateWarningTime"]) {
          const { inadequateWarningTime, ...rest } = timeToShowDialogControl.errors;
          timeToShowDialogControl.setErrors(Object.keys(rest).length ? rest : null);
        }
      }

      return Object.keys(errors).length ? errors : null;
    };
  }
}
