package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.enums.Theme;
import com.luiz.helpdesk.domain.model.Person;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PersonDTO {

    private Integer id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "CPF is required")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "Invalid CPF format")
    private String cpf;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Profiles are required")
    private Set<Integer> profiles = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate creationDate;

    private AddressDTO address;

    private String theme;

    public PersonDTO() {
    }

    public PersonDTO(Integer id, String name, String cpf, String email, String password, Set<Integer> profiles, LocalDate creationDate, String theme) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.profiles = profiles != null ? profiles : new HashSet<>();
        this.creationDate = creationDate;
        this.theme = theme;
        if (this.profiles.isEmpty()) {
            this.profiles.add(Profile.CLIENT.getCode());
        }
    }

    public Person toDomainModel() {
        Set<Profile> profileSet = profiles.stream().map(this::getProfileFromCode).collect(Collectors.toSet());

        Person.Builder builder = Person.builder()
                .withId(id)
                .withName(name)
                .withCpf(cpf)
                .withEmail(email)
                .withPassword(password)
                .withCreationDate(creationDate)
                .withProfiles(profileSet);

        if (theme != null) {
            builder.withTheme(Theme.fromString(theme));
        }

        if (address != null) {
            builder.withAddress(address.toDomainModel());
        }

        return builder.build();
    }

    private Profile getProfileFromCode(Integer code) {
        for (Profile profile : Profile.values()) {
            if (profile.getCode().equals(code)) {
                return profile;
            }
        }
        throw new IllegalArgumentException("Invalid profile code: " + code);
    }

    public static PersonDTO fromDomainModel(Person person) {
        PersonDTO dto = new PersonDTO(
                person.getId(),
                person.getName(),
                person.getCpf(),
                person.getEmail(),
                person.getPassword(),
                person.getProfiles().stream().map(Profile::getCode).collect(Collectors.toSet()),
                person.getCreationDate(),
                person.getTheme().getValue()
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

    public Set<Integer> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<Integer> profiles) {
        this.profiles = profiles;
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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
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
                Objects.equals(profiles, personDTO.profiles) &&
                Objects.equals(creationDate, personDTO.creationDate) &&
                Objects.equals(address, personDTO.address) &&
                Objects.equals(theme, personDTO.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cpf, email, password, profiles, creationDate, address, theme);
    }

    @Override
    public String toString() {
        return "PersonDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", profiles=" + profiles +
                ", creationDate=" + creationDate +
                ", address=" + address +
                ", theme='" + theme + '\'' +
                '}';
    }
}