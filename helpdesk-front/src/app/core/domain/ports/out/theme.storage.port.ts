import { InjectionToken } from "@angular/core";

export interface ThemeStoragePort {
  setTheme(theme: string): void;

  getTheme(): string | null;
}

export const THEME_SERVICE_PORT = new InjectionToken<ThemeStoragePort>("ThemeStoragePort");
