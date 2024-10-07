package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.luiz.helpdesk.domain.model.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Schema(description = "Data Transfer Object for Person's own information",
        example = "{"
                + "\"id\":1,"
                + "\"name\":\"admin\","
                + "\"email\":\"admin@email.com\","
                + "\"profile\":0,"
                + "\"theme\":5"
                + "}")
public class PersonMeDTO {

    @Schema(description = "Unique identifier of the person", example = "1")
    private Integer id;

    @Schema(description = "Name of the person", example = "admin")
    private String name;

    @Schema(description = "Email address of the person", example = "admin@email.com")
    private String email;

    @Schema(description = "Current password of the person")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String currentPassword;

    @Schema(description = "New password of the person")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;

    @Schema(description = "Profile code associated with the person", example = "0")
    private Integer profile;

    @Schema(description = "Theme code associated with the person", example = "5")
    private Integer theme;

    public PersonMeDTO() {
    }

    public PersonMeDTO(Integer id, String name, String email, Integer profile, Integer theme) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.theme = theme;
    }

    @Schema(description = "Create DTO from domain model")
    public static PersonMeDTO fromDomainModel(Person person) {
        return new PersonMeDTO(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getProfile(),
                person.getTheme()
        );
    }

    @Schema(description = "Convert DTO to domain model for update")
    public Person toUpdateDomainModel(Person existingPerson) {
        return Person.builder()
                .withId(existingPerson.getId())
                .withName(existingPerson.getName())
                .withEmail(existingPerson.getEmail())
                .withPassword(this.newPassword != null ? this.newPassword : existingPerson.getPassword())
                .withCpf(existingPerson.getCpf())
                .withCreationDate(existingPerson.getCreationDate())
                .withProfile(existingPerson.getProfile())
                .withTheme(this.theme != null ? this.theme : existingPerson.getTheme())
                .withAddress(existingPerson.getAddress())
                .build();
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

    public Integer getProfile() {
        return profile;
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
                ", profile=" + profile +
                ", theme=" + theme +
                '}';
    }
}