import {Theme} from "@enums//theme.enum";

export interface UserSettings {
  id: number;
  name: string;
  email: string;
  profiles: number[];
  theme: Theme;
}
