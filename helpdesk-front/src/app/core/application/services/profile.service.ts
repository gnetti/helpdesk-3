import { Injectable, Inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PROFILE_REPOSITORY_PORT, ProfileRepositoryPort } from "@domain/ports/out/profile-repository.port";
import { ProfileUseCasePort } from "@domain/ports/in/profile-use-case.port";
import {UserProfileGet, UserProfilePut, UserProfileResponse} from "@model//user-profile.model";

@Injectable({
  providedIn: 'root'
})
export class ProfileUseCase implements ProfileUseCasePort {
  constructor(
    @Inject(PROFILE_REPOSITORY_PORT) private profileRepository: ProfileRepositoryPort
  ) {}

  getCurrentUser(): Observable<UserProfileGet> {
    return this.profileRepository.getCurrentUser();
  }

  updateCurrentUser(profile: UserProfilePut): Observable<UserProfileResponse> {
    return this.profileRepository.updateCurrentUser(profile);
  }
}
