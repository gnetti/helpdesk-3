import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {PersonRepositoryPort} from '@domain/ports/out/person-repository.port';
import {Observable} from 'rxjs';
import {environment} from "@env/environment";
import {PaginatedPersonResponse, Person} from "@model//person.model";

@Injectable({
  providedIn: 'root'
})
export class PersonRepositoryAdapter implements PersonRepositoryPort {
  private apiUrl = `${environment.apiUrl}/persons`;

  constructor(private http: HttpClient) {
  }

  createPerson(person: Person): Observable<Person> {
    return this.http.post<Person>(this.apiUrl, person);
  }

  getAllPersons(params: { page: number; size: number }): Observable<PaginatedPersonResponse> {
    const httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString());
    return this.http.get<PaginatedPersonResponse>(this.apiUrl, {params: httpParams});
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
