import { Observable } from 'rxjs';
import {UserSettings} from "@model//user-settings.model";

export const SETTINGS_REPOSITORY_PORT = 'SettingsRepositoryPort';

export interface SettingsRepositoryPort {
  getUserSettings(): Observable<UserSettings>;
  updateUserSettings(settings: UserSettings): Observable<UserSettings>;
}
