import { Observable } from 'rxjs';
import {UserSettings} from "@model//user-settings.model";

export const SETTINGS_USE_CASE_PORT = 'SettingsUseCasePort';

export interface SettingsUseCasePort {
  getUserSettings(): Observable<UserSettings>;
  updateUserSettings(settings: UserSettings): Observable<UserSettings>;
}
