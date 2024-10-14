import {Theme} from "@core/domain/enums/theme.enum";
import {Profile} from "@core/domain/enums/profile.enum";

export interface UserProfile {
  id: number;
  name: string;
  email: string;
  profile: Profile;
  theme: Theme;
  currentPassword?: string;
  newPassword?: string;
  confirmNewPassword?: string;
}

export type UserProfileResponse = Pick<UserProfile, 'id' | 'name' | 'email' | 'profile' | 'theme'>;

export type UserProfileGet = UserProfileResponse;

export type UserProfilePut = {
  theme?: Theme;
  currentPassword?: string;
  newPassword?: string;
};
