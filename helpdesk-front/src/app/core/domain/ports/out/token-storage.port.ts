import { InjectionToken } from '@angular/core';

export interface TokenStoragePort {
    getToken(): string | null;
    setToken(token: string): void;
    removeToken(): void;
}

export const TOKEN_STORAGE_PORT = new InjectionToken<TokenStoragePort>('TokenStoragePort');