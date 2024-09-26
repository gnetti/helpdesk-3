// src/app/core/infrastructure/adapters/in/web/callback.component.ts

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationPort } from '@core/domain/ports/in/authentication.port';
import { Inject } from '@angular/core';
import { AUTHENTICATION_PORT } from '@core/domain/ports/in/authentication.port';

@Component({
  template: '<p>Processando login...</p>'
})
export class CallbackComponent implements OnInit {
  constructor(
    @Inject(AUTHENTICATION_PORT) private authService: AuthenticationPort,
    private router: Router
  ) {}

  ngOnInit() {
    this.authService.completeAuthentication()
      .then(() => {

        this.router.navigate(['/']);
      })
      .catch(error => {
           throw new Error(`Falha ao completar a autenticação: ${error.message}`);
      });
  }
}
