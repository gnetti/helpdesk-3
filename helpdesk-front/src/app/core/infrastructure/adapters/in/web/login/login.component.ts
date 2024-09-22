import {Component, OnInit, Inject} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router, ActivatedRoute} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {LOGIN_USE_CASE_PORT, LoginUseCasePort} from "@domain/ports/in/login.use-case.port";
import {AuthService} from "@application/services/auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  returnUrl: string = '/';

  constructor(
    private formBuilder: FormBuilder,
    private toast: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
    @Inject(LOGIN_USE_CASE_PORT) private loginUseCase: LoginUseCasePort,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.initForm();
    this.setReturnUrl();
  }

  async login() {
    this.loginForm.valid ? await this.processLogin() : this.showFormError();
  }

  validFields(): boolean {
    return this.loginForm.valid;
  }

  private initForm(): void {
    this.loginForm = this.formBuilder.group({
      email: ['admin@mail.com', [Validators.required, Validators.email]],
      password: ['L@ndQLYN5yvx', [Validators.required, Validators.minLength(5)]]
    });
  }

  private setReturnUrl(): void {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  private async processLogin(): Promise<void> {
    const {email, password} = this.loginForm.value;
    try {
      const result = await this.loginUseCase.execute(email, password);
      this.handleLoginResult(result);
    } catch (error) {
      this.showLoginError();
    }
  }

  private handleLoginResult(result: any): void {
    result?.token && this.storeToken(result.token);
    this.authService.getToken() ? this.navigateAfterLogin() : this.showProcessingError();
  }

  private storeToken(token: string): void {
    localStorage.setItem('token', token);
  }

  private async navigateAfterLogin(): Promise<void> {
    await this.router.navigateByUrl(this.returnUrl);
    this.showLoginSuccess();
  }

  private showLoginSuccess(): void {
    this.toast.success('Login realizado com sucesso');
  }

  private showProcessingError(): void {
    this.toast.error('Erro ao processar o login. Por favor, tente novamente.');
  }

  private showLoginError(): void {
    this.toast.error('Usuário e/ou senha inválidos');
  }

  private showFormError(): void {
    this.toast.error('Por favor, preencha todos os campos corretamente');
  }
}
