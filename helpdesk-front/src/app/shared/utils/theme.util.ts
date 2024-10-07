import { environment } from "src/environments/environment";
import {Theme, ThemeNames} from "@enums//theme.enum";


export class ThemeUtil {
  static readonly DEFAULT_THEME: Theme =
    ThemeUtil.isValidTheme(Number(environment.defaultTheme))
      ? Number(environment.defaultTheme) as Theme
      : Theme.INDIGO_PINK;

  static getThemeEnum(theme: number | string): Theme {
    if (typeof theme === 'number') {
      return ThemeUtil.isValidTheme(theme) ? theme : ThemeUtil.DEFAULT_THEME;
    }
    const themeEntry = Object.entries(ThemeNames).find(([_, value]) =>
      value.toLowerCase() === theme.toLowerCase().replace(/-/g, '_')
    );
    return themeEntry ? parseInt(themeEntry[0]) as Theme : ThemeUtil.DEFAULT_THEME;
  }

  static createOrGetThemeLinkElement(): HTMLLinkElement {
    let themeLink = document.querySelector("link[data-theme=\"true\"]") as HTMLLinkElement;
    if (!themeLink) {
      themeLink = document.createElement("link");
      themeLink.rel = "stylesheet";
      themeLink.dataset["theme"] = "true";
      document.head.appendChild(themeLink);
    }
    return themeLink;
  }

  static isValidTheme(value: number): boolean {
    return value >= 0 && value < Object.keys(Theme).length / 2;
  }

  static getThemeName(theme: Theme): string {
    return ThemeNames[theme];
  }
}
