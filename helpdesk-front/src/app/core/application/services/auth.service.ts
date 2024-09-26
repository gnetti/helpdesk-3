import { Injectable, Inject } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { TOKEN_STORAGE_PORT, TokenStoragePort } from "@core/domain/ports/out/token-storage.port";
import { JWT_DECODER_PORT, JwtDecoderPort } from "@core/domain/ports/out/jwt-decoder.port";
import { User } from "@core/domain/models/user.model";
import { THEME_USE_CASE_PORT, ThemeUseCasePort } from "@domain/ports/in/theme-use-case.port";
import { Theme } from "@enums//theme.enum";

@Injectable({
  providedIn: "root"
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;

  constructor(
    @Inject(TOKEN_STORAGE_PORT) private tokenStorage: TokenStoragePort,
    @Inject(JWT_DECODER_PORT) private jwtDecoder: JwtDecoderPort,
    @Inject(THEME_USE_CASE_PORT) private themeService: ThemeUseCasePort
  ) {
    this.currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromToken());
    this.currentUser = this.currentUserSubject.asObservable();
  }

  get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  setToken(token: string): void {
    this.tokenStorage.setToken(token);
    const user = this.getUserFromToken();
    this.currentUserSubject.next(user);
    if (user) {
      const decodedToken = this.jwtDecoder.decodeToken(token);
      this.themeService.setThemeFromToken(decodedToken);
    }
  }

  getToken(): string | null {
    return this.tokenStorage.getToken();
  }

  logout(): void {
    this.tokenStorage.removeToken();
    this.currentUserSubject.next(null);
    this.themeService.clearTheme();
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token && !this.jwtDecoder.isTokenExpired(token);
  }

  hasRole(role: string): boolean {
    return this.currentUserValue?.profiles.includes(role) ?? false;
  }

  private getUserFromToken(): User | null {
    const token = this.getToken();
    if (!token) return null;

    const decodedToken = this.jwtDecoder.decodeToken(token);
    if (!decodedToken) return null;

    return {
      id: decodedToken.id,
      email: decodedToken.sub,
      profiles: Array.isArray(decodedToken.profiles) ? decodedToken.profiles : [],
      theme: decodedToken.theme as Theme
    };
  }
}
