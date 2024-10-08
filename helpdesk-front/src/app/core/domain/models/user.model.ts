import {Profile} from "@enums//profile.enum";
import {Theme} from "@enums//theme.enum";

export interface User {
  id: number;
  email: string;
  profile: Profile;
  theme: Theme;

}
