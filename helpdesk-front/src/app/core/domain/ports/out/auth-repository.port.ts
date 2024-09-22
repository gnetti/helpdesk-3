import {InjectionToken} from '@angular/core';
import {User} from '@model//user.model';

export interface AuthRepositoryPort {
    authenticate(email: string, password: string): Promise<{ user: User; token: string }>;

    getCurrentUser(): Promise<User>;
}

export const AUTH_REPOSITORY_PORT = new InjectionToken<AuthRepositoryPort>('AuthRepositoryPort');