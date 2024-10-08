import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { CommonModule, NgOptimizedImage } from "@angular/common";
import { ReactiveFormsModule } from "@angular/forms";
import { RouterLink, RouterOutlet } from "@angular/router";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatSelectModule } from "@angular/material/select";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatMenuModule } from "@angular/material/menu";
import { MatToolbarModule } from "@angular/material/toolbar";
import { GravatarModule } from "ngx-gravatar";

import { SETTINGS_USE_CASE_PORT } from "@domain/ports/in/settings-use-case.port";
import { SETTINGS_REPOSITORY_PORT } from "@domain/ports/out/settings-repository.port";
import { SettingsRepositoryAdapter } from "@adapters/out/persistence/settings.repository.adapter";
import { SettingsComponent } from "@adapters/in/web/settings/settings.component";
import { SettingsUseCase } from "@application/services/setting.service.";
import { PersonModule } from "@modules/person.module";
import { ThemeTitleCasePipe } from "@shared/pipes/theme-title-case.pipe";
import { MenuComponent } from "@adapters/in/web/menu/menu.component";

@NgModule({
  declarations: [
    SettingsComponent,
    ThemeTitleCasePipe,
    MenuComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterOutlet,
    RouterLink,
    NgOptimizedImage,
    PersonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatSelectModule,
    MatTooltipModule,
    MatProgressBarModule,
    MatMenuModule,
    MatToolbarModule,
    GravatarModule
  ],
  providers: [
    { provide: SETTINGS_USE_CASE_PORT, useClass: SettingsUseCase },
    { provide: SETTINGS_REPOSITORY_PORT, useClass: SettingsRepositoryAdapter }
  ],
  exports: [
    SettingsComponent,
    MenuComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SettingsModule { }
