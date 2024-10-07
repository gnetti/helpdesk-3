import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, map} from 'rxjs';
import {environment} from "@env/environment";
import {SettingsRepositoryPort} from "@domain/ports/out/settings-repository.port";
import {Theme} from '@core/domain/enums/theme.enum';
import {Profile} from '@core/domain/enums/profile.enum';
import {UserSettingsGet, UserSettingsPut, UserSettingsResponse} from "@model//user-settings.model";

@Injectable({
  providedIn: 'root'
})
export class SettingsRepositoryAdapter implements SettingsRepositoryPort {
  private baseUrl: string = `${environment.apiUrl}/persons/me`;

  constructor(private http: HttpClient) {
  }

  getCurrentUser(): Observable<UserSettingsGet> {
    return this.http.get<any>(this.baseUrl).pipe(
      map(data => this.mapToUserSettingsResponse(data))
    );
  }

  updateCurrentUser(settings: UserSettingsPut): Observable<UserSettingsResponse> {
    const updatedSettings = {
      ...settings,
      theme: settings.theme !== undefined ? settings.theme.toString() : undefined
    };

    return this.http.put<any>(this.baseUrl, updatedSettings).pipe(
      map(data => this.mapToUserSettingsResponse(data))
    );
  }

  private mapToUserSettingsResponse(data: any): UserSettingsResponse {
    return {
      id: data.id,
      name: data.name,
      email: data.email,
      profile: data.profile as Profile,
      theme: Number(data.theme) as Theme
    };
  }
}
