import { Injectable, Inject } from '@angular/core';
import { Observable } from 'rxjs';
import { SETTINGS_REPOSITORY_PORT, SettingsRepositoryPort } from "@domain/ports/out/settings-repository.port";
import { SettingsUseCasePort } from "@domain/ports/in/settings-use-case.port";
import {UserSettingsGet, UserSettingsPut, UserSettingsResponse} from "@model//user-settings.model";

@Injectable({
  providedIn: 'root'
})
export class SettingsUseCase implements SettingsUseCasePort {
  constructor(
    @Inject(SETTINGS_REPOSITORY_PORT) private settingsRepository: SettingsRepositoryPort
  ) {}

  getCurrentUser(): Observable<UserSettingsGet> {
    return this.settingsRepository.getCurrentUser();
  }

  updateCurrentUser(settings: UserSettingsPut): Observable<UserSettingsResponse> {
    return this.settingsRepository.updateCurrentUser(settings);
  }
}
