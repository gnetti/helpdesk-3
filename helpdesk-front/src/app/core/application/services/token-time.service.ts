import { Inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { TOKEN_TIME_REPOSITORY_PORT, TokenTimeRepositoryPort } from "@domain/ports/out/token-time-repository.port";
import { TokenTimeProfile } from "@model//token-time-profile.model";
import { TokenTimeUseCasePort } from "@domain/ports/in/token-time-use-case.port";


@Injectable({
  providedIn: "root"
})
export class TokenTimeService implements TokenTimeUseCasePort {
  constructor(
    @Inject(TOKEN_TIME_REPOSITORY_PORT) private tokenTimePersistence: TokenTimeRepositoryPort
  ) {
  }

  findAll(): Observable<TokenTimeProfile[]> {
    return this.tokenTimePersistence.findAll();
  }

  create(tokenTimeProfile: TokenTimeProfile): Observable<TokenTimeProfile> {
    return this.tokenTimePersistence.create(tokenTimeProfile);
  }

  findByProfile(profileCode: number): Observable<TokenTimeProfile | null> {
    return this.tokenTimePersistence.findByProfile(profileCode);
  }

  findByProfileForLogin(profileCode: number): Observable<TokenTimeProfile | null> {
    return this.tokenTimePersistence.findByProfileForLogin(profileCode);
  }

  update(profileCode: number, tokenTimeProfile: TokenTimeProfile): Observable<TokenTimeProfile> {
    return this.tokenTimePersistence.update(profileCode, tokenTimeProfile);
  }

  getExpirationTimeInMillis(profileCode: number): Observable<number> {
    return this.tokenTimePersistence.getExpirationTimeInMillis(profileCode);
  }

  existsByProfile(profileCode: number): Observable<boolean> {
    return this.tokenTimePersistence.existsByProfile(profileCode);
  }
}
