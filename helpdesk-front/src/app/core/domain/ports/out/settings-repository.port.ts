import {InjectionToken} from "@angular/core";
import {Observable} from 'rxjs';
import {UserSettingsGet, UserSettingsPut, UserSettingsResponse} from "@model//user-settings.model";

export interface SettingsRepositoryPort {

  getCurrentUser(): Observable<UserSettingsGet>;

  updateCurrentUser(settings: UserSettingsPut): Observable<UserSettingsResponse>;
}

export const SETTINGS_REPOSITORY_PORT = new InjectionToken<SettingsRepositoryPort>("SettingsRepositoryPort");
