import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthenticationPort, AUTHENTICATION_PORT } from '@domain/ports/in/authentication.port';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authPort = inject<AuthenticationPort>(AUTHENTICATION_PORT);
    const token = authPort.getToken();

    if (token) {
        req = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    return next(req);
};