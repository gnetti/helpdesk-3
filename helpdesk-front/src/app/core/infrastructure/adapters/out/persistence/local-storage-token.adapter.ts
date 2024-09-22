import {Injectable} from '@angular/core';
import {TokenStoragePort} from '@domain/ports/out/token-storage.port';

@Injectable({
    providedIn: 'root'
})
export class LocalStorageTokenAdapter implements TokenStoragePort {
    private readonly TOKEN_KEY = 'token';

    getToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    setToken(token: string): void {
        localStorage.setItem(this.TOKEN_KEY, token);
    }

    removeToken(): void {
        localStorage.removeItem(this.TOKEN_KEY);
    }
}