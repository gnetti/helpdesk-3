import { Component, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from "@application/services/auth.service";

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})
export class NavComponent implements AfterViewInit {

  constructor(
    private router: Router,
    private authService: AuthService,
    private toast: ToastrService
  ) {}

  async ngAfterViewInit(): Promise<void> {
    try {
      await this.router.navigate(['dashboard']);
    } catch (error) {
      this.toast.error('Não foi possível carregar o dashboard', 'Erro de Navegação');
    }
  }

  async logout(): Promise<void> {
    try {
      await this.router.navigate(['login']);
      this.authService.logout();
      this.toast.info('Logout realizado com sucesso', 'Logout');
    } catch (error) {
      this.toast.error('Ocorreu um erro durante o logout', 'Erro');
    }
  }
}
