import { Inject, Injectable } from "@angular/core";
import { ThemeUseCasePort } from "@domain/ports/in/theme-use-case.port";
import { THEME_SERVICE_PORT, ThemeStoragePort } from "@domain/ports/out/theme.storage.port";
import { Theme } from "@enums//theme.enum";
import { ThemeConfig } from "@infrastructure/config/theme.config";

@Injectable({
  providedIn: "root"
})
export class ThemeService implements ThemeUseCasePort {
  private themeLinkElement: HTMLLinkElement;
  private readonly DEFAULT_THEME = Theme.INDIGO_PINK;

  constructor(
    @Inject(THEME_SERVICE_PORT) private themeStorage: ThemeStoragePort
  ) {
    this.themeLinkElement = this.createOrGetThemeLinkElement();
    this.loadSavedTheme();
  }

  setTheme(theme: Theme) {
    const themeFileName = ThemeConfig.getThemeFileName(theme);
    if (themeFileName) {
      this.themeLinkElement.href = `/themes/${themeFileName}`;
      this.themeStorage.setTheme(theme);
    } else {
      throw new Error(`Arquivo de tema não encontrado para: ${theme}`);
    }
  }

  getCurrentTheme(): Theme {
    return this.themeStorage.getTheme() as Theme || this.DEFAULT_THEME;
  }

  clearTheme() {
    this.setTheme(this.DEFAULT_THEME);
  }

  setThemeFromToken(decodedToken: any): void {
    const theme = decodedToken?.theme || this.DEFAULT_THEME;
    this.setTheme(theme);
  }

  resetTheme(): void {
    this.setTheme(this.DEFAULT_THEME);
  }

  private createOrGetThemeLinkElement(): HTMLLinkElement {
    let themeLink = document.querySelector("link[data-theme=\"true\"]") as HTMLLinkElement;
    if (!themeLink) {
      themeLink = document.createElement("link");
      themeLink.rel = "stylesheet";
      themeLink.dataset["theme"] = "true";
      document.head.appendChild(themeLink);
    }
    return themeLink;
  }

  private loadSavedTheme() {
    const savedTheme = this.getCurrentTheme();
    this.setTheme(savedTheme);
  }
}
