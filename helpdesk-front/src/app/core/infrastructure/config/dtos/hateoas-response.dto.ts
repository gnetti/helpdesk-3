export interface PageInfo {
  size: number;
  totalElements: number;
  totalPages: number;
  number: number;
}

export interface PaginationMetadata {
  minPageSize: number;
  maxPageSize: number;
  defaultPage: number;
  defaultSize: number;
}

export interface HateoasLinks {
  first: { href: string };
  self: { href: string };
  next?: { href: string };
  prev?: { href: string };
  last: { href: string };
}

export interface HateoasResponse<T> {
  _embedded: {
    [key: string]: T[];
  };
  _links: HateoasLinks;
  page: PageInfo;
}

export interface PersonHateoasResponse extends HateoasResponse<Person>, PaginationMetadata {
  content: Person[];
}

export interface PaginatedPersonResponse {
  pageNumber: number;
  pageSize: number;
  content: Person[];
  totalElements: number;
  totalPages: number;
  minPageSize: number;
  maxPageSize: number;
  defaultPage: number;
  defaultSize: number;
  numberOfElements: number;
}

export interface Person {
  id?: number;
  name: string;
  cpf: string;
  email: string;
  profiles: number[];
  creationDate?: string;
  address?: Address;
  theme?: number;
  _links?: {
    self: { href: string };
    [key: string]: { href: string };
  };
}

export interface Address {
  id?: number;
  street: string;
  complement?: string;
  neighborhood: string;
  city: string;
  state: string;
  zipCode: string;
  number: string;
}

export interface PageParams {
  page: number;
  size: number;
  sort?: string;
}

export interface GetAllPersonsParams extends PageParams {
  name?: string;
  cpf?: string;
  email?: string;
  profile?: number | number[];
  creationDate?: string;
  creationDateFrom?: string;
  creationDateTo?: string;
  theme?: number | number[];
}

