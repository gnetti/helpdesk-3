import { Injectable, Inject } from '@angular/core';
import { LoginUseCasePort } from '@domain/ports/in/login.use-case.port';
import { AuthRepositoryPort, AUTH_REPOSITORY_PORT } from '@domain/ports/out/auth-repository.port';
import { User } from '@model//user.model';

@Injectable()
export class LoginUseCase implements LoginUseCasePort {
    constructor(@Inject(AUTH_REPOSITORY_PORT) private authRepository: AuthRepositoryPort) {}

    async execute(email: string, password: string): Promise<{ user: User; token: string }> {
        return this.authRepository.authenticate(email, password);
    }
}