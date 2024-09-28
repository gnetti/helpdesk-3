import { Inject, Injectable } from "@angular/core";
import { ThemeUseCasePort } from "@domain/ports/in/theme-use-case.port";
import { THEME_SERVICE_PORT, ThemeStoragePort } from "@domain/ports/out/theme.storage.port";
import { ThemeConfig } from "@infrastructure/config/theme.config";
import { ThemeUtils } from "@shared/utils/theme.utils";
import {Theme} from "@enums//theme.enum";

@Injectable({
  providedIn: "root"
})
export class ThemeService implements ThemeUseCasePort {
  private themeLinkElement: HTMLLinkElement;

  constructor(
    @Inject(THEME_SERVICE_PORT) private themeStorage: ThemeStoragePort
  ) {
    this.themeLinkElement = ThemeUtils.createOrGetThemeLinkElement();
    this.loadSavedTheme();
  }

  setTheme(theme: Theme): void {
    const themeFileName = ThemeConfig.getThemeFileName(theme);
    if (themeFileName) {
      this.themeLinkElement.href = `/themes/${themeFileName}`;
      this.themeStorage.setTheme(theme);
    } else {
      console.warn(`Theme file not found for theme: ${theme}`);
    }
  }

  getCurrentTheme(): Theme {
    return this.themeStorage.getTheme() as Theme || this.getDefaultTheme();
  }

  setThemeFromToken(decodedToken: any): void {
    if (decodedToken?.theme) {
      const themeEnum = ThemeUtils.getThemeEnum(decodedToken.theme);
      if (themeEnum) {
        this.setTheme(themeEnum);
      } else {
        console.warn(`Invalid theme in token: ${decodedToken.theme}`);
        this.setTheme(this.getDefaultTheme());
      }
    } else {
      this.setTheme(this.getDefaultTheme());
    }
  }

  resetTheme(): void {
    this.setTheme(this.getDefaultTheme());
  }

  getDefaultTheme(): Theme {
    return ThemeUtils.DEFAULT_THEME;
  }

  private loadSavedTheme(): void {
    const savedTheme = this.themeStorage.getTheme() as Theme;
    if (savedTheme && Object.values(Theme).includes(savedTheme)) {
      this.setTheme(savedTheme);
    } else {
      this.setTheme(this.getDefaultTheme());
    }
  }
}
