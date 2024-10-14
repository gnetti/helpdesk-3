import {InjectionToken} from "@angular/core";
import {Observable} from 'rxjs';
import {UserProfileGet, UserProfilePut, UserProfileResponse} from "@model//user-profile.model";

export interface ProfileRepositoryPort {

  getCurrentUser(): Observable<UserProfileGet>;

  updateCurrentUser(profile: UserProfilePut): Observable<UserProfileResponse>;
}

export const PROFILE_REPOSITORY_PORT = new InjectionToken<ProfileRepositoryPort>("ProfileRepositoryPort");
