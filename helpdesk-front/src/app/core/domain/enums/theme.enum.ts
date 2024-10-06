export enum Theme {
  AZURE_BLUE = 0,
  CYAN_ORANGE = 1,
  DEEP_PURPLE_AMBER = 2,
  INDIGO_PINK = 3,
  MAGENTA_VIOLET = 4,
  PINK_BLUE_GREY = 5,
  PURPLE_GREEN = 6,
  ROSE_RED = 7
}

export const ThemeNames: Record<Theme, string> = {
  [Theme.AZURE_BLUE]: "azureBlue",
  [Theme.CYAN_ORANGE]: "cyanOrange",
  [Theme.DEEP_PURPLE_AMBER]: "deepPurpleAmber",
  [Theme.INDIGO_PINK]: "indigoPink",
  [Theme.MAGENTA_VIOLET]: "magentaViolet",
  [Theme.PINK_BLUE_GREY]: "pinkBlueGrey",
  [Theme.PURPLE_GREEN]: "purpleGreen",
  [Theme.ROSE_RED]: "roseRed"
};
