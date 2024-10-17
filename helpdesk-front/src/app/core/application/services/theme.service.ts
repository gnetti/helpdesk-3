import { Inject, Injectable } from "@angular/core";
import { ThemeUseCasePort } from "@domain/ports/in/theme-use-case.port";
import { THEME_SERVICE_PORT, ThemeStoragePort } from "@domain/ports/out/theme-storage.port";
import { ThemeConfig } from "@infrastructure/config/theme.config";
import { ThemeUtil } from "@utils//theme.util";
import { Theme } from "@enums//theme.enum";

@Injectable({
  providedIn: "root"
})
export class ThemeService implements ThemeUseCasePort {
  private themeLinkElement: HTMLLinkElement;

  constructor(
    @Inject(THEME_SERVICE_PORT) private themeStorage: ThemeStoragePort
  ) {
    this.themeLinkElement = ThemeUtil.createOrGetThemeLinkElement();
    this.loadSavedTheme();
  }

  setTheme(theme: Theme): void {
    const themeFileName = ThemeConfig.getThemeFileName(theme);
    if (themeFileName) {
      this.themeLinkElement.href = `/themes/${themeFileName}`;
      this.themeStorage.setTheme(theme.toString());
    } else {
      throw new Error(`Theme file not found for theme: ${ThemeUtil.getThemeName(theme)}`);
    }
  }

  getCurrentTheme(): Theme {
    const savedTheme = this.themeStorage.getTheme();
    return savedTheme !== null ? ThemeUtil.getThemeEnum(Number(savedTheme)) : this.getDefaultTheme();
  }

  setThemeFromToken(decodedToken: any): void {
    if (decodedToken?.theme !== undefined) {
      const themeEnum = ThemeUtil.getThemeEnum(decodedToken.theme);
      this.setTheme(themeEnum);
    } else {
      this.setTheme(this.getDefaultTheme());
    }
  }

  resetTheme(): void {
    this.setTheme(this.getDefaultTheme());
  }

  getDefaultTheme(): Theme {
    return ThemeUtil.DEFAULT_THEME;
  }

  private loadSavedTheme(): void {
    const savedTheme = this.themeStorage.getTheme();
    if (savedTheme !== null) {
      const themeEnum = ThemeUtil.getThemeEnum(Number(savedTheme));
      this.setTheme(themeEnum);
    } else {
      this.setTheme(this.getDefaultTheme());
    }
  }
}
