import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from "rxjs";
import { PROFILE_USE_CASE_PORT, ProfileUseCasePort } from "@domain/ports/in/profile-use-case.port";
import { ThemeService } from "@application/services/theme.service";
import { takeUntil } from "rxjs/operators";
import { MatSelectChange } from "@angular/material/select";
import {
  PasswordStrength,
  UserProfileFormControlModel,
  ProfileHelper,
  ThemeOption
} from "@model//user-profile-form-control.model";
import {ProfileUtils} from "@utils//profile.util";
import {Theme} from "@enums//theme.enum";
import {CryptoService} from "@security//crypto.service";
import {ProfileValidator} from "@validators//profile.validator";
import {Profile} from "@enums//profile.enum";
import {PasswordStrengthValidator} from "@validators//Password-strength.validator";
import {UserProfileGet, UserProfileResponse} from "@model//user-profile.model";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, OnDestroy {
  profileForm!: FormGroup<UserProfileFormControlModel>;
  isPasswordFieldsVisible = false;
  isCancelButtonVisible = false;
  availableThemes: ThemeOption[] = ProfileUtils.getAvailableThemes();
  isSaveEnabled = false;
  originalTheme!: Theme;
  showPassword = {currentPassword: false,
    newPassword: false, confirmNewPassword: false};
  passwordErrors: any = {};
  private destroy$ = new Subject<void>();
  hasClickedCurrentPassword = false;
  passwordStrength: PasswordStrength = ProfileHelper.initializePasswordStrength();
  passwordPlaceholder = ProfileHelper.getPasswordPlaceholders();

  constructor(
    @Inject(PROFILE_USE_CASE_PORT) private profileUseCase: ProfileUseCasePort,
    private fb: FormBuilder,
    private themeService: ThemeService,
    private cryptoService: CryptoService,
    private profileValidator: ProfileValidator
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadUserProfile();
    this.setupFormChangeListeners();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.profileForm = this.fb.group<UserProfileFormControlModel>({
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
      this.profileForm.get(control)?.disable();
    });
  }

  private setupFormChangeListeners(): void {
    this.profileForm.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.updateSaveButtonState();
    });

    this.setupPasswordChangeListeners();
  }

  private setupPasswordChangeListeners(): void {
    ['newPassword', 'confirmNewPassword'].forEach(field => {
      this.profileForm.get(field)?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
        this.validatePassword(field);
        this.handlePasswordFieldChange(field);
      });
    });
  }

  private handlePasswordFieldChange(field: string): void {
    field === 'newPassword' ? this.updatePasswordStrength() : this.profileForm.updateValueAndValidity();
  }

  loadUserProfile(): void {
    this.profileUseCase.getCurrentUser().pipe(takeUntil(this.destroy$)).subscribe({
      next: this.handleUserProfileLoaded.bind(this),
      error: this.profileValidator.showLoadError.bind(this.profileValidator)
    });
  }

  private handleUserProfileLoaded(profile: UserProfileGet): void {
    this.patchFormWithProfile(profile);
    this.themeService.setTheme(profile.theme);
    this.originalTheme = profile.theme;
    this.updateSaveButtonState();
  }

  private patchFormWithProfile(profile: UserProfileGet): void {
    this.profileForm.patchValue({
      id: profile.id,
      name: profile.name,
      email: profile.email,
      profile: profile.profile,
      theme: profile.theme,
    });
  }

  updateProfile(): void {
    this.profileValidator.validateForm(this.profileForm.valid);

    if (!this.profileForm.valid) {
      return;
    }

    try {
      const updatedProfile = ProfileUtils.getUpdatedProfile(
        this.profileForm.getRawValue(),
        this.originalTheme,
        this.isPasswordFieldsVisible
      );
      this.processUpdatedProfile(updatedProfile);
    } catch (error: any) {
      this.profileValidator.showUpdateError(error);
    }
  }

  private processUpdatedProfile(updatedProfile: any): void {
    this.profileValidator.validateUpdatedProfile(updatedProfile);

    if (Object.keys(updatedProfile).length === 0) {
      return;
    }

    this.encryptPasswordFields(updatedProfile);
    this.profileUseCase.updateCurrentUser(updatedProfile).pipe(takeUntil(this.destroy$)).subscribe({
      next: this.onUpdateSuccess.bind(this),
      error: this.onUpdateError.bind(this)
    });
  }

  private encryptPasswordFields(updatedProfile: any): void {
    ['currentPassword', 'newPassword'].forEach(field => {
      if (updatedProfile[field]) {
        updatedProfile[field] = this.cryptoService.encrypt(updatedProfile[field]);
      }
    });
  }

  private onUpdateSuccess(updatedProfile: UserProfileResponse): void {
    this.themeService.setTheme(updatedProfile.theme);
    this.originalTheme = updatedProfile.theme;
    this.hidePasswordFields();
    this.profileForm.markAsPristine();
    this.updateSaveButtonState();
    this.profileValidator.showUpdateSuccess();
  }

  private onUpdateError(error: any): void {
    this.profileValidator.showUpdateError(error);
    this.hidePasswordFields();
    this.profileForm.markAsPristine();
    this.updateSaveButtonState();
  }

  showPasswordFields(): void {
    this.isPasswordFieldsVisible = true;
    this.isCancelButtonVisible = true;
    this.setPasswordValidators();
    this.updateFormValidityAndState();
  }

  private setPasswordValidators(): void {
    this.profileForm.get('currentPassword')?.setValidators([Validators.required]);
    this.profileForm.get('newPassword')?.setValidators([Validators.required, PasswordStrengthValidator.validate]);
    this.profileForm.get('confirmNewPassword')?.setValidators([Validators.required]);
  }

  hidePasswordFields(): void {
    this.isPasswordFieldsVisible = false;
    this.isCancelButtonVisible = false;
    this.resetPasswordFields();
    this.clearPasswordValidators();
    this.updateFormValidityAndState();
    this.hasClickedCurrentPassword = false;
    this.passwordStrength = ProfileHelper.initializePasswordStrength();
  }

  private resetPasswordFields(): void {
    this.profileForm.patchValue({
      currentPassword: this.passwordPlaceholder['currentPassword'],
      newPassword: '',
      confirmNewPassword: ''
    });
  }

  private clearPasswordValidators(): void {
    ['currentPassword', 'newPassword', 'confirmNewPassword'].forEach(field => {
      this.profileForm.get(field)?.clearValidators();
    });
  }

  private updateFormValidityAndState(): void {
    ['currentPassword', 'newPassword', 'confirmNewPassword'].forEach(field => {
      this.profileForm.get(field)?.updateValueAndValidity();
    });
    this.updateSaveButtonState();
  }

  updateSaveButtonState(): void {
    this.isSaveEnabled = this.profileForm.dirty && this.profileForm.valid;
  }

  togglePasswordVisibility(field: 'currentPassword' | 'newPassword' | 'confirmNewPassword'): void {
    this.showPassword[field] = !this.showPassword[field];
    this.handleCurrentPasswordClick(field);
  }

  private handleCurrentPasswordClick(field: string): void {
    if (field === 'currentPassword' && !this.hasClickedCurrentPassword) {
      this.profileForm.patchValue({currentPassword: ''});
      this.hasClickedCurrentPassword = true;
      this.showPasswordFields();
    }
  }

  private validatePassword(field: string): void {
    const control = this.profileForm.get(field);
    if (control?.invalid && (control.dirty || control.touched)) {
      this.passwordErrors[field] = control.errors;
    } else {
      delete this.passwordErrors[field];
    }
  }

  protected updatePasswordStrength(): void {
    const password = this.profileForm.get('newPassword')?.value ?? '';
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
    this.profileForm.get('theme')?.setValue(newTheme);
    this.themeService.setTheme(newTheme);
    this.updateSaveButtonState();
  }

  getProfileName(profile: Profile | null | undefined): string {
    return ProfileUtils.getProfileName(profile);
  }

  onPasswordFocus(field: string): void {
    this.clearPasswordPlaceholder(field);
    this.handleCurrentPasswordFocus(field);
  }

  private clearPasswordPlaceholder(field: string): void {
    if (this.profileForm.get(field)?.value === this.passwordPlaceholder[field]) {
      this.profileForm.get(field)?.setValue('');
    }
  }

  private handleCurrentPasswordFocus(field: string): void {
    if (field === 'currentPassword' && !this.hasClickedCurrentPassword) {
      this.hasClickedCurrentPassword = true;
      this.showPasswordFields();
    }
  }

  onPasswordBlur(field: string): void {
    if (!this.profileForm.get(field)?.value) {
      this.profileForm.get(field)?.setValue(this.passwordPlaceholder[field]);
    }
  }
}
