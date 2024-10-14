import { Component, Inject, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ToastrService } from "ngx-toastr";
import { AuthService } from "@application/services/auth.service";
import { Profile } from "@enums//profile.enum";
import { TokenTimeValidator } from "@validators//token-time.validator";
import { TokenTimeProfile } from "@model//token-time-profile.model";
import { ProfileUtils } from "@utils//profile.util";
import { BehaviorSubject, distinctUntilChanged, Subject } from "rxjs";
import { TOKEN_TIME_USE_CASE_PORT, TokenTimeUseCasePort } from "@domain/ports/in/token-time-use-case.port";
import { takeUntil } from "rxjs/operators";
import { bigDecimalValidator } from "@validators//big-decimal.validator";

@Component({
  selector: "app-settings",
  templateUrl: "./settings.component.html",
  styleUrls: ["./settings.component.css"]
})
export class SettingsComponent implements OnInit, OnDestroy {
  tokenTimeForm: FormGroup;
  profiles: { value: Profile; label: string }[] = [];
  isSaving: boolean = false;
  isRootUser: boolean = false;
  isDefaultSettings: boolean = false;
  formModified: boolean = false;
  private destroy$ = new Subject<void>();
  private selectedProfileSubject = new BehaviorSubject<Profile>(Profile.ADMIN);

  constructor(
    private fb: FormBuilder,
    private toast: ToastrService,
    private authService: AuthService,
    @Inject(TOKEN_TIME_USE_CASE_PORT) private tokenTimeUseCase: TokenTimeUseCasePort
  ) {
    this.tokenTimeForm = this.initForm();
  }

  ngOnInit(): void {
    this.isRootUser = this.authService.hasRole(Profile.ROOT);
    if (!this.isRootUser) {
      this.toast.error("Acesso não autorizado.", "Erro");
      return;
    }
    this.initProfiles();
    this.setupProfileChangeListener();

    this.selectedProfileSubject.pipe(
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(profile => {
      this.loadTokenTimeSettings(profile);
    });

    this.tokenTimeForm.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.formModified = !this.tokenTimeForm.pristine;
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): FormGroup {
    return this.fb.group({
      profileCode: [Profile.ADMIN, Validators.required],
      tokenExpirationTimeMinutes: ["", [
        Validators.required,
        TokenTimeValidator.validateTokenExpiration,
        bigDecimalValidator(60, 1440)
      ]],
      timeToShowDialogMinutes: ["", [
        Validators.required,
        TokenTimeValidator.validateTimeToShowDialog,
        bigDecimalValidator(15, 30)
      ]],
      dialogDisplayTimeForTokenUpdateMinutes: ["", [
        Validators.required,
        bigDecimalValidator(2, 15)
      ]],
      tokenUpdateIntervalMinutes: ["", [
        Validators.required,
        bigDecimalValidator(1, 5)
      ]]
    }, {
      validators: TokenTimeValidator.validateTokenTime()
    });
  }

  private initProfiles(): void {
    this.profiles = Object.values(Profile)
      .filter((value): value is Profile => typeof value === "number" && value !== Profile.ROOT)
      .map(profile => ({
        value: profile,
        label: ProfileUtils.getProfileName(profile)
      }));
  }

  private setupProfileChangeListener(): void {
    const profileCodeControl = this.tokenTimeForm.get("profileCode");
    if (profileCodeControl) {
      profileCodeControl.valueChanges.pipe(
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      ).subscribe((profile: Profile) => {
        this.selectedProfileSubject.next(profile);
      });
    }
  }

  loadTokenTimeSettings(profile: Profile): void {
    if (!this.isRootUser) {
      this.toast.error("Acesso não autorizado.", "Erro");
      return;
    }

    this.tokenTimeUseCase.findByProfile(profile).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (settings: TokenTimeProfile | null) => {
        if (settings) {
          this.tokenTimeForm.patchValue(settings, { emitEvent: false });
          this.isDefaultSettings = false;
        } else {
          const defaultSettings = this.getDefaultSettings();
          this.tokenTimeForm.patchValue({
            ...defaultSettings,
            profileCode: profile
          }, { emitEvent: false });
          this.isDefaultSettings = true;
          this.toast.info("Configurações padrão carregadas. Você pode editá-las e salvar para personalizar.", "Configurações Padrão");
        }
        this.tokenTimeForm.markAsPristine();
        this.formModified = false;
      },
      error: () => {
        this.toast.warning("Erro ao carregar configurações de token time. Configurações padrão serão usadas.", "Aviso");
        const defaultSettings = this.getDefaultSettings();
        this.tokenTimeForm.patchValue({
          ...defaultSettings,
          profileCode: profile
        }, { emitEvent: false });
        this.isDefaultSettings = true;
        this.tokenTimeForm.markAsPristine();
        this.formModified = false;
      }
    });
  }

  private getDefaultSettings(): Partial<TokenTimeProfile> {
    return {
      tokenExpirationTimeMinutes: 60.0,
      timeToShowDialogMinutes: 30.0,
      dialogDisplayTimeForTokenUpdateMinutes: 15.0,
      tokenUpdateIntervalMinutes: 5.0
    };
  }

  saveTokenTimeSettings(): void {
    if (!this.isRootUser) {
      this.toast.error("Acesso não autorizado.", "Erro");
      return;
    }

    if (this.tokenTimeForm.valid) {
      this.isSaving = true;
      const tokenTimeProfile: TokenTimeProfile = this.tokenTimeForm.value;

      const operation = this.isDefaultSettings
        ? this.tokenTimeUseCase.create(tokenTimeProfile)
        : this.tokenTimeUseCase.update(tokenTimeProfile.profileCode, tokenTimeProfile);

      operation.pipe(
        takeUntil(this.destroy$)
      ).subscribe({
        next: () => {
          this.loadTokenTimeSettings(this.selectedProfileSubject.value);
          this.isSaving = false;
          this.isDefaultSettings = false;
          this.formModified = false;
          this.tokenTimeForm.markAsPristine();
          this.toast.success("Configurações de token time atualizadas com sucesso!", "Sucesso");
        },
        error: () => {
          this.toast.error("Erro ao atualizar configurações de token time.", "Erro");
          this.isSaving = false;
        }
      });
    } else {
      this.toast.error("Formulário de configurações de token time inválido.", "Erro");
    }
  }
}
