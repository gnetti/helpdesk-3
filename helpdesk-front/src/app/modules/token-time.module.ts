import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ReactiveFormsModule } from "@angular/forms";

import { TOKEN_TIME_USE_CASE_PORT } from "@domain/ports/in/token-time-use-case.port";
import { TOKEN_TIME_REPOSITORY_PORT } from "@domain/ports/out/token-time-repository.port";
import { TokenTimeService } from "@application/services/token-time.service";
import { TokenTimeRepositoryAdapter } from "@adapters/out/persistence/token-time-repository.adapter";

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  providers: [
    { provide: TOKEN_TIME_USE_CASE_PORT, useClass: TokenTimeService },
    { provide: TOKEN_TIME_REPOSITORY_PORT, useClass: TokenTimeRepositoryAdapter }
  ],
  exports: [
    ReactiveFormsModule
  ]
})
export class TokenTimeModule {
}
