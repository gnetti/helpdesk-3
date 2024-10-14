import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, map} from 'rxjs';
import {environment} from "@env/environment";
import {ProfileRepositoryPort} from "@domain/ports/out/profile-repository.port";
import {Theme} from '@core/domain/enums/theme.enum';
import {Profile} from '@core/domain/enums/profile.enum';
import {UserProfileGet, UserProfilePut, UserProfileResponse} from "@model//user-profile.model";

@Injectable({
  providedIn: 'root'
})
export class ProfileRepositoryAdapter implements ProfileRepositoryPort {
  private baseUrl: string = `${environment.apiUrl}/persons/me`;

  constructor(private http: HttpClient) {
  }

  getCurrentUser(): Observable<UserProfileGet> {
    return this.http.get<any>(this.baseUrl).pipe(
      map(data => this.mapToUserProfileResponse(data))
    );
  }

  updateCurrentUser(profile: UserProfilePut): Observable<UserProfileResponse> {
    const updatedProfile = {
      ...profile,
      theme: profile.theme !== undefined ? profile.theme.toString() : undefined
    };

    return this.http.put<any>(this.baseUrl, updatedProfile).pipe(
      map(data => this.mapToUserProfileResponse(data))
    );
  }

  private mapToUserProfileResponse(data: any): UserProfileResponse {
    return {
      id: data.id,
      name: data.name,
      email: data.email,
      profile: data.profile as Profile,
      theme: Number(data.theme) as Theme
    };
  }
}
