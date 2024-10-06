import {Theme, ThemeNames} from "@enums//theme.enum";

export class ThemeConfig {
  private static readonly themeFiles: Record<number, string> = {
    [Theme.AZURE_BLUE]: "azure_blue.css",
    [Theme.CYAN_ORANGE]: "cyan_orange.css",
    [Theme.DEEP_PURPLE_AMBER]: "deep_purple_amber.css",
    [Theme.INDIGO_PINK]: "indigo_pink.css",
    [Theme.MAGENTA_VIOLET]: "magenta_violet.css",
    [Theme.PINK_BLUE_GREY]: "pink_blue_grey.css",
    [Theme.PURPLE_GREEN]: "purple_green.css",
    [Theme.ROSE_RED]: "rose_red.css"
  };

  static getThemeFileName(theme: Theme): string | undefined {
    return ThemeConfig.themeFiles[theme];
  }

  static getThemeName(theme: Theme): string {
    return ThemeNames[theme];
  }
}
