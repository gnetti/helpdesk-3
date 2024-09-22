import {InjectionToken} from '@angular/core';
import {Observable} from 'rxjs';
import {Address} from "@model//person.model";

export interface CepServicePort {
  getAddressByCep(cep: string): Observable<Address>;
}

export const CEP_SERVICE_PORT = new InjectionToken<CepServicePort>('CepServicePort');
