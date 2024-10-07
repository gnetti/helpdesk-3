import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from "rxjs";
import { SETTINGS_USE_CASE_PORT, SettingsUseCasePort } from "@domain/ports/in/settings-use-case.port";
import { ThemeService } from "@application/services/theme.service";
import { takeUntil } from "rxjs/operators";
import { MatSelectChange } from "@angular/material/select";
import {
  PasswordStrength,
  SettingsFormControlModel,
  SettingsHelper,
  ThemeOption
} from "@model//settings-form-control.model";
import {SettingsUtils} from "@utils//settings.util";
import {Theme} from "@enums//theme.enum";
import {CryptoService} from "@security//crypto.service";
import {SettingsValidator} from "@validators//settings.validator";
import {Profile} from "@enums//profile.enum";
import {PasswordStrengthValidator} from "@validators//Password-strength.validator";
import {UserSettingsGet, UserSettingsResponse} from "@model//user-settings.model";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit, OnDestroy {
  settingsForm!: FormGroup<SettingsFormControlModel>;
  isPasswordFieldsVisible = false;
  isCancelButtonVisible = false;
  availableThemes: ThemeOption[] = SettingsUtils.getAvailableThemes();
  isSaveEnabled = false;
  originalTheme!: Theme;
  showPassword = {currentPassword: false,
    newPassword: false, confirmNewPassword: false};
  passwordErrors: any = {};
  private destroy$ = new Subject<void>();
  hasClickedCurrentPassword = false;
  passwordStrength: PasswordStrength = SettingsHelper.initializePasswordStrength();
  passwordPlaceholder = SettingsHelper.getPasswordPlaceholders();

  constructor(
    @Inject(SETTINGS_USE_CASE_PORT) private settingsUseCase: SettingsUseCasePort,
    private fb: FormBuilder,
    private themeService: ThemeService,
    private cryptoService: CryptoService,
    private settingsValidator: SettingsValidator
  ) {}

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
    this.settingsForm = this.fb.group<SettingsFormControlModel>({
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

    this.disableFormControls();
  }

  private disableFormControls(): void {
    ['id', 'name', 'email', 'profile'].forEach(control => {
      this.settingsForm.get(control)?.disable();
    });
  }

  private setupFormChangeListeners(): void {
    this.settingsForm.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.updateSaveButtonState();
    });

    this.setupPasswordChangeListeners();
  }

  private setupPasswordChangeListeners(): void {
    ['newPassword', 'confirmNewPassword'].forEach(field => {
      this.settingsForm.get(field)?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
        this.validatePassword(field);
        this.handlePasswordFieldChange(field);
      });
    });
  }

  private handlePasswordFieldChange(field: string): void {
    field === 'newPassword' ? this.updatePasswordStrength() : this.settingsForm.updateValueAndValidity();
  }

  loadUserSettings(): void {
    this.settingsUseCase.getCurrentUser().pipe(takeUntil(this.destroy$)).subscribe({
      next: this.handleUserSettingsLoaded.bind(this),
      error: this.settingsValidator.showLoadError.bind(this.settingsValidator)
    });
  }

  private handleUserSettingsLoaded(settings: UserSettingsGet): void {
    this.patchFormWithSettings(settings);
    this.themeService.setTheme(settings.theme);
    this.originalTheme = settings.theme;
    this.updateSaveButtonState();
  }

  private patchFormWithSettings(settings: UserSettingsGet): void {
    this.settingsForm.patchValue({
      id: settings.id,
      name: settings.name,
      email: settings.email,
      profile: settings.profile,
      theme: settings.theme,
    });
  }

  updateSettings(): void {
    this.settingsValidator.validateForm(this.settingsForm.valid);

    if (!this.settingsForm.valid) {
      return;
    }

    try {
      const updatedSettings = SettingsUtils.getUpdatedSettings(
        this.settingsForm.getRawValue(),
        this.originalTheme,
        this.isPasswordFieldsVisible
      );
      this.processUpdatedSettings(updatedSettings);
    } catch (error: any) {
      this.settingsValidator.showUpdateError(error);
    }
  }

  private processUpdatedSettings(updatedSettings: any): void {
    this.settingsValidator.validateUpdatedSettings(updatedSettings);

    if (Object.keys(updatedSettings).length === 0) {
      return;
    }

    this.encryptPasswordFields(updatedSettings);
    this.settingsUseCase.updateCurrentUser(updatedSettings).pipe(takeUntil(this.destroy$)).subscribe({
      next: this.onUpdateSuccess.bind(this),
      error: this.onUpdateError.bind(this)
    });
  }

  private encryptPasswordFields(updatedSettings: any): void {
    ['currentPassword', 'newPassword'].forEach(field => {
      if (updatedSettings[field]) {
        updatedSettings[field] = this.cryptoService.encrypt(updatedSettings[field]);
      }
    });
  }

  private onUpdateSuccess(updatedSettings: UserSettingsResponse): void {
    this.themeService.setTheme(updatedSettings.theme);
    this.originalTheme = updatedSettings.theme;
    this.hidePasswordFields();
    this.settingsForm.markAsPristine();
    this.updateSaveButtonState();
    this.settingsValidator.showUpdateSuccess();
  }

  private onUpdateError(error: any): void {
    this.settingsValidator.showUpdateError(error);
    this.hidePasswordFields();
    this.settingsForm.markAsPristine();
    this.updateSaveButtonState();
  }

  showPasswordFields(): void {
    this.isPasswordFieldsVisible = true;
    this.isCancelButtonVisible = true;
    this.setPasswordValidators();
    this.updateFormValidityAndState();
  }

  private setPasswordValidators(): void {
    this.settingsForm.get('currentPassword')?.setValidators([Validators.required]);
    this.settingsForm.get('newPassword')?.setValidators([Validators.required, PasswordStrengthValidator.validate]);
    this.settingsForm.get('confirmNewPassword')?.setValidators([Validators.required]);
  }

  hidePasswordFields(): void {
    this.isPasswordFieldsVisible = false;
    this.isCancelButtonVisible = false;
    this.resetPasswordFields();
    this.clearPasswordValidators();
    this.updateFormValidityAndState();
    this.hasClickedCurrentPassword = false;
    this.passwordStrength = SettingsHelper.initializePasswordStrength();
  }

  private resetPasswordFields(): void {
    this.settingsForm.patchValue({
      currentPassword: this.passwordPlaceholder['currentPassword'],
      newPassword: '',
      confirmNewPassword: ''
    });
  }

  private clearPasswordValidators(): void {
    ['currentPassword', 'newPassword', 'confirmNewPassword'].forEach(field => {
      this.settingsForm.get(field)?.clearValidators();
    });
  }

  private updateFormValidityAndState(): void {
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
    this.handleCurrentPasswordClick(field);
  }

  private handleCurrentPasswordClick(field: string): void {
    if (field === 'currentPassword' && !this.hasClickedCurrentPassword) {
      this.settingsForm.patchValue({currentPassword: ''});
      this.hasClickedCurrentPassword = true;
      this.showPasswordFields();
    }
  }

  private validatePassword(field: string): void {
    const control = this.settingsForm.get(field);
    if (control?.invalid && (control.dirty || control.touched)) {
      this.passwordErrors[field] = control.errors;
    } else {
      delete this.passwordErrors[field];
    }
  }

  protected updatePasswordStrength(): void {
    const password = this.settingsForm.get('newPassword')?.value ?? '';
    const strengthResult = PasswordStrengthValidator.getPasswordStrength(password);
    this.updatePasswordStrengthState(strengthResult);
  }

  private updatePasswordStrengthState(strengthResult: PasswordStrength): void {
    if (this.passwordStrength?.isStrong) {
      return;
    }
    strengthResult.isStrong ? this.setPasswordStrengthToStrong(strengthResult) : this.setPasswordStrengthToWeak(strengthResult);
  }

  private setPasswordStrengthToStrong(strengthResult: PasswordStrength): void {
    this.passwordStrength = {
      ...strengthResult,
      strength: 100,
      isStrong: true
    };
    this.schedulePasswordStrengthReset();
  }

  private setPasswordStrengthToWeak(strengthResult: PasswordStrength): void {
    this.passwordStrength = {
      ...strengthResult,
      isStrong: false
    };
  }

  private schedulePasswordStrengthReset(): void {
    setTimeout(() => {
      this.passwordStrength = {
        ...this.passwordStrength,
        strength: -1
      };
    }, 1500);
  }

  onThemeChange(event: MatSelectChange): void {
    const newTheme = event.value as Theme;
    this.settingsForm.get('theme')?.setValue(newTheme);
    this.themeService.setTheme(newTheme);
    this.updateSaveButtonState();
  }

  getProfileName(profile: Profile | null | undefined): string {
    return SettingsUtils.getProfileName(profile);
  }

  onPasswordFocus(field: string): void {
    this.clearPasswordPlaceholder(field);
    this.handleCurrentPasswordFocus(field);
  }

  private clearPasswordPlaceholder(field: string): void {
    if (this.settingsForm.get(field)?.value === this.passwordPlaceholder[field]) {
      this.settingsForm.get(field)?.setValue('');
    }
  }

  private handleCurrentPasswordFocus(field: string): void {
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
