import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {PersonListComponent} from "@adapters/in/web/person/person-list/person-list.component";
import {PERSON_USE_CASE_PORT} from "@domain/ports/in/person-use-case.port";
import {PERSON_REPOSITORY_PORT} from "@domain/ports/out/person-repository.port";
import {PersonRepositoryAdapter} from "@adapters/out/persistence/person-repository.adapter";
import {PersonService} from "@application/services/person.service";
import {SpinnerComponent} from "@shared/components/spinner/spinner.component";
import {RouterLink} from "@angular/router";
import {ToastrModule} from "ngx-toastr";
import {CoolDialogsModule} from "@angular-cool/dialogs";
import {CEP_SERVICE_PORT} from "@application/services/cep-service.port";
import {CepServiceAdapter} from "@adapters/out/persistence/cep-service.adapter";
import {GetAddressByCepUseCase} from "@domain/ports/in/get-address-by-cep.use-case";
import {PersonCreateComponent} from "@adapters/in/web/person/person-create/person-create.component";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import {MatCheckbox} from "@angular/material/checkbox";
import {NgxMaskDirective, NgxMaskPipe, provideNgxMask} from "ngx-mask";
import { MatOption, MatSelect } from "@angular/material/select";


@NgModule({
  declarations: [
    PersonListComponent,
    SpinnerComponent,
    PersonCreateComponent
  ],
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    RouterLink,
    NgxMaskDirective,
    NgxMaskPipe,
    CoolDialogsModule.forRoot(),
    ToastrModule.forRoot({
      timeOut: 4000,
      closeButton: true,
      progressBar: true
    }),
    ReactiveFormsModule,
    MatCheckbox,
    NgxMaskDirective,
    MatSelect,
    MatOption,
    FormsModule
  ],
  exports: [PersonListComponent, SpinnerComponent],
  providers: [
    {provide: PERSON_USE_CASE_PORT, useClass: PersonService},
    {provide: PERSON_REPOSITORY_PORT, useClass: PersonRepositoryAdapter},
    {provide: CEP_SERVICE_PORT, useClass: CepServiceAdapter},
    provideNgxMask(),
    GetAddressByCepUseCase

  ]
})
export class PersonModule {
}
