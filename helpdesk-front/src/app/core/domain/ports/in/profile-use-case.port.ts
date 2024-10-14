import {InjectionToken} from "@angular/core";
import {Observable} from 'rxjs';
import {UserProfileGet, UserProfilePut, UserProfileResponse} from "@model//user-profile.model";

export interface ProfileUseCasePort {

  getCurrentUser(): Observable<UserProfileGet>;

  updateCurrentUser(profile: UserProfilePut): Observable<UserProfileResponse>;
}

export const PROFILE_USE_CASE_PORT = new InjectionToken<ProfileUseCasePort>("ProfileUseCasePort");
