import { Component, OnInit, Inject } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { LoginUtils } from "@utils//login-util";
import { LOGIN_USE_CASE_PORT, LoginUseCasePort } from "@domain/ports/in/login.use-case.port";
import { CryptoService } from "@security//crypto.service";
import { AuthService } from "@application/services/auth.service";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"]
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  returnUrl: string = "/";

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    @Inject(LOGIN_USE_CASE_PORT) private loginUseCase: LoginUseCasePort,
    private cryptoService: CryptoService,
    private loginUtils: LoginUtils,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.initForm();
    this.setReturnUrl();
  }

  async login(): Promise<void> {
    if (this.loginForm.valid) {
      try {
        await this.processLogin();
      } catch (error) {
        this.loginUtils.showLoginError();
      }
    } else {
      this.loginUtils.showFormError();
    }
  }

  validFields(): boolean {
    return this.loginForm.valid;
  }

  private initForm(): void {
    this.loginForm = this.formBuilder.group({
      email: ["admin@email.com", [Validators.required, Validators.email]],
      password: ["L@ndQLYN5yvx", [Validators.required, Validators.minLength(5)]]
    });
  }

  private setReturnUrl(): void {
    this.returnUrl = this.route.snapshot.queryParams["returnUrl"] || "/";
  }

  private async processLogin(): Promise<void> {
    const { email, password } = this.loginForm.value;
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
