import { Observable } from "rxjs";
import { GetAllPersonsParams, PersonHateoasResponse } from "@dto//hateoas-response.dto";
import { PaginatedPersonResponse, Person } from "@model//person.model";
import { InjectionToken } from "@angular/core";

export interface PersonUseCasePort {

  getAllPersons(params: GetAllPersonsParams): Observable<PaginatedPersonResponse | PersonHateoasResponse>;

  getPersonById(id: number): Observable<Person>;

  createPerson(person: Person): Observable<Person>;

  updatePerson(id: number, person: Person): Observable<Person>;

  deletePerson(id: number): Observable<void>;

  getPersonByCpf(cpf: string): Observable<Person>;

  getPersonByEmail(email: string): Observable<Person>;

  existsPersonById(id: number): Observable<boolean>;
}

export const PERSON_USE_CASE_PORT = new InjectionToken<PersonUseCasePort>("PersonUseCasePort");
