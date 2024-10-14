import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { TokenTimeRepositoryPort } from "@domain/ports/out/token-time-repository.port";
import { environment } from "@env/environment";
import { TokenTimeProfile } from "@model//token-time-profile.model";


@Injectable({
  providedIn: "root"
})
export class TokenTimeRepositoryAdapter implements TokenTimeRepositoryPort {
  private apiUrl = `${environment.apiUrl}/token-time`;

  constructor(private http: HttpClient) {
  }

  findAll(): Observable<TokenTimeProfile[]> {
    return this.http.get<TokenTimeProfile[]>(`${this.apiUrl}/all`);
  }

  create(tokenTimeProfile: TokenTimeProfile): Observable<TokenTimeProfile> {
    return this.http.post<TokenTimeProfile>(this.apiUrl, tokenTimeProfile);
  }

  findByProfile(profileCode: number): Observable<TokenTimeProfile | null> {
    return this.http.get<TokenTimeProfile | null>(`${this.apiUrl}/profile?profileCode=${profileCode}`);
  }

  findByProfileForLogin(profileCode: number): Observable<TokenTimeProfile | null> {
    return this.http.get<TokenTimeProfile | null>(`${this.apiUrl}/login/profile?profileCode=${profileCode}`);
  }

  update(profileCode: number, tokenTimeProfile: TokenTimeProfile): Observable<TokenTimeProfile> {
    return this.http.put<TokenTimeProfile>(`${this.apiUrl}/update?profileCode=${profileCode}`, tokenTimeProfile);
  }

  getExpirationTimeInMillis(profileCode: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/expiration?profileCode=${profileCode}`);
  }

  existsByProfile(profileCode: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/exists?profileCode=${profileCode}`);
  }
}
