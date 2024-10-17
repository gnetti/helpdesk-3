import { HttpParams } from "@angular/common/http";
import { GetAllPersonsParams } from "@dto//hateoas-response.dto";

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
}
