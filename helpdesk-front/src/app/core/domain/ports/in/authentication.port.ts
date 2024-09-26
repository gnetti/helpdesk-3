import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from "@model//user.model";

export interface AuthenticationPort {
  login(): Promise<void>;
  completeAuthentication(): Promise<void>;
  logout(): Promise<void>;
  isAuthenticated(): boolean;
  hasRole(role: string): boolean;
  getToken(): string | null;
  getUser(): Observable<User | null>;
}

export const AUTHENTICATION_PORT = new InjectionToken<AuthenticationPort>('AuthenticationPort');
