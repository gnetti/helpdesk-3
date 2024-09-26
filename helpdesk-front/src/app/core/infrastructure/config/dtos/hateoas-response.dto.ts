import {Person} from '@model//person.model';

export interface HateoasResponse<T> {
  _embedded: {
    [key: string]: T[];
  };
  _links: {
    first: { href: string };
    self: { href: string };
    next?: { href: string };
    prev?: { href: string };
    last: { href: string };
  };
  page: {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
  };
}

export interface PaginationMetadata {
  minPageSize: number;
  maxPageSize: number;
  defaultPage: number;
  defaultSize: number;
}

export interface PersonHateoasResponse extends HateoasResponse<Person>, PaginationMetadata {
  content: Person[];
}

export interface PaginatedPersonResponse extends PaginationMetadata {
  content: Person[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  numberOfElements: number;
}
