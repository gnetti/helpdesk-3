import { InjectionToken } from "@angular/core";
import { Theme } from "@enums//theme.enum";

export interface ThemeUseCasePort {
  setTheme(theme: Theme): void;
  getCurrentTheme(): Theme;
  clearTheme(): void;
  setThemeFromToken(decodedToken: any): void;
  resetTheme(): void;
}

export const THEME_USE_CASE_PORT = new InjectionToken<ThemeUseCasePort>("ThemeUseCasePort");
