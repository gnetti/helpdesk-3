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
