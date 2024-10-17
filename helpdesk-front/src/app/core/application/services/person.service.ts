import { Inject, Injectable } from "@angular/core";
import { PersonUseCasePort } from "@domain/ports/in/person-use-case.port";
import { PERSON_REPOSITORY_PORT, PersonRepositoryPort } from "@domain/ports/out/person-repository.port";
import { PaginatedPersonResponse, Person } from "@model//person.model";
import { Observable } from "rxjs";
import { GetAllPersonsParams, PersonHateoasResponse } from "@dto//hateoas-response.dto";

@Injectable({
  providedIn: "root"
})
export class PersonService implements PersonUseCasePort {
  constructor(@Inject(PERSON_REPOSITORY_PORT) private personRepository: PersonRepositoryPort) {}

  createPerson(person: Person): Observable<Person> {
    return this.personRepository.createPerson(person);
  }

  getAllPersons(params: GetAllPersonsParams): Observable<PaginatedPersonResponse | PersonHateoasResponse> {
    return this.personRepository.getAllPersons(params);
  }

  getPersonById(id: number): Observable<Person> {
    return this.personRepository.getPersonById(id);
  }

  updatePerson(id: number, person: Person): Observable<Person> {
    return this.personRepository.updatePerson(id, person);
  }

  deletePerson(id: number): Observable<void> {
    return this.personRepository.deletePerson(id);
  }

  getPersonByCpf(cpf: string): Observable<Person> {
    return this.personRepository.getPersonByCpf(cpf);
  }

  getPersonByEmail(email: string): Observable<Person> {
    return this.personRepository.getPersonByEmail(email);
  }

  existsPersonById(id: number): Observable<boolean> {
    return this.personRepository.existsPersonById(id);
  }
}
