import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SettingsRepositoryPort } from '@core/domain/ports/out/settings-repository.port';
import {environment} from "@env/environment";
import {UserSettings} from "@model//user-settings.model";


@Injectable()
export class SettingsHttpRepository implements SettingsRepositoryPort {
  private baseUrl: string = `${environment.apiUrl}/pessoas/me`;

  constructor(private http: HttpClient) {}

  getUserSettings(): Observable<UserSettings> {
    return this.http.get<UserSettings>(this.baseUrl);
  }

  updateUserSettings(settings: UserSettings): Observable<UserSettings> {
    return this.http.put<UserSettings>(this.baseUrl, settings);
  }
}
