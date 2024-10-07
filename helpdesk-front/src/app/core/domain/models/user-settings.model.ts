import {Theme} from "@core/domain/enums/theme.enum";
import {Profile} from "@core/domain/enums/profile.enum";

export interface UserSettings {
  id: number;
  name: string;
  email: string;
  profile: Profile;
  theme: Theme;
  currentPassword?: string;
  newPassword?: string;
  confirmNewPassword?: string;
}

export type UserSettingsResponse = Pick<UserSettings, 'id' | 'name' | 'email' | 'profile' | 'theme'>;

export type UserSettingsGet = UserSettingsResponse;

export type UserSettingsPut = {
  theme?: Theme;
  currentPassword?: string;
  newPassword?: string;
};
