export interface TokenTimeProfile {
  id?: number;
  profileCode: number;
  tokenExpirationTimeMinutes: number;
  timeToShowDialogMinutes: number;
  dialogDisplayTimeForTokenUpdateMinutes: number;
  tokenUpdateIntervalMinutes: number;
}
