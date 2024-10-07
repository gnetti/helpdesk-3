import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ValidationErrors, Validators} from '@angular/forms';
import {ToastrService} from "ngx-toastr";
import {SETTINGS_USE_CASE_PORT, SettingsUseCasePort} from "@domain/ports/in/settings-use-case.port";
import {ThemeService} from "@application/services/theme.service";
import {Subject, takeUntil} from 'rxjs';
import {Theme, ThemeNames} from "@core/domain/enums/theme.enum";
import {CryptoService} from "@security//crypto.service";
import {UserSettingsGet, UserSettingsPut, UserSettingsResponse} from "@model//user-settings.model";
import {PasswordStrengthValidator} from "@validators//PasswordStrengthValidator";
import {Profile} from "@enums//profile.enum";
import {MatSelectChange} from "@angular/material/select";

type SettingsFormControls = {
  [K in keyof UserSettingsGet]-?: FormControl<UserSettingsGet[K]>;
} & {
  currentPassword: FormControl<string>;
  newPassword: FormControl<string>;
  confirmNewPassword: FormControl<string>;
};

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit, OnDestroy {
  settingsForm!: FormGroup<SettingsFormControls>;
  isPasswordFieldsVisible = false;
  isCancelButtonVisible = false;
  availableThemes: { value: Theme; name: string }[] = Object.values(Theme)
    .filter((value): value is Theme => typeof value === 'number')
    .map(theme => ({value: theme, name: ThemeNames[theme]}));
  isSaveEnabled = false;
  originalTheme!: Theme;
  showPassword = {currentPassword: false, newPassword: false, confirmNewPassword: false};
  passwordErrors: any = {};
  private destroy$ = new Subject<void>();
  hasClickedCurrentPassword = false;
  passwordStrength: { strength: number; errors: string[]; passedRules: string[]; isStrong: boolean } = {
    strength: 0,
    errors: [],
    passedRules: [],
    isStrong: false
  };
  passwordPlaceholder: { [key: string]: string } = {
    currentPassword: '**********',
    newPassword: '',
    confirmNewPassword: ''
  };

  constructor(
    @Inject(SETTINGS_USE_CASE_PORT) private settingsUseCase: SettingsUseCasePort,
    private fb: FormBuilder,
    private themeService: ThemeService,
    private toast: ToastrService,
    private cryptoService: CryptoService
  ) {
  }

  ngOnInit(): void {
    this.initForm();
    this.loadUserSettings();
    this.setupFormChangeListeners();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.settingsForm = new FormGroup<SettingsFormControls>({
      id: this.fb.nonNullable.control(0),
      name: this.fb.nonNullable.control(''),
      email: this.fb.nonNullable.control(''),
      profile: this.fb.nonNullable.control(Profile.USER),
      theme: this.fb.nonNullable.control(0 as Theme),
      currentPassword: this.fb.nonNullable.control(this.passwordPlaceholder['currentPassword']),
      newPassword: this.fb.nonNullable.control('', [Validators.required, PasswordStrengthValidator.validate]),
      confirmNewPassword: this.fb.nonNullable.control('', Validators.required),
    }, {
      validators: PasswordStrengthValidator.passwordsMatch
    });

    this.settingsForm.controls.id.disable();
    this.settingsForm.controls.name.disable();
    this.settingsForm.controls.email.disable();
    this.settingsForm.controls.profile.disable();
  }

  private setupFormChangeListeners(): void {
    this.settingsForm.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.updateSaveButtonState();
    });

    this.settingsForm.get('newPassword')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.validatePassword('newPassword');
      this.updatePasswordStrength();
    });
    this.settingsForm.get('confirmNewPassword')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.validatePassword('confirmNewPassword');
      this.settingsForm.updateValueAndValidity();
    });
  }

  loadUserSettings(): void {
    this.settingsUseCase.getCurrentUser().pipe(takeUntil(this.destroy$)).subscribe({
      next: (settings: UserSettingsGet) => {
        this.patchFormWithSettings(settings);
        this.themeService.setTheme(settings.theme);
        this.originalTheme = settings.theme;
        this.updateSaveButtonState();
      },
      error: () => this.toast.error('Erro ao carregar configurações de usuário.', 'Erro')
    });
  }

  patchFormWithSettings(settings: UserSettingsGet): void {
    this.settingsForm.patchValue({
      id: settings.id,
      name: settings.name,
      email: settings.email,
      profile: settings.profile,
      theme: settings.theme,
    });
  }

  updateSettings(): void {
    if (this.settingsForm.valid) {
      const updatedSettings = this.getUpdatedSettings();
      if (Object.keys(updatedSettings).length) {
        this.settingsUseCase.updateCurrentUser(updatedSettings).pipe(takeUntil(this.destroy$)).subscribe({
          next: (response: UserSettingsResponse) => {
            this.onUpdateSuccess(response);
          },
          error: (error: any) => {
            this.onUpdateError(error);
          }
        });
      } else {
        this.toast.info('Nenhuma alteração detectada.', 'Info');
      }
    } else {
      this.toast.error('Formulário inválido. Por favor, corrija os erros.', 'Erro');
    }
  }

  getUpdatedSettings(): UserSettingsPut {
    const formValue = this.settingsForm.getRawValue();
    const updatedSettings: UserSettingsPut = {};

    if (this.originalTheme !== formValue.theme) {
      updatedSettings.theme = formValue.theme;
    }

    if (this.isPasswordFieldsVisible) {
      this.handlePasswordUpdate(formValue, updatedSettings);
    }

    return updatedSettings;
  }

  handlePasswordUpdate(formValue: any, updatedSettings: UserSettingsPut): void {
    if (formValue.currentPassword && formValue.newPassword && formValue.confirmNewPassword) {
      if (formValue.newPassword !== formValue.confirmNewPassword) {
        this.toast.error('As senhas não coincidem.', 'Erro');
        return;
      }
      updatedSettings.currentPassword = this.cryptoService.encrypt(formValue.currentPassword);
      updatedSettings.newPassword = this.cryptoService.encrypt(formValue.newPassword);
    } else if (formValue.currentPassword || formValue.newPassword || formValue.confirmNewPassword) {
      this.toast.error('Preencha todos os campos de senha para atualizar.', 'Erro');
    }
  }

  onUpdateSuccess(updatedSettings: UserSettingsResponse): void {
    this.themeService.setTheme(updatedSettings.theme);
    this.originalTheme = updatedSettings.theme;
    this.hidePasswordFields();
    this.settingsForm.markAsPristine();
    this.updateSaveButtonState();
    this.toast.success('Configurações atualizadas com sucesso!', 'Sucesso');
  }

  onUpdateError(error: any): void {
    let errorMessage = 'Erro ao atualizar as configurações.';
    if (error.error?.message) {
      errorMessage = error.error.message;
    }
    this.toast.error(errorMessage, 'Erro');
    this.hidePasswordFields();
    this.settingsForm.markAsPristine();
    this.updateSaveButtonState();
  }

  showPasswordFields(): void {
    this.isPasswordFieldsVisible = true;
    this.isCancelButtonVisible = true;
    this.settingsForm.get('currentPassword')?.setValidators([Validators.required]);
    this.settingsForm.get('newPassword')?.setValidators([Validators.required, PasswordStrengthValidator.validate]);
    this.settingsForm.get('confirmNewPassword')?.setValidators([Validators.required]);
    this.updateFormValidityAndState();
  }

  hidePasswordFields(): void {
    this.isPasswordFieldsVisible = false;
    this.isCancelButtonVisible = false;
    this.settingsForm.patchValue({
      currentPassword: this.passwordPlaceholder['currentPassword'],
      newPassword: '',
      confirmNewPassword: ''
    });
    this.settingsForm.get('currentPassword')?.clearValidators();
    this.settingsForm.get('newPassword')?.clearValidators();
    this.settingsForm.get('confirmNewPassword')?.clearValidators();
    this.updateFormValidityAndState();
    this.hasClickedCurrentPassword = false;
    this.passwordStrength = {strength: 0, errors: [], passedRules: [], isStrong: false};
  }

  updateFormValidityAndState(): void {
    ['currentPassword', 'newPassword', 'confirmNewPassword'].forEach(field => {
      this.settingsForm.get(field)?.updateValueAndValidity();
    });
    this.updateSaveButtonState();
  }

  updateSaveButtonState(): void {
    this.isSaveEnabled = this.settingsForm.dirty && this.settingsForm.valid;
  }

  togglePasswordVisibility(field: 'currentPassword' | 'newPassword' | 'confirmNewPassword'): void {
    this.showPassword[field] = !this.showPassword[field];
    if (field === 'currentPassword' && !this.hasClickedCurrentPassword) {
      this.settingsForm.patchValue({currentPassword: ''});
      this.hasClickedCurrentPassword = true;
      this.showPasswordFields();
    }
  }

  validatePassword(field: string): void {
    const control = this.settingsForm.get(field);
    if (control?.invalid && (control.dirty || control.touched)) {
      this.passwordErrors[field] = control.errors;
    } else {
      delete this.passwordErrors[field];
    }
  }

  updatePasswordStrength(): void {
    const password = this.settingsForm.get('newPassword')?.value ?? '';
    const strengthResult = PasswordStrengthValidator.getPasswordStrength(password);
    if (this.passwordStrength?.isStrong) {
      return;
    }
    if (strengthResult.isStrong) {
      this.passwordStrength = {
        ...strengthResult,
        strength: 100,
        isStrong: true
      };
      setTimeout(() => {
        this.passwordStrength = {
          ...this.passwordStrength,
          strength: -1
        };
      }, 1500);
    } else {
      this.passwordStrength = {
        ...strengthResult,
        isStrong: false
      };
    }
  }

  onThemeChange(event: MatSelectChange): void {
    const newTheme = event.value as Theme;
    this.settingsForm.get('theme')?.setValue(newTheme);
    this.themeService.setTheme(newTheme);
    this.updateSaveButtonState();
  }

  getProfileName(profile: Profile | null | undefined): string {
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

  passwordsMatch(formGroup: FormGroup): ValidationErrors | null {
    const newPassword = formGroup.get('newPassword')?.value;
    const confirmNewPassword = formGroup.get('confirmNewPassword')?.value;
    return newPassword === confirmNewPassword ? null : {passwordsMismatch: true};
  }

  onPasswordFocus(field: string): void {
    if (this.settingsForm.get(field)?.value === this.passwordPlaceholder[field]) {
      this.settingsForm.get(field)?.setValue('');
    }
    if (field === 'currentPassword' && !this.hasClickedCurrentPassword) {
      this.hasClickedCurrentPassword = true;
      this.showPasswordFields();
    }
  }

  onPasswordBlur(field: string): void {
    if (!this.settingsForm.get(field)?.value) {
      this.settingsForm.get(field)?.setValue(this.passwordPlaceholder[field]);
    }
  }
}
