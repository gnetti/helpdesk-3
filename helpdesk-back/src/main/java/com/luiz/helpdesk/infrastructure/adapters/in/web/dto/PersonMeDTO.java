package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.luiz.helpdesk.application.ports.in.PersonManageUseCasePort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.enums.Theme;
import com.luiz.helpdesk.domain.model.Person;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PersonMeDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String currentPassword;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Integer> profiles = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate creationDate;

    private Theme theme;

    public PersonMeDTO() {
        this.theme = Theme.INDIGO_PINK;
    }

    public PersonMeDTO(Integer id, String name, String email, Set<Integer> profiles, LocalDate creationDate, Theme theme) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profiles = profiles != null ? profiles : new HashSet<>();
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
        this.theme = theme != null ? theme : Theme.INDIGO_PINK;
    }

    public static PersonMeDTO fromDomainModel(Person person) {
        return new PersonMeDTO(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getProfiles().stream().map(Profile::getCode).collect(Collectors.toSet()),
                person.getCreationDate(),
                person.getTheme()
        );
    }

    public Person toUpdateDomainModel(Person existingPerson) {
        return Person.builder()
                .withId(existingPerson.getId())
                .withName(existingPerson.getName())
                .withEmail(existingPerson.getEmail())
                .withPassword(existingPerson.getPassword())
                .withCpf(existingPerson.getCpf())
                .withCreationDate(existingPerson.getCreationDate())
                .withProfiles(existingPerson.getProfiles())
                .withTheme(this.theme != null ? this.theme : existingPerson.getTheme())
                .withAddress(existingPerson.getAddress())
                .build();
    }

    public static PersonMeDTO getCurrentUser(PersonManageUseCasePort personUseCase, String email) {
        Person person = personUseCase.findPersonByEmail(email);
        return PersonMeDTO.fromDomainModel(person);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Set<Integer> getProfiles() {
        return profiles;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonMeDTO)) return false;
        PersonMeDTO that = (PersonMeDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "PersonMeDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profiles=" + profiles +
                ", creationDate=" + creationDate +
                ", theme=" + theme +
                '}';
    }
}