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
  [Theme.AZURE_BLUE]: "Azure Blue",
  [Theme.CYAN_ORANGE]: "Cyan Orange",
  [Theme.DEEP_PURPLE_AMBER]: "Deep Purple Amber",
  [Theme.INDIGO_PINK]: "Indigo Pink",
  [Theme.MAGENTA_VIOLET]: "Magenta Violet",
  [Theme.PINK_BLUE_GREY]: "Pink Blue Grey",
  [Theme.PURPLE_GREEN]: "Purple Green",
  [Theme.ROSE_RED]: "Rose Red"
};
