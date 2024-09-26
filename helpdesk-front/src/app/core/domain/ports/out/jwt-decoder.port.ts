import { InjectionToken } from '@angular/core';

export interface JwtDecoderPort {
  decodeToken(token: string): any;
  isTokenExpired(token: string): boolean;
}

export const JWT_DECODER_PORT = new InjectionToken<JwtDecoderPort>('JwtDecoderPort');
