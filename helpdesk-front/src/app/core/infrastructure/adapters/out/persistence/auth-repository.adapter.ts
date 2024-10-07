import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { AuthRepositoryPort } from "@domain/ports/out/auth-repository.port";
import { User } from "@domain/models/user.model";
import { firstValueFrom } from "rxjs";
import { environment } from "@env/environment";
import { JwtHelperService } from "@auth0/angular-jwt";
import { ThemeUtil } from "@utils//theme.util";
import {Profile} from "@enums//profile.enum";

@Injectable()
export class AuthRepositoryAdapter implements AuthRepositoryPort {
  private apiUrl = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private jwtHelper: JwtHelperService
  ) {}

  async authenticate(email: string, password: string): Promise<{ user: User; token: string }> {
    const response = await firstValueFrom(
      this.http.post<any>(`${this.apiUrl}/auth/login`, { email, password }, {
        observe: "response",
        headers: new HttpHeaders({ "Content-Type": "application/json" }),
        withCredentials: true
      })
    );

    const token = response.headers.get("Authorization") || response.body?.token || response.body?.access_token;
    if (!token) throw new Error("Token ausente na resposta");
    const cleanToken = token.replace("Bearer ", "");
    const user = this.extractUserFromToken(cleanToken);
    return { user, token: cleanToken };
  }

  async getCurrentUser(): Promise<User> {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Usuário não autenticado");
    return this.extractUserFromToken(token);
  }

  private extractUserFromToken(token: string): User {
    const decodedToken = this.jwtHelper.decodeToken(token);
    return {
      id: decodedToken.id,
      email: decodedToken.sub,
      profile: this.getProfileFromToken(decodedToken.profile),
      theme: ThemeUtil.getThemeEnum(decodedToken.theme)
    };
  }

  private getProfileFromToken(profileValue: string): Profile {
    return Profile[profileValue as keyof typeof Profile] || Profile.USER;
  }
}
