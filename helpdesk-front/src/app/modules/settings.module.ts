import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {SETTINGS_USE_CASE_PORT} from "@domain/ports/in/settings-use-case.port";
import {SETTINGS_REPOSITORY_PORT} from "@domain/ports/out/settings-repository.port";
import {SettingsHttpRepository} from "@adapters/out/persistence/settings.repository.adapter";
import {SettingsUseCase} from "@application/services/settings.service.";

@NgModule({
  declarations: [],
  imports: [
    CommonModule,

  ],
  providers: [
    {provide: SETTINGS_USE_CASE_PORT, useClass: SettingsUseCase},
    {provide: SETTINGS_REPOSITORY_PORT, useClass: SettingsHttpRepository}
  ]
})
export class SettingsModule {
}
