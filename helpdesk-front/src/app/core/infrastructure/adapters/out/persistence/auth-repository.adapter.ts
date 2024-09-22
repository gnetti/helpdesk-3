import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthRepositoryPort} from '@domain/ports/out/auth-repository.port';
import {User} from '@domain/models/user.model';
import {firstValueFrom} from 'rxjs';
import {environment} from "@env/environment";

@Injectable()
export class AuthRepositoryAdapter implements AuthRepositoryPort {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  async authenticate(email: string, password: string): Promise<{ user: User; token: string }> {
    const response = await firstValueFrom(
      this.http.post<{ user: User; token: string }>(`${this.apiUrl}/auth/login`, {email, password})
    );
    if (!response || !response.token) {
      throw new Error('Resposta inválida do servidor: token ausente');
    }
    return response;
  }

  async getCurrentUser(): Promise<User> {
    return firstValueFrom(
      this.http.get<User>(`${this.apiUrl}/auth/me`)
    );
  }
}
