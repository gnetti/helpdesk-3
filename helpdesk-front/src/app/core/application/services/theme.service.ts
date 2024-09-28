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

  setTheme(theme: Theme | string): void {
    const themeEnum = ThemeUtils.getThemeEnum(theme);
    const themeFileName = ThemeConfig.getThemeFileName(themeEnum);
    if (themeFileName) {
      this.themeLinkElement.href = `/themes/${themeFileName}`;
      this.themeStorage.setTheme(themeEnum);
    } else {
      this.setTheme(ThemeUtils.DEFAULT_THEME);
    }
  }

  getCurrentTheme(): Theme {
    return this.themeStorage.getTheme() as Theme || ThemeUtils.DEFAULT_THEME;
  }

  clearTheme = this.resetTheme;

  setThemeFromToken(decodedToken: any): void {
    this.setTheme(decodedToken?.theme || ThemeUtils.DEFAULT_THEME);
  }

  resetTheme(): void {
    this.setTheme(ThemeUtils.DEFAULT_THEME);
  }

  private loadSavedTheme(): void {
    this.setTheme(this.getCurrentTheme());
  }
}
