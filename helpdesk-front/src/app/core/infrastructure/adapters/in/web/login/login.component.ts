import {Component, OnInit, Inject} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {LOGIN_USE_CASE_PORT, LoginUseCasePort} from "@domain/ports/in/login.use-case.port";
import {AuthService} from "@application/services/auth.service";
import {THEME_USE_CASE_PORT, ThemeUseCasePort} from "@domain/ports/in/theme-use-case.port";
import {CryptoService} from "@security//crypto.service";
import {LoginUtils} from "@utils//login.util";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"]
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  returnUrl: string = "/";
  isLoading: boolean = false;
  formSubmitted: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    @Inject(LOGIN_USE_CASE_PORT) private loginUseCase: LoginUseCasePort,
    private cryptoService: CryptoService,
    private loginUtils: LoginUtils,
    private authService: AuthService,
    @Inject(THEME_USE_CASE_PORT) private themeService: ThemeUseCasePort
  ) {
  }

  ngOnInit(): void {
    this.initForm();
    this.setReturnUrl();
    this.resetThemeForLoginPage();
  }

  async login(): Promise<void> {
    this.formSubmitted = true;
    if (this.loginForm.valid) {
      this.isLoading = true;
      try {
        await this.processLogin();
      } catch (error) {
        this.loginUtils.showLoginError();
      } finally {
        this.isLoading = false;
      }
    } else {
      this.loginUtils.showFormError();
    }
  }

  private initForm(): void {
    this.loginForm = this.formBuilder.group({
      email: ["admin@email.com", [Validators.required, Validators.email]],
      password: ["L@ndQLYN5yvx", [Validators.required, Validators.minLength(10)]]
    });
  }

  validFields(): boolean {
    return this.loginForm.valid;
  }

  private setReturnUrl(): void {
    this.returnUrl = this.route.snapshot.queryParams["returnUrl"] || "/";
  }

  private resetThemeForLoginPage(): void {
    if (this.authService.isAuthenticated()) {
      this.themeService.resetTheme();
    }
    const defaultTheme = this.themeService.getDefaultTheme();
    this.themeService.setTheme(defaultTheme);
  }

  private async processLogin(): Promise<void> {
    const {email, password} = this.loginForm.value;
    const encryptedPassword = this.cryptoService.encrypt(password);
    try {
      const result = await this.loginUseCase.execute(email, encryptedPassword);
      if (result?.token) {
        this.handleSuccessfulLogin(result.token);
      } else {
        this.loginUtils.showLoginError();
      }
    } catch (error) {
      this.loginUtils.showLoginError();
    }
  }

  private handleSuccessfulLogin(token: string): void {
    this.authService.setToken(token);
    this.loginUtils.showLoginSuccess();
    this.loginUtils.navigateTo(this.returnUrl);
  }
}
