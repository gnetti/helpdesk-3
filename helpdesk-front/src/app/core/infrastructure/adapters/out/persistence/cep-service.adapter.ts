import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, map} from 'rxjs';
import {CepServicePort} from "@application/services/cep-service.port";
import {environment} from "@env/environment";
import {Address} from "@model//person.model";


@Injectable({
  providedIn: 'root'
})
export class CepServiceAdapter implements CepServicePort {
  private apiUrl = environment.apiCepUrl;

  constructor(private http: HttpClient) {
  }

  getAddressByCep(cep: string): Observable<Address> {
    return this.http.get<any>(`${this.apiUrl}/${cep}/json`).pipe(
      map(response => this.mapResponseToAddress(response))
    );
  }

  private mapResponseToAddress(response: any): Address {
    return {
      street: response.logradouro,
      number: '',
      neighborhood: response.bairro,
      city: response.localidade,
      state: response.uf,
      zipCode: response.cep,
      complement: response.complemento || undefined
    };
  }
}
