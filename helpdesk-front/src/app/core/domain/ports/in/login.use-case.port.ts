import {InjectionToken} from '@angular/core';
import {User} from '@model//user.model';

export interface LoginUseCasePort {
  execute(email: string, password: string): Promise<{ user: User; token: string }>;
}

export const LOGIN_USE_CASE_PORT = new InjectionToken<LoginUseCasePort>('LoginUseCasePort');

