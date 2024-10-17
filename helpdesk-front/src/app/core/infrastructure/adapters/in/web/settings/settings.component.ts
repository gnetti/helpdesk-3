import { Component, Inject, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, FormGroup } from "@angular/forms";
import { ToastrService } from "ngx-toastr";
import { AuthService } from "@application/services/auth.service";
import { Profile } from "@enums//profile.enum";
import { TokenTimeProfile } from "@model//token-time-profile.model";
import { BehaviorSubject, distinctUntilChanged, Subject } from "rxjs";
import { TOKEN_TIME_USE_CASE_PORT, TokenTimeUseCasePort } from "@domain/ports/in/token-time-use-case.port";
import { takeUntil } from "rxjs/operators";
import { SettingsUtil } from "@utils//settings.util";

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
    this.tokenTimeForm = SettingsUtil.initForm(this.fb);
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

  private initProfiles(): void {
    this.profiles = SettingsUtil.getProfiles();
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
          const defaultSettings = SettingsUtil.getDefaultSettings();
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
        const defaultSettings = SettingsUtil.getDefaultSettings();
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
