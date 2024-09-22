import {Observable} from 'rxjs';
import {PaginatedPersonResponse, Person} from '@core/domain/models/person.model';

export interface PersonUseCasePort {
  getAllPersons(params: { page: number; size: number }): Observable<PaginatedPersonResponse>;

  getPersonById(id: number): Observable<Person>;

  createPerson(person: Person): Observable<Person>;

  updatePerson(id: number, person: Person): Observable<Person>;

  deletePerson(id: number): Observable<void>;
}

export const PERSON_USE_CASE_PORT = 'PersonUseCasePort';
