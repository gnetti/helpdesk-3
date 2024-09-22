import {Injectable, Inject} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {LoginUseCasePort, LOGIN_USE_CASE_PORT} from '@domain/ports/in/login.use-case.port';
import {AuthenticationPort} from '@domain/ports/in/authentication.port';
import {JwtHelperService} from '@auth0/angular-jwt';
import {TOKEN_STORAGE_PORT, TokenStoragePort} from '@core/domain/ports/out/token-storage.port';
import {User} from "@model//user.model";


@Injectable({
  providedIn: 'root'
})
export class AuthService implements AuthenticationPort {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;

  constructor(
    @Inject(LOGIN_USE_CASE_PORT) private loginUseCase: LoginUseCasePort,
    @Inject(TOKEN_STORAGE_PORT) private tokenStorage: TokenStoragePort,
    private jwtHelper: JwtHelperService
  ) {
    this.currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromToken());
    this.currentUser = this.currentUserSubject.asObservable();
  }

  get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  async login(email: string, password: string): Promise<User> {
    const result = await this.loginUseCase.execute(email, password);
    if (!result?.token) {
      throw new Error('Resposta de login inválida');
    }

    this.tokenStorage.setToken(result.token);
    const user = this.getUserFromToken();

    if (!user) {
      throw new Error('Falha ao decodificar token de usuário');
    }

    this.currentUserSubject.next(user);
    return user;
  }

  logout(): void {
    this.tokenStorage.removeToken();
    localStorage.clear();
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    const token = this.tokenStorage.getToken();
    return Boolean(token && !this.jwtHelper.isTokenExpired(token));
  }

  getToken(): string | null {
    return this.tokenStorage.getToken();
  }

  hasRole(role: string): boolean {
    return this.currentUserValue?.profiles.includes(role) ?? false;
  }

  private getUserFromToken(): User | null {
    const token = this.tokenStorage.getToken();
    return token ? this.decodeToken(token) : null;
  }

  private decodeToken(token: string): User | null {
    try {
      const {id, sub: email, profiles} = this.jwtHelper.decodeToken(token);
      return {id, email, profiles};
    } catch {
      return null;
    }
  }
}
