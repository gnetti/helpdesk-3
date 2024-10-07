import {AfterViewInit, Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {ToastrService} from 'ngx-toastr';
import {CoolDialogService} from '@angular-cool/dialogs';
import {PERSON_USE_CASE_PORT, PersonUseCasePort} from '@core/domain/ports/in/person-use-case.port';
import {PaginatedPersonResponse, Person} from '@core/domain/models/person.model';
import {PersonHateoasResponse} from '@infrastructure/config/dtos/hateoas-response.dto';
import {catchError, finalize, switchMap} from 'rxjs/operators';
import {of} from 'rxjs';
import {FormUtils} from "@utils//form.util";
import {DialogUtils} from "@utils//dialog.util";

type PersonResponse = PaginatedPersonResponse | PersonHateoasResponse;

@Component({
  selector: 'app-person-list',
  templateUrl: './person-list.component.html',
  styleUrls: ['./person-list.component.css']
})
export class PersonListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['id', 'name', 'cpf', 'email', 'actions'];
  dataSource: MatTableDataSource<Person> = new MatTableDataSource<Person>([]);
  isLoading: boolean = false;
  totalElements: number = 0;
  pageSize: number = 5;
  pageIndex: number = 0;
  pageSizeOptions: number[] = [5, 10, 25, 50, 100];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    @Inject(PERSON_USE_CASE_PORT) private personService: PersonUseCasePort,
    private toast: ToastrService,
    private coolDialogService: CoolDialogService
  ) {
  }

  ngOnInit(): void {
    this.loadPersons();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  loadPersons(): void {
    this.isLoading = true;
    const params = FormUtils.getPageParams(this.pageIndex, this.pageSize);
    this.personService.getAllPersons(params).pipe(
      catchError(() => {
        this.handleError('Erro ao carregar a lista de pessoas');
        return of(null);
      }),
      finalize(() => this.isLoading = false)
    ).subscribe({
      next: (response: PersonResponse | null) => {
        if (response) {
          this.handlePersonsResponse(response);
        }
      },
      error: () => {
        this.handleError('Erro ao carregar a lista de pessoas');
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadPersons();
  }

  applyFilter(event: Event): void {
    FormUtils.applyFilter(event, this.dataSource);
  }

  async deletePerson(id: number, name: string): Promise<void> {
    const result = await DialogUtils.showDeleteConfirmation(this.coolDialogService, name);
    if (result) {
      this.isLoading = true;
      this.personService.deletePerson(id).pipe(
        catchError(() => {
          this.toast.error('Erro ao excluir a pessoa');
          return of(null);
        }),
        switchMap(() => {
          this.toast.success('Pessoa excluída com sucesso', 'Sucesso');
          return this.personService.getAllPersons(FormUtils.getPageParams(this.pageIndex, this.pageSize));
        }),
        catchError(() => {
          this.toast.warning('Erro ao recarregar a lista de pessoas');
          return of(null);
        }),
        finalize(() => this.isLoading = false)
      ).subscribe({
        next: (response: PersonResponse | null) => {
          if (response) {
            this.handlePersonsResponse(response);
          }
        },
        error: () => {
          this.toast.error('Erro ao processar a exclusão');
        }
      });
    }
  }

  editPerson(id: number): void {
    this.toast.info(`Funcionalidade de edição a ser implementada para o ID: ${id}`, 'Info');
  }

  viewPerson(id: number): void {
    this.toast.info(`Funcionalidade de visualização a ser implementada para o ID: ${id}`, 'Info');
  }

  private handlePersonsResponse(response: PersonResponse): void {
    if ('_embedded' in response) {
      this.dataSource.data = response._embedded['personDTOList'];
      this.totalElements = response.page.totalElements;
      this.pageSize = response.page.size;
      this.pageIndex = response.page.number;
    } else {
      this.dataSource.data = response.content;
      this.totalElements = response.totalElements;
      this.pageSize = response.pageSize;
      this.pageIndex = response.pageNumber;
    }

    this.pageSizeOptions = [
      response.minPageSize,
      response.defaultSize,
      response.maxPageSize
    ].filter((value, index, self) => self.indexOf(value) === index && value > 0);

    if (this.paginator) {
      this.paginator.pageIndex = this.pageIndex;
      this.paginator.pageSize = this.pageSize;
      this.paginator.length = this.totalElements;
    }
  }

  private handleError(message: string): void {
    this.toast.error(message, 'Erro');
  }
}
