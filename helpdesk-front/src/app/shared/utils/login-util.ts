import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class LoginUtils {
  constructor(private toast: ToastrService, private router: Router) {}

  showLoginSuccess(): void {
    this.toast.success('Login realizado com sucesso');
  }

  showLogoutSuccess(): void {
    this.toast.success('Logout realizado com sucesso');
  }

  showProcessingError(): void {
    this.toast.error('Erro ao processar a operação. Por favor, tente novamente.');
  }

  showLoginError(): void {
    this.toast.error('Usuário e/ou senha inválidos');
  }

  showFormError(): void {
    this.toast.error('Por favor, preencha todos os campos corretamente');
  }

  navigateTo(url: string): void {
    this.router.navigate([url]);
  }
}
