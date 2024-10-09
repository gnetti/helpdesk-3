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
import java.util.Objects;

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
    private Integer profile;

    @Schema(description = "Creation date of the person record", example = "2023-05-20")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate creationDate;

    @Schema(description = "Address of the person")
    private AddressDTO address;

    @Schema(description = "Theme code of the person", example = "1")
    private Integer theme;

    public PersonDTO() {
    }

    public PersonDTO(Integer id, String name, String cpf, String email, String password, Integer profile, LocalDate creationDate, Integer theme) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.profile = profile != null ? profile : Profile.CLIENT.getCode();
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
        this.theme = theme != null ? theme : Theme.INDIGO_PINK.getCode();
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
                .withProfile(profile)
                .withTheme(theme);

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getProfile() {
        return profile;
    }

    public void setProfile(Integer profile) {
        this.profile = profile;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public Integer getTheme() {
        return theme;
    }

    public void setTheme(Integer theme) {
        this.theme = theme;
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
                Objects.equals(profile, personDTO.profile) &&
                Objects.equals(creationDate, personDTO.creationDate) &&
                Objects.equals(address, personDTO.address) &&
                Objects.equals(theme, personDTO.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cpf, email, password, profile, creationDate, address, theme);
    }

    @Override
    public String toString() {
        return "PersonDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", profile=" + profile +
                ", creationDate=" + creationDate +
                ", address=" + address +
                ", theme=" + theme +
                '}';
    }
}