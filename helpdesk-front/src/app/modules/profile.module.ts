import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
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

import { PROFILE_USE_CASE_PORT } from "@domain/ports/in/profile-use-case.port";
import { PROFILE_REPOSITORY_PORT } from "@domain/ports/out/profile-repository.port";
import { ProfileRepositoryAdapter } from "@adapters/out/persistence/profile.repository.adapter";
import { ProfileComponent } from "@adapters/in/web/profile/profile.component";
import { PersonModule } from "@modules/person.module";
import { ThemeTitleCasePipe } from "@shared/pipes/theme-title-case.pipe";
import { MenuComponent } from "@adapters/in/web/menu/menu.component";
import { ProfileUseCase } from "@application/services/profile.service";

@NgModule({
  declarations: [
    ProfileComponent,
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
    { provide: PROFILE_USE_CASE_PORT, useClass: ProfileUseCase },
    { provide: PROFILE_REPOSITORY_PORT, useClass: ProfileRepositoryAdapter }
  ],
  exports: [
    ProfileComponent,
    MenuComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProfileModule {
}
