import { Person, Address } from '@core/domain/models/person.model';

export interface CreatePersonDTO extends Omit<Person, 'id' | 'creationDate' | '_links'> {
  password: string;
  address?: Address;
}
