import {NgModule, Optional, SkipSelf} from '@angular/core';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {JwtModule} from '@auth0/angular-jwt';
import {LoginUseCase} from '@application/services/login.use-case';
import {AuthRepositoryAdapter} from '@adapters/out/persistence/auth-repository.adapter';
import {LOGIN_USE_CASE_PORT} from '@domain/ports/in/login.use-case.port';
import {AUTH_REPOSITORY_PORT} from '@domain/ports/out/auth-repository.port';
import {authInterceptor} from "@adapters/in/web/interceptors/auth.interceptor";
import {TOKEN_STORAGE_PORT} from "@domain/ports/out/token-storage.port";
import {LocalStorageTokenAdapter} from "@adapters/out/persistence/local-storage-token.adapter";
import {AUTHENTICATION_PORT} from "@domain/ports/in/authentication.port";
import {environment} from "@env/environment";
import {AuthService} from "@application/services/auth.service";

export function tokenGetter() {
  return localStorage.getItem('token');
}

@NgModule({
  imports: [
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
        allowedDomains: [new URL(environment.apiUrl).hostname],
        disallowedRoutes: [`${environment.apiUrl}/auth`]
      }
    })
  ],
  providers: [
    provideHttpClient(
      withInterceptors([authInterceptor])
    ),
    {provide: LOGIN_USE_CASE_PORT, useClass: LoginUseCase},
    {provide: AUTH_REPOSITORY_PORT, useClass: AuthRepositoryAdapter},
    {provide: AUTHENTICATION_PORT, useClass: AuthService},
    {provide: TOKEN_STORAGE_PORT, useClass: LocalStorageTokenAdapter}
  ]
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error('CoreModule is already loaded. Import it in the AppModule only.');
    }
  }
}
