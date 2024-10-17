import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { PersonRepositoryPort } from "@domain/ports/out/person-repository.port";
import { Observable } from "rxjs";
import { environment } from "@env/environment";
import { PaginatedPersonResponse, Person } from "@model//person.model";
import { GetAllPersonsParams } from "@dto//hateoas-response.dto";
import { PersonUtil } from "@utils//person.util";

@Injectable({
  providedIn: "root"
})
export class PersonRepositoryAdapter implements PersonRepositoryPort {
  private apiUrl = `${environment.apiUrl}/persons`;

  constructor(private http: HttpClient) {}

  createPerson(person: Person): Observable<Person> {
    return this.http.post<Person>(this.apiUrl, person);
  }

  getAllPersons(params: GetAllPersonsParams): Observable<PaginatedPersonResponse> {
    const httpParams = PersonUtil.buildHttpParams(params);
    return this.http.get<PaginatedPersonResponse>(`${this.apiUrl}/all`, { params: httpParams });
  }

  getPersonById(id: number): Observable<Person> {
    return this.http.get<Person>(`${this.apiUrl}/${id}`);
  }

  updatePerson(id: number, person: Person): Observable<Person> {
    return this.http.put<Person>(`${this.apiUrl}/${id}`, person);
  }

  deletePerson(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getPersonByCpf(cpf: string): Observable<Person> {
    return this.http.get<Person>(`${this.apiUrl}/cpf/${cpf}`);
  }

  getPersonByEmail(email: string): Observable<Person> {
    return this.http.get<Person>(`${this.apiUrl}/email/${email}`);
  }

  existsPersonById(id: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${id}/exists`);
  }
}
