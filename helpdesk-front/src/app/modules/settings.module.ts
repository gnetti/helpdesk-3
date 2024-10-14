import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { CommonModule, NgOptimizedImage } from "@angular/common";
import { ReactiveFormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatSelectModule } from "@angular/material/select";
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from "@angular/material/tooltip";
import { ToastrModule } from "ngx-toastr";

import { TOKEN_TIME_USE_CASE_PORT } from "@domain/ports/in/token-time-use-case.port";
import { TOKEN_TIME_REPOSITORY_PORT } from "@domain/ports/out/token-time-repository.port";
import { TokenTimeService } from "@application/services/token-time.service";
import { TokenTimeRepositoryAdapter } from "@adapters/out/persistence/token-time-repository.adapter";
import { AuthService } from "@application/services/auth.service";
import { PersonModule } from "@modules/person.module";
import { TokenTimeModule } from "@modules/token-time.module";
import { SettingsComponent } from "@adapters/in/web/settings/settings.component";
import { BigDecimalDirectiveMask } from "@domain/masks/big-decimal-directive.mask";

@NgModule({
  declarations: [
    SettingsComponent,
    BigDecimalDirectiveMask
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatIconModule,
    MatTooltipModule,
    PersonModule,
    NgOptimizedImage,
    TokenTimeModule,
    ToastrModule.forRoot()

  ],
  providers: [
    { provide: TOKEN_TIME_USE_CASE_PORT, useClass: TokenTimeService },
    { provide: TOKEN_TIME_REPOSITORY_PORT, useClass: TokenTimeRepositoryAdapter },
    AuthService
  ],
  exports: [SettingsComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SettingsModule {
}
