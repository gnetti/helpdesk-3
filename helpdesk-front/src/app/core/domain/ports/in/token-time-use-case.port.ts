import { InjectionToken } from "@angular/core";
import { Observable } from "rxjs";
import { TokenTimeProfile } from "@model//token-time-profile.model";


export interface TokenTimeUseCasePort {

  findAll(): Observable<TokenTimeProfile[]>;

  create(tokenTimeProfile: TokenTimeProfile): Observable<TokenTimeProfile>;

  findByProfile(profileCode: number): Observable<TokenTimeProfile | null>;

  findByProfileForLogin(profileCode: number): Observable<TokenTimeProfile | null>;

  update(profileCode: number, tokenTimeProfile: TokenTimeProfile): Observable<TokenTimeProfile>;

  getExpirationTimeInMillis(profileCode: number): Observable<number>;

  existsByProfile(profileCode: number): Observable<boolean>;
}

export const TOKEN_TIME_USE_CASE_PORT = new InjectionToken<TokenTimeUseCasePort>("TokenTimeUseCasePort");
