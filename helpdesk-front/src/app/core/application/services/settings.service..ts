import {Injectable, Inject} from '@angular/core';
import {Observable} from 'rxjs';
import {SettingsUseCasePort} from "@domain/ports/in/settings-use-case.port";
import {SETTINGS_REPOSITORY_PORT, SettingsRepositoryPort} from "@domain/ports/out/settings-repository.port";
import {UserSettings} from "@model//user-settings.model";


@Injectable()
export class SettingsUseCase implements SettingsUseCasePort {
  constructor(
    @Inject(SETTINGS_REPOSITORY_PORT) private settingsRepository: SettingsRepositoryPort
  ) {
  }

  getUserSettings(): Observable<UserSettings> {
    return this.settingsRepository.getUserSettings();
  }

  updateUserSettings(settings: UserSettings): Observable<UserSettings> {
    return this.settingsRepository.updateUserSettings(settings);
  }
}
