import { Injectable, Inject } from '@angular/core';
import { LoginUseCasePort } from '@domain/ports/in/login.use-case.port';
import { AuthRepositoryPort, AUTH_REPOSITORY_PORT } from '@domain/ports/out/auth-repository.port';
import { User } from '@model//user.model';
import { AuthService } from '@application/services/auth.service';
import { ThemeService } from '@application/services/theme.service';
import { JWT_DECODER_PORT, JwtDecoderPort } from '@domain/ports/out/jwt-decoder.port';

@Injectable({
  providedIn: 'root'
})
export class LoginUserCaseAdapter implements LoginUseCasePort {
  constructor(
    @Inject(AUTH_REPOSITORY_PORT) private authRepository: AuthRepositoryPort,
    private authService: AuthService,
    private themeService: ThemeService,
    @Inject(JWT_DECODER_PORT) private jwtDecoder: JwtDecoderPort
  ) {}

  async execute(email: string, password: string): Promise<{ user: User; token: string }> {
    const result = await this.authRepository.authenticate(email, password);
    this.authService.setToken(result.token);
    const decodedToken = this.jwtDecoder.decodeToken(result.token);
    this.themeService.setThemeFromToken(decodedToken);
    return result;
  }
}
