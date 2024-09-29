package com.luiz.helpdesk.infrastructure.adapters.in.web;

import com.luiz.helpdesk.application.ports.in.PaginationUseCasePort;
import com.luiz.helpdesk.application.ports.in.PersonManageUseCasePort;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.PaginationDTO;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.PersonDTO;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.PersonMeDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonManageUseCasePort personUseCase;
    private final PaginationUseCasePort paginationUseCase;


    public PersonController(PersonManageUseCasePort personUseCase,
                            PaginationUseCasePort paginationUseCase) {
        this.personUseCase = personUseCase;
        this.paginationUseCase = paginationUseCase;

    }

    @PostMapping
    @Operation(summary = "Create a new person with address")
    public ResponseEntity<PersonDTO> createPerson(@Valid @RequestBody PersonDTO personDTO) {
        Person createdPerson = personUseCase.createPerson(personDTO.toDomainModel());
        URI uri = createResourceUri(createdPerson.getId());
        return ResponseEntity.created(uri).body(PersonDTO.fromDomainModel(createdPerson));
    }

    @GetMapping
    @Operation(summary = "Get all persons with their addresses")
    public ResponseEntity<PaginationDTO<PersonDTO>> getAllPersons(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        PaginationDTO<Void> config = createPaginationConfig(page, size);
        Pagination<Person> personPagination = personUseCase.getAllPersons(config.toDomainPagination());
        return ResponseEntity.ok(createPaginatedResponse(personPagination, config));
    }

    @GetMapping("/pagination-info")
    @Operation(summary = "Get pagination configuration information")
    public ResponseEntity<PaginationDTO<Void>> getPaginationInfo() {
        return ResponseEntity.ok(PaginationDTO.createConfigDTO(paginationUseCase));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a person with address by id")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Integer id) {
        return personUseCase.getPersonById(id)
                .map(PersonDTO::fromDomainModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a person with address")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable Integer id, @Valid @RequestBody PersonDTO personDTO) {
        Person existingPerson = getExistingPerson(id);
        Person updatedPerson = existingPerson.updateWithPersonAndAddress(personDTO, personDTO.getAddress());
        Person savedPerson = personUseCase.updatePersonWithAddress(id, updatedPerson);
        return ResponseEntity.ok(PersonDTO.fromDomainModel(savedPerson));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a person and their address")
    public ResponseEntity<Void> deletePerson(@PathVariable Integer id) {
        if (personUseCase.deletePerson(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new PersonNotFoundException("Person not found with id: " + id);
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Get a person with address by CPF")
    public ResponseEntity<PersonDTO> getPersonByCpf(@PathVariable String cpf) {
        return personUseCase.findPersonByCpf(cpf)
                .map(PersonDTO::fromDomainModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with CPF: " + cpf));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get a person with address by email")
    public ResponseEntity<PersonDTO> getPersonByEmail(@PathVariable String email) {
        Person person = personUseCase.findPersonByEmail(email);
        return ResponseEntity.ok(PersonDTO.fromDomainModel(person));
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if a person exists")
    public ResponseEntity<Boolean> existsPersonById(@PathVariable Integer id) {
        return ResponseEntity.ok(personUseCase.existsPersonById(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's information")
    public ResponseEntity<PersonMeDTO> getCurrentUser() {
        Person currentUser = personUseCase.getCurrentUser();
        return ResponseEntity.ok(PersonMeDTO.fromDomainModel(currentUser));
    }

    @PutMapping("/me")
    @Transactional
    @Operation(summary = "Update current user's theme and password")
    public ResponseEntity<PersonMeDTO> updateCurrentUser(@Valid @RequestBody PersonMeDTO personMeDTO) {
        Person existingPerson = personUseCase.getCurrentUser();
        Person updatedPerson = updateCurrentUserPerson(existingPerson, personMeDTO);
        return ResponseEntity.ok(PersonMeDTO.fromDomainModel(updatedPerson));
    }

    private URI createResourceUri(Integer id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    private PaginationDTO<Void> createPaginationConfig(Integer page, Integer size) {
        PaginationDTO<Void> config = PaginationDTO.createConfigDTO(paginationUseCase);
        config.validateAndSetPageNumber(page);
        config.validateAndSetPageSize(size);
        return config;
    }

    private PaginationDTO<PersonDTO> createPaginatedResponse(Pagination<Person> personPagination, PaginationDTO<Void> config) {
        PaginationDTO<PersonDTO> result = PaginationDTO.fromDomainPagination(personPagination)
                .map(PersonDTO::fromDomainModel);
        config.copyConfigTo(result);
        return result;
    }

    private Person getExistingPerson(Integer id) {
        return personUseCase.getPersonById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
    }


    private Person updateCurrentUserPerson(Person existingPerson, PersonMeDTO personMeDTO) {
        Person updatedPerson = personMeDTO.toUpdateDomainModel(existingPerson);
        return personUseCase.updateCurrentUser(
                existingPerson.getId(),
                updatedPerson,
                personMeDTO.getCurrentPassword(),
                personMeDTO.getNewPassword()
        );
    }
}