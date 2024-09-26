import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { inject } from "@angular/core";
import { AuthenticationPort, AUTHENTICATION_PORT } from "@domain/ports/in/authentication.port";

export const AuthGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): boolean | UrlTree => {
  const authPort = inject<AuthenticationPort>(AUTHENTICATION_PORT);
  const router = inject(Router);

  if (!authPort.isAuthenticated()) {
    return router.createUrlTree(["/login"], { queryParams: { returnUrl: state.url } });
  }

  const requiredRole = route.data["requiredRole"] as string;
  if (requiredRole && !authPort.hasRole(requiredRole)) {
    return router.createUrlTree(["/unauthorized"]);
  }

  return true;
};
