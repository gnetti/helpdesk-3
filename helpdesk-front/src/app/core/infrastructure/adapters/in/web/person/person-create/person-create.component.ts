import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {debounceTime, takeUntil} from 'rxjs/operators';
import {Subject} from 'rxjs';
import {CoolDialogService} from '@angular-cool/dialogs';
import {PERSON_USE_CASE_PORT, PersonUseCasePort} from "@domain/ports/in/person-use-case.port";
import {PasswordValidationService} from "@application/services/password-validation.service";
import {Profile} from "@enums//profile.enum";
import {GetAddressByCepUseCase} from "@domain/ports/in/get-address-by-cep.use-case";
import {CpfValidator} from "@validators//cpf.validator";
import {PasswordValidator} from "@validators//password.validator";
import {FormUtils} from "@utils//form.util";
import { CreatePersonDTO } from "@dto//create-person.dto";


@Component({
  selector: 'app-person-create',
  templateUrl: './person-create.component.html',
  styleUrls: ['./person-create.component.css']
})
export class PersonCreateComponent implements OnInit, OnDestroy {
  personForm!: FormGroup;
  private cepSubject = new Subject<string>();
  private destroy$ = new Subject<void>();
  isLoading = false;
  Profile = Profile;
  passwordErrors: string[] = [];
  hidePassword = true;

  constructor(
    private formBuilder: FormBuilder,
    @Inject(PERSON_USE_CASE_PORT) private personService: PersonUseCasePort,
    private toast: ToastrService,
    private router: Router,
    private coolDialogService: CoolDialogService,
    private passwordValidationService: PasswordValidationService,
    private getAddressByCepUseCase: GetAddressByCepUseCase
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.setupFormListeners();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.personForm = this.formBuilder.group({
      name: [null, [Validators.required, Validators.maxLength(50)]],
      cpf: [null, [Validators.required, CpfValidator.validateCpf]],
      email: [null, [Validators.required, Validators.email]],
      password: [null, [Validators.required, Validators.minLength(10), Validators.maxLength(15), PasswordValidator.validate()]],
      zipCode: [null, [Validators.required, Validators.pattern('^\\d{5}-\\d{3}$|^\\d{8}$')]],
      street: [{value: null, disabled: true}, Validators.required],
      number: [{value: null, disabled: true}, Validators.required],
      neighborhood: [{value: null, disabled: true}, Validators.required],
      complement: [{value: null, disabled: true}],
      state: [{value: null, disabled: true}, Validators.required],
      city: [{value: null, disabled: true}, Validators.required],
      profiles: [[]]
    });
  }

  private setupFormListeners(): void {
    this.personForm.get('zipCode')?.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => this.onCepChange());

    this.cepSubject.pipe(
      debounceTime(300),
      takeUntil(this.destroy$)
    ).subscribe(cep => this.validateCep(cep));

    this.personForm.get('password')?.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => this.updatePasswordErrors());
  }

  updatePasswordErrors(): void {
    const passwordControl = this.personForm.get('password');
    if (passwordControl) {
      this.passwordErrors = this.passwordValidationService.validatePassword(passwordControl);
    }
  }

  onCepChange(): void {
    const zipCodeControl = this.personForm.get('zipCode');
    if (zipCodeControl?.value) {
      const cepSemHifen = zipCodeControl.value.replace(/\D/g, '');
      if (FormUtils.validateCep(cepSemHifen)) {
        this.cepSubject.next(cepSemHifen);
      }
    }
  }

  validateCep(cep: string): void {
    if (FormUtils.validateCep(cep)) {
      this.searchCep(cep);
    }
  }

  searchCep(cep: string): void {
    this.isLoading = true;
    this.getAddressByCepUseCase.execute(cep).subscribe({
      next: (address) => {
        if (address) {
          this.personForm.patchValue({
            street: address.street,
            neighborhood: address.neighborhood,
            complement: address.complement,
            state: address.state,
            city: address.city
          });
          this.updateAddressFields();
        } else {
          this.toast.error('CEP não encontrado.');
        }
        this.isLoading = false;
      },
      error: () => {
        this.toast.error('Erro ao buscar CEP.');
        this.isLoading = false;
      }
    });
  }

  updateAddressFields(): void {
    ['street', 'number', 'neighborhood', 'complement', 'state', 'city'].forEach(field => {
      const control = this.personForm.get(field);
      if (control) {
        control.value ? control.disable() : control.enable();
      }
    });
  }

  addProfile(profile: Profile, event: MatCheckboxChange): void {
    const profiles = this.personForm.get('profiles')?.value || [];
    if (event.checked) {
      if (!profiles.includes(profile)) profiles.push(profile);
    } else {
      const index = profiles.indexOf(profile);
      if (index > -1) profiles.splice(index, 1);
    }
    this.personForm.patchValue({profiles});
  }

  isProfileChecked(profile: Profile): boolean {
    return (this.personForm.get('profiles')?.value || []).includes(profile);
  }

  validFields(): boolean {
    return this.personForm.valid;
  }

  togglePasswordVisibility(event: MouseEvent): void {
    event.preventDefault();
    this.hidePassword = !this.hidePassword;
  }

  async create(): Promise<void> {
    if (!this.validFields()) {
      this.toast.warning('Por favor, preencha todos os campos obrigatórios corretamente.');
      return;
    }

    const confirmResult = await this.coolDialogService.showDialog({
      titleText: 'Confirmação',
      questionText: 'Você deseja realmente criar esta pessoa?',
      confirmActionButtonText: 'Sim',
      cancelActionButtonText: 'Cancelar',
      confirmActionButtonColor: 'primary'
    });

    if (!confirmResult.isConfirmed) return;

    this.isLoading = true;
    this.enableAllFormFields();
    const person = this.createPersonDTO();

    this.personService.createPerson(person).subscribe({
      next: () => {
        this.isLoading = false;
        this.toast.success('Pessoa cadastrada com sucesso.');
        this.router.navigate(['persons']);
      },
      error: (error) => {
        this.isLoading = false;
        const errorMessage = error.error?.message || 'Erro ao cadastrar pessoa.';
        this.toast.error(errorMessage, 'Erro');
      }
    });

    this.disableAddressFields();
  }

  private enableAllFormFields(): void {
    Object.keys(this.personForm.controls).forEach(key => {
      this.personForm.get(key)?.enable();
    });
  }

  private createPersonDTO(): CreatePersonDTO {
    const formValues = this.personForm.value;
    return {
      name: formValues.name,
      cpf: FormUtils.formatCpf(formValues.cpf),
      email: formValues.email,
      password: formValues.password,
      profiles: formValues.profiles.map((profile: Profile) => profile as number),
      address: {
        zipCode: FormUtils.formatCep(formValues.zipCode),
        street: formValues.street,
        number: formValues.number,
        neighborhood: formValues.neighborhood,
        complement: formValues.complement,
        state: formValues.state,
        city: formValues.city
      }
    };
  }

  private disableAddressFields(): void {
    ['street', 'number', 'neighborhood', 'complement', 'state', 'city'].forEach(field => {
      this.personForm.get(field)?.disable();
    });
  }
}
