package com.luiz.helpdesk.infrastructure.adapters.in.web;

import com.luiz.helpdesk.application.ports.in.PaginationUseCasePort;
import com.luiz.helpdesk.application.ports.in.PersonManageUseCasePort;
import com.luiz.helpdesk.application.ports.in.VerifyLoggedUserUseCase;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.PaginationDTO;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.PersonDTO;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.PersonMeDTO;
import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonManageUseCasePort personUseCase;
    private final PaginationUseCasePort paginationUseCase;
    private final VerifyLoggedUserUseCase verifyLoggedUserUseCase;

    public PersonController(PersonManageUseCasePort personUseCase,
                            PaginationUseCasePort paginationUseCase,
                            VerifyLoggedUserUseCase verifyLoggedUserUseCase) {
        this.personUseCase = personUseCase;
        this.paginationUseCase = paginationUseCase;
        this.verifyLoggedUserUseCase = verifyLoggedUserUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a new person with address")
    public ResponseEntity<PersonDTO> createPerson(@Valid @RequestBody PersonDTO personDTO) {
        verifyLoggedUserUseCase.getAuthenticatedUser();
        Person createdPerson = personUseCase.createPerson(personDTO.toDomainModel());
        URI uri = createResourceUri(createdPerson.getId());
        return ResponseEntity.created(uri).body(PersonDTO.fromDomainModel(createdPerson));
    }

    @GetMapping
    @Operation(summary = "Get all persons with their addresses")
    public ResponseEntity<PaginationDTO<PersonDTO>> getAllPersons(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        verifyLoggedUserUseCase.getAuthenticatedUser();
        PaginationDTO<Void> config = createPaginationConfig(page, size);
        Pagination<Person> personPagination = personUseCase.getAllPersons(config.toDomainPagination());
        return ResponseEntity.ok(createPaginatedResponse(personPagination, config));
    }

    @GetMapping("/pagination-info")
    @Operation(summary = "Get pagination configuration information")
    public ResponseEntity<PaginationDTO<Void>> getPaginationInfo() {
        verifyLoggedUserUseCase.getAuthenticatedUser();
        return ResponseEntity.ok(PaginationDTO.createConfigDTO(paginationUseCase));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a person with address by id")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Integer id) {
        verifyLoggedUserUseCase.getAuthenticatedUser();
        return personUseCase.getPersonById(id)
                .map(PersonDTO::fromDomainModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a person with address")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable Integer id, @Valid @RequestBody PersonDTO personDTO) {
        verifyLoggedUserUseCase.getAuthenticatedUser();
        Person existingPerson = getExistingPerson(id);
        Person updatedPerson = existingPerson.updateWithPersonAndAddress(personDTO, personDTO.getAddress());
        Person savedPerson = personUseCase.updatePersonWithAddress(id, updatedPerson);
        return ResponseEntity.ok(PersonDTO.fromDomainModel(savedPerson));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a person and their address")
    public ResponseEntity<Void> deletePerson(@PathVariable Integer id) {
        verifyLoggedUserUseCase.getAuthenticatedUser();
        if (personUseCase.deletePerson(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new PersonNotFoundException("Person not found with id: " + id);
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Get a person with address by CPF")
    public ResponseEntity<PersonDTO> getPersonByCpf(@PathVariable String cpf) {
        verifyLoggedUserUseCase.getAuthenticatedUser();
        return personUseCase.findPersonByCpf(cpf)
                .map(PersonDTO::fromDomainModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with CPF: " + cpf));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get a person with address by email")
    public ResponseEntity<PersonDTO> getPersonByEmail(@PathVariable String email) {
        verifyLoggedUserUseCase.getAuthenticatedUser();
        Person person = personUseCase.findPersonByEmail(email);
        return ResponseEntity.ok(PersonDTO.fromDomainModel(person));
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if a person exists")
    public ResponseEntity<Boolean> existsPersonById(@PathVariable Integer id) {
        verifyLoggedUserUseCase.getAuthenticatedUser();
        return ResponseEntity.ok(personUseCase.existsPersonById(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's information")
    public ResponseEntity<PersonMeDTO> getCurrentUser() {
        CustomUserDetails userDetails = verifyLoggedUserUseCase.getAuthenticatedUser();
        Person currentUser = personUseCase.findPersonByEmail(userDetails.getUsername());
        return ResponseEntity.ok(PersonMeDTO.fromDomainModel(currentUser));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user's theme and password")
    public ResponseEntity<PersonMeDTO> updateCurrentUser(@Valid @RequestBody PersonMeDTO personMeDTO) throws Exception {
        CustomUserDetails userDetails = verifyLoggedUserUseCase.getAuthenticatedUser();
        Person existingPerson = personUseCase.findPersonByEmail(userDetails.getUsername());
        Person updatedPerson = updateCurrentUserPerson(existingPerson, personMeDTO);
        return ResponseEntity.ok(PersonMeDTO.fromDomainModel(updatedPerson));
    }

    @PostMapping("/verify-password")
    @Operation(summary = "Verify user's password")
    public ResponseEntity<Boolean> verifyPassword(@RequestBody String password) {
        CustomUserDetails userDetails = verifyLoggedUserUseCase.getAuthenticatedUser();
        boolean isValid = personUseCase.verifyPassword(userDetails.getUsername(), password);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh JWT token")
    public ResponseEntity<String> refreshToken(@RequestHeader("Authorization") String token) {
        String newToken = verifyLoggedUserUseCase.refreshToken(token.replace("Bearer ", ""));
        return ResponseEntity.ok(newToken);
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

    private Person updateCurrentUserPerson(Person existingPerson, PersonMeDTO personMeDTO) throws Exception {
        Person updatedPerson = personMeDTO.toUpdateDomainModel(existingPerson);
        return personUseCase.updateCurrentUser(
                existingPerson.getEmail(),
                updatedPerson,
                personMeDTO.getCurrentPassword(),
                personMeDTO.getNewPassword()
        );
    }
}