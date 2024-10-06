package com.luiz.helpdesk.infrastructure.adapters.in.web;

import com.luiz.helpdesk.application.ports.in.PaginationUseCasePort;
import com.luiz.helpdesk.application.ports.in.PersonManageUseCasePort;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.in.web.annotation.PaginationParameters;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.PaginationDTO;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.PersonDTO;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.PersonMeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/persons")
@Tag(name = "Person Management", description = "APIs for managing person information")
public class PersonController {

    private final PersonManageUseCasePort personUseCase;
    private final PaginationUseCasePort paginationUseCase;

    public PersonController(PersonManageUseCasePort personUseCase,
                            PaginationUseCasePort paginationUseCase) {
        this.personUseCase = personUseCase;
        this.paginationUseCase = paginationUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a new person with address", description = "Creates a new person record with associated address information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Person created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<PersonDTO> createPerson(@Valid @RequestBody PersonDTO personDTO) {
        Person createdPerson = personUseCase.createPerson(personDTO.toDomainModel());
        URI uri = createResourceUri(createdPerson.getId());
        return ResponseEntity.created(uri).body(PersonDTO.fromDomainModel(createdPerson));
    }

    @GetMapping
    @Operation(summary = "Get all persons with their addresses", description = "Retrieves a paginated list of all persons with their address information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of persons")
    })
    @PaginationParameters
    public ResponseEntity<PaginationDTO<PersonDTO>> getAllPersons(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        PaginationDTO<Void> config = createPaginationConfig(page, size);
        Pagination<Person> personPagination = personUseCase.getAllPersons(config.toDomainPagination());
        return ResponseEntity.ok(createPaginatedResponse(personPagination, config));
    }

    @GetMapping("/pagination-info")
    @Operation(summary = "Get pagination configuration information", description = "Retrieves the default pagination configuration")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved pagination information")
    public ResponseEntity<PaginationDTO<Void>> getPaginationInfo() {
        return ResponseEntity.ok(PaginationDTO.createConfigDTO(paginationUseCase));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a person with address by id", description = "Retrieves a person's information including their address by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved person information"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<PersonDTO> getPersonById(@Parameter(description = "Person ID") @PathVariable Integer id) {
        return personUseCase.getPersonById(id)
                .map(PersonDTO::fromDomainModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a person with address", description = "Updates an existing person's information including their address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<PersonDTO> updatePerson(@Parameter(description = "Person ID") @PathVariable Integer id, @Valid @RequestBody PersonDTO personDTO) {
        Person existingPerson = getExistingPerson(id);
        Person updatedPerson = existingPerson.updateWithPersonAndAddress(personDTO, personDTO.getAddress());
        Person savedPerson = personUseCase.updatePersonWithAddress(id, updatedPerson);
        return ResponseEntity.ok(PersonDTO.fromDomainModel(savedPerson));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a person and their address", description = "Deletes a person's record and associated address information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Person deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<Void> deletePerson(@Parameter(description = "Person ID") @PathVariable Integer id) {
        if (personUseCase.deletePerson(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new PersonNotFoundException("Person not found with id: " + id);
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Get a person with address by CPF", description = "Retrieves a person's information including their address by their CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved person information"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<PersonDTO> getPersonByCpf(@Parameter(description = "Person CPF") @PathVariable String cpf) {
        return personUseCase.findPersonByCpf(cpf)
                .map(PersonDTO::fromDomainModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with CPF: " + cpf));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get a person with address by email", description = "Retrieves a person's information including their address by their email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved person information"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<PersonDTO> getPersonByEmail(@Parameter(description = "Person email") @PathVariable String email) {
        Person person = personUseCase.findPersonByEmail(email);
        return ResponseEntity.ok(PersonDTO.fromDomainModel(person));
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if a person exists", description = "Checks if a person with the given ID exists in the system")
    @ApiResponse(responseCode = "200", description = "Successfully checked person existence")
    public ResponseEntity<Boolean> existsPersonById(@Parameter(description = "Person ID") @PathVariable Integer id) {
        return ResponseEntity.ok(personUseCase.existsPersonById(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's information", description = "Retrieves the information of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved current user information")
    public ResponseEntity<PersonMeDTO> getCurrentUser() {
        Person currentUser = personUseCase.getCurrentUser();
        return ResponseEntity.ok(PersonMeDTO.fromDomainModel(currentUser));
    }

    @PutMapping("/me")
    @Transactional
    @Operation(summary = "Update current user's theme and password", description = "Updates the theme and password of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
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