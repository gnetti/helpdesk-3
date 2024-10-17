import { HttpParams } from "@angular/common/http";
import { MatTableDataSource } from "@angular/material/table";
import { Sort } from "@angular/material/sort";
import { GetAllPersonsParams, PersonHateoasResponse } from "@core/infrastructure/config/dtos/hateoas-response.dto";
import { PaginatedPersonResponse, Person } from "@model//person.model";

export class PersonUtil {
  static buildHttpParams(params: GetAllPersonsParams): HttpParams {
    let httpParams = new HttpParams()
      .set("page", params.page.toString())
      .set("size", params.size.toString());

    if (params.sort) {
      httpParams = httpParams.set("sort", params.sort);
    }
    if (params.name) {
      httpParams = httpParams.set("name", params.name);
    }
    if (params.cpf) {
      httpParams = httpParams.set("cpf", params.cpf);
    }
    if (params.email) {
      httpParams = httpParams.set("email", params.email);
    }
    if (params.profile) {
      if (Array.isArray(params.profile)) {
        params.profile.forEach(p => httpParams = httpParams.append("profile", p.toString()));
      } else {
        httpParams = httpParams.set("profile", params.profile.toString());
      }
    }
    if (params.creationDate) {
      httpParams = httpParams.set("creationDate", params.creationDate);
    }
    if (params.creationDateFrom) {
      httpParams = httpParams.set("creationDateFrom", params.creationDateFrom);
    }
    if (params.creationDateTo) {
      httpParams = httpParams.set("creationDateTo", params.creationDateTo);
    }
    if (params.theme) {
      if (Array.isArray(params.theme)) {
        params.theme.forEach(t => httpParams = httpParams.append("theme", t.toString()));
      } else {
        httpParams = httpParams.set("theme", params.theme.toString());
      }
    }

    return httpParams;
  }

  static getSortString(sort: Sort): string | undefined {
    return sort.direction ? `${sort.active},${sort.direction}` : undefined;
  }

  static handlePersonsResponse(
    response: PaginatedPersonResponse | PersonHateoasResponse | null,
    dataSource: MatTableDataSource<Person>,
    paginator: any
  ): void {
    if (!response) {
      dataSource.data = [];
      if (paginator) {
        paginator.length = 0;
      }
      return;
    }

    dataSource.data = response.content;

    if (paginator) {
      if ("page" in response) {
        paginator.length = response.page.totalElements;
        paginator.pageIndex = response.page.number;
        paginator.pageSize = response.page.size;
      } else {
        paginator.length = response.totalElements;
        paginator.pageIndex = response.pageNumber;
        paginator.pageSize = response.pageSize;
      }
    }
  }

  static generatePagination(currentPage: number, totalPages: number): number[] {
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(totalPages - 1, startPage + 4);
    return Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);
  }

  static resetPagination(paginator: any): void {
    if (paginator) {
      paginator.pageIndex = 0;
    }
  }
}
