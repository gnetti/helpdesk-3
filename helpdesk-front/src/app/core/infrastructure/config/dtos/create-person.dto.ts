import { Person, Address } from '@model//person.model';

export interface CreatePersonDTO extends Omit<Person, 'id' | 'creationDate' | '_links'> {
  password: string;
  address?: Address;
}
