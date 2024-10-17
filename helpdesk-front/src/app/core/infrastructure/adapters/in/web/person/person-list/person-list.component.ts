import { AfterViewInit, Component, Inject, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort, Sort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { ToastrService } from "ngx-toastr";
import { CoolDialogService } from "@angular-cool/dialogs";
import { PERSON_USE_CASE_PORT, PersonUseCasePort } from "@core/domain/ports/in/person-use-case.port";
import {
  GetAllPersonsParams,
  PaginatedPersonResponse,
  Person,
  PersonHateoasResponse
} from "@infrastructure/config/dtos/hateoas-response.dto";
import { catchError, debounceTime, distinctUntilChanged, switchMap, takeUntil, tap } from "rxjs/operators";
import { BehaviorSubject, merge, of, Subject } from "rxjs";
import { DialogUtils } from "@utils//dialog.util";

type PersonResponse = PaginatedPersonResponse | PersonHateoasResponse;

@Component({
  selector: "app-person-list",
  templateUrl: "./person-list.component.html",
  styleUrls: ["./person-list.component.css"]
})
export class PersonListComponent implements OnInit, AfterViewInit, OnDestroy {
  displayedColumns: string[] = ["id", "name", "cpf", "actions"];
  dataSource = new MatTableDataSource<Person>([]);
  isLoading = new BehaviorSubject<boolean>(false);
  totalElements = 0;
  pageSize = 10;
  pageSizeOptions: number[] = [5, 10, 25, 100];
  currentSort: Sort = { active: "", direction: "" };
  private destroy$ = new Subject<void>();
  filterSubject = new BehaviorSubject<string>("");
  currentPage = 0;
  totalPages = 1;
  pages: number[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    @Inject(PERSON_USE_CASE_PORT) private personService: PersonUseCasePort,
    private toast: ToastrService,
    private coolDialogService: CoolDialogService
  ) {
  }

  ngOnInit(): void {
    this.setupFilterListener();
    this.loadPersons();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;

    if (this.sort && this.paginator) {
      this.setupSortAndPaginationListeners();
    }
  }

  private setupSortAndPaginationListeners() {
    merge(
      this.sort.sortChange,
      this.paginator.page
    ).pipe(
      tap(() => {
        this.currentPage = this.paginator.pageIndex;
        this.pageSize = this.paginator.pageSize;
        this.loadPersons();
      }),
      takeUntil(this.destroy$)
    ).subscribe();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSortChange(sort: Sort) {
    this.currentSort = sort;
    this.loadPersons();
  }

  loadPersons() {
    this.isLoading.next(true);
    const params: GetAllPersonsParams = {
      page: this.currentPage,
      size: this.pageSize,
      sort: this.getSortString()
    };

    if (this.filterSubject.value) {
      params.name = this.filterSubject.value;
    }

    console.log("Params for loading persons:", params);

    this.personService.getAllPersons(params).pipe(
      catchError((error) => {
        console.error("Error loading persons:", error);
        this.toast.error("Erro ao carregar a lista de pessoas");
        return of(null);
      }),
      tap(() => this.isLoading.next(false))
    ).subscribe(this.handlePersonsResponse.bind(this));
  }

  private getSortString(): string | undefined {
    if (this.currentSort.direction === "") {
      return undefined;
    }
    return `${this.currentSort.active},${this.currentSort.direction}`;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    console.log("Filter value applied:", filterValue);
    this.filterSubject.next(filterValue.trim().toLowerCase());
  }

  setupFilterListener() {
    this.filterSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      tap(value => console.log("Filter value after debounce:", value)),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.resetPagination();
      this.loadPersons();
    });
  }

  resetPagination() {
    this.currentPage = 0;
    if (this.paginator) {
      this.paginator.pageIndex = 0;
    }
  }

  async deletePerson(id: number, name: string) {
    const result = await DialogUtils.showDeleteConfirmation(this.coolDialogService, name);
    if (result) {
      this.isLoading.next(true);
      this.personService.deletePerson(id).pipe(
        catchError(() => {
          this.toast.error("Erro ao excluir a pessoa");
          return of(null);
        }),
        switchMap(() => {
          this.toast.success("Pessoa excluída com sucesso", "Sucesso");
          return this.personService.getAllPersons({
            page: this.currentPage,
            size: this.pageSize,
            sort: this.getSortString(),
            name: this.filterSubject.value || undefined
          });
        }),
        catchError(() => {
          this.toast.warning("Erro ao recarregar a lista de pessoas");
          return of(null);
        }),
        tap(() => this.isLoading.next(false))
      ).subscribe((response) => {
        if (response) {
          this.handlePersonsResponse(response);
        }
      });
    }
  }

  editPerson(id: number) {
    this.toast.info(`Funcionalidade de edição a ser implementada para o ID: ${id}`, "Info");
  }

  viewPerson(id: number) {
    this.toast.info(`Funcionalidade de visualização a ser implementada para o ID: ${id}`, "Info");
  }

  private handlePersonsResponse(response: PersonResponse | null) {
    if (!response) {
      this.dataSource.data = [];
      this.totalElements = 0;
      return;
    }

    this.dataSource.data = response.content;

    if ("page" in response) {
      this.totalElements = response.page.totalElements;
      this.pageSize = response.page.size;
      this.currentPage = response.page.number;
      this.totalPages = response.page.totalPages;
    } else {
      this.totalElements = response.totalElements;
      this.pageSize = response.pageSize;
      this.currentPage = response.pageNumber;
      this.totalPages = response.totalPages;
    }

    this.generatePagination();

    if (this.paginator) {
      this.paginator.length = this.totalElements;
      this.paginator.pageIndex = this.currentPage;
      this.paginator.pageSize = this.pageSize;
    }

    console.log("Filtered data received:", this.dataSource.data);

    this.updateSort();
  }

  private updateSort() {
    this.dataSource.sort = this.sort;
    if (this.currentSort.direction) {
      this.sort.active = this.currentSort.active;
      this.sort.direction = this.currentSort.direction;
      this.sort.sortChange.emit(this.currentSort);
    }
  }

  generatePagination() {
    const startPage = Math.max(0, this.currentPage - 2);
    const endPage = Math.min(this.totalPages - 1, startPage + 4);
    this.pages = Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);
  }

  goToPage(page: number) {
    if (page !== this.currentPage && page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      if (this.paginator) {
        this.paginator.pageIndex = page;
      }
      this.loadPersons();
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.goToPage(this.currentPage - 1);
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.goToPage(this.currentPage + 1);
    }
  }

  goToFirstPage() {
    if (this.currentPage !== 0) {
      this.goToPage(0);
    }
  }

  goToLastPage() {
    if (this.currentPage !== this.totalPages - 1) {
      this.goToPage(this.totalPages - 1);
    }
  }

  onPageSizeChange(newSize: number) {
    this.pageSize = newSize;
    this.resetPagination();
    this.loadPersons();
  }

  protected readonly Math = Math;
}
