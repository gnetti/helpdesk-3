import { Injectable } from "@angular/core";
import { JwtHelperService } from "@auth0/angular-jwt";
import { JwtDecoderPort } from "@core/domain/ports/out/jwt-decoder.port";

@Injectable({
  providedIn: "root"
})
export class JwtDecoderAdapter implements JwtDecoderPort {
  constructor(private jwtHelper: JwtHelperService) {
  }

  decodeToken(token: string): any {
    try {
      return this.jwtHelper.decodeToken(token);
    } catch (error) {
      const errorMessage = (error as Error).message || "Unknown error";
      throw new Error(`Error decoding JWT token: ${errorMessage}`);
    }
  }

  isTokenExpired(token: string): boolean {
    try {
      return this.jwtHelper.isTokenExpired(token);
    } catch (error) {
      const errorMessage = (error as Error).message || "Unknown error";
      throw new Error(`Error checking if JWT token is expired: ${errorMessage}`);
    }
  }
}
