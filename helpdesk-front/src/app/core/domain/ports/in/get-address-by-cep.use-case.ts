import {Injectable, Inject} from '@angular/core';
import {Observable} from 'rxjs';
import {CEP_SERVICE_PORT, CepServicePort} from "@application/services/cep-service.port";
import {Address} from "@model//person.model";

@Injectable({
  providedIn: 'root'
})
export class GetAddressByCepUseCase {
  constructor(@Inject(CEP_SERVICE_PORT) private cepService: CepServicePort) {
  }

  execute(cep: string): Observable<Address> {
    return this.cepService.getAddressByCep(cep);
  }
}
