import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Profile } from "@enums//profile.enum";
import { TokenTimeValidator } from "@validators//token-time.validator";
import { bigDecimalValidator } from "@validators//big-decimal.validator";
import { ProfileUtils } from "@utils//profile.util";
import { TokenTimeProfile } from "@model//token-time-profile.model";

export class SettingsUtil {
  static initForm(fb: FormBuilder): FormGroup {
    return fb.group({
      profileCode: [Profile.ADMIN, Validators.required],
      tokenExpirationTimeMinutes: ["", [
        Validators.required,
        TokenTimeValidator.validateTokenExpiration,
        bigDecimalValidator(60, 1440)
      ]],
      timeToShowDialogMinutes: ["", [
        Validators.required,
        TokenTimeValidator.validateTimeToShowDialog,
        bigDecimalValidator(15, 30)
      ]],
      dialogDisplayTimeForTokenUpdateMinutes: ["", [
        Validators.required,
        bigDecimalValidator(2, 15)
      ]],
      tokenUpdateIntervalMinutes: ["", [
        Validators.required,
        bigDecimalValidator(1, 5)
      ]]
    }, {
      validators: TokenTimeValidator.validateTokenTime()
    });
  }

  static getProfiles(): { value: Profile; label: string }[] {
    return Object.values(Profile)
      .filter((value): value is Profile => typeof value === "number" && value !== Profile.ROOT)
      .map(profile => ({
        value: profile,
        label: ProfileUtils.getProfileName(profile)
      }));
  }

  static getDefaultSettings(): Partial<TokenTimeProfile> {
    return {
      tokenExpirationTimeMinutes: 60,
      timeToShowDialogMinutes: 30,
      dialogDisplayTimeForTokenUpdateMinutes: 15,
      tokenUpdateIntervalMinutes: 5
    };
  }
}
