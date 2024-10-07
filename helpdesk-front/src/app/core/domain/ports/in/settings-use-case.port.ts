import {InjectionToken} from "@angular/core";
import {Observable} from 'rxjs';
import {UserSettingsGet, UserSettingsPut, UserSettingsResponse} from "@model//user-settings.model";

export interface SettingsUseCasePort {

  getCurrentUser(): Observable<UserSettingsGet>;

  updateCurrentUser(settings: UserSettingsPut): Observable<UserSettingsResponse>;
}

export const SETTINGS_USE_CASE_PORT = new InjectionToken<SettingsUseCasePort>("SettingsUseCasePort");
