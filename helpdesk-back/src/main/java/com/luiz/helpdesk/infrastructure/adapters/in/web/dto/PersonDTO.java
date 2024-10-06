package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.enums.Theme;
import com.luiz.helpdesk.domain.model.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Schema(description = "Data Transfer Object for Person information",
        example = "{"
                + "\"id\":1,"
                + "\"name\":\"John Doe\","
                + "\"cpf\":\"123.456.789-00\","
                + "\"email\":\"john.doe@example.com\","
                + "\"password\":\"password123\","
                + "\"profile\":1,"
                + "\"creationDate\":\"2023-05-20\","
                + "\"theme\":1,"
                + "\"address\":{...}"
                + "}")
public class PersonDTO {

    @Schema(description = "Unique identifier of the person", example = "1")
    private Integer id;

    @Schema(description = "Name of the person", example = "John Doe")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "CPF (Brazilian individual taxpayer registry identification) of the person", example = "123.456.789-00")
    @NotBlank(message = "CPF is required")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "Invalid CPF format")
    private String cpf;

    @Schema(description = "Email address of the person", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Password of the person", example = "password123")
    @NotBlank(message = "Password is required")
    private String password;

    @Schema(description = "Profile code of the person", example = "1")
    @NotNull(message = "Profile is required")
    private Set<Integer> profiles = new HashSet<>();

    @Schema(description = "Creation date of the person record", example = "2023-05-20")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate creationDate;

    @Schema(description = "Address of the person")
    private AddressDTO address;

    @Schema(description = "Theme code of the person", example = "1")
    private Set<Integer> themes = new HashSet<>();

       public PersonDTO() {
    }

    public PersonDTO(Integer id, String name, String cpf, String email, String password, Integer profile, LocalDate creationDate, Integer theme) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.profiles.add(profile != null ? profile : Profile.CLIENT.getCode());
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
        this.themes.add(theme != null ? theme : Theme.INDIGO_PINK.getCode());
    }

    @Schema(description = "Convert DTO to domain model")
    public Person toDomainModel() {
        Person.Builder builder = Person.builder()
                .withId(id)
                .withName(name)
                .withCpf(cpf)
                .withEmail(email)
                .withPassword(password)
                .withCreationDate(creationDate)
                .withProfile(getProfile())
                .withTheme(getTheme());

        if (address != null) {
            builder.withAddress(address.toDomainModel());
        }

        return builder.build();
    }

    @Schema(description = "Create DTO from domain model")
    public static PersonDTO fromDomainModel(Person person) {
        PersonDTO dto = new PersonDTO(
                person.getId(),
                person.getName(),
                person.getCpf(),
                person.getEmail(),
                person.getPassword(),
                person.getProfile(),
                person.getCreationDate(),
                person.getTheme()
        );
        if (person.getAddress() != null) {
            dto.setAddress(AddressDTO.fromDomainModel(person.getAddress()));
        }
        return dto;
    }

    @Schema(description = "Get the person's ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Schema(description = "Get the person's name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Schema(description = "Get the person's CPF")
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Schema(description = "Get the person's email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Schema(description = "Get the person's password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Schema(description = "Get the person's profile code")
    public Integer getProfile() {
        return profiles.isEmpty() ? null : profiles.iterator().next();
    }

    public void setProfile(Integer profile) {
        this.profiles.clear();
        if (profile != null) {
            this.profiles.add(profile);
        }
    }

    @Schema(description = "Get the person's creation date")
    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    @Schema(description = "Get the person's address")
    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    @Schema(description = "Get the person's theme code")
    public Integer getTheme() {
        return themes.isEmpty() ? null : themes.iterator().next();
    }

    public void setTheme(Integer theme) {
        this.themes.clear();
        if (theme != null) {
            this.themes.add(theme);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return Objects.equals(id, personDTO.id) &&
                Objects.equals(name, personDTO.name) &&
                Objects.equals(cpf, personDTO.cpf) &&
                Objects.equals(email, personDTO.email) &&
                Objects.equals(password, personDTO.password) &&
                Objects.equals(profiles, personDTO.profiles) &&
                Objects.equals(creationDate, personDTO.creationDate) &&
                Objects.equals(address, personDTO.address) &&
                Objects.equals(themes, personDTO.themes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cpf, email, password, profiles, creationDate, address, themes);
    }

    @Override
    public String toString() {
        return "PersonDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", profile=" + getProfile() +
                ", creationDate=" + creationDate +
                ", address=" + address +
                ", theme=" + getTheme() +
                '}';
    }
}