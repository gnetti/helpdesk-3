import { InjectionToken } from '@angular/core';

export interface AuthenticationPort {
    isAuthenticated(): boolean;
    hasRole(role: string): boolean;
    getToken(): string | null;
}

export const AUTHENTICATION_PORT = new InjectionToken<AuthenticationPort>('AuthenticationPort');