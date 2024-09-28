import {environment} from "src/environments/environment";
import {Theme} from "@enums//theme.enum";

export class ThemeUtils {
  static readonly DEFAULT_THEME: Theme = Theme[environment.defaultTheme as keyof typeof Theme] || Theme.INDIGO_PINK;

  static getThemeEnum(theme: Theme | string): Theme {
    if (typeof theme === 'string') {
      const themeKey = Object.keys(Theme).find(key =>
        key.toLowerCase() === theme.toLowerCase().replace(/-/g, '_'));
      return (themeKey && Theme[themeKey as keyof typeof Theme]) || ThemeUtils.DEFAULT_THEME;
    }
    return theme;
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
}
