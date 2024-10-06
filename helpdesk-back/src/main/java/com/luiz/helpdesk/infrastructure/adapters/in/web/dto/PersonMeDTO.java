package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.luiz.helpdesk.domain.enums.Theme;
import com.luiz.helpdesk.domain.model.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Schema(description = "Data Transfer Object for Person's own information",
        example = "{"
                + "\"id\":1,"
                + "\"name\":\"John Doe\","
                + "\"email\":\"john.doe@example.com\","
                + "\"currentPassword\":\"currentPassword123\","
                + "\"newPassword\":\"newPassword123\","
                + "\"profile\":1,"
                + "\"creationDate\":\"2023-05-20\","
                + "\"theme\":3"
                + "}")
public class PersonMeDTO {

    @Schema(description = "Unique identifier of the person", example = "1")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @Schema(description = "Name of the person", example = "John Doe")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String name;

    @Schema(description = "Email address of the person", example = "john.doe@example.com")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;

    @Schema(description = "Current password of the person", example = "currentPassword123")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String currentPassword;

    @Schema(description = "New password of the person", example = "newPassword123")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;

    @Schema(description = "Profile code associated with the person", example = "[1]")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Integer> profiles = new HashSet<>();

    @Schema(description = "Creation date of the person record", example = "2023-05-20")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate creationDate;

    @Schema(description = "Theme code associated with the person", example = "[3]")
    private Set<Integer> themes = new HashSet<>();


    public PersonMeDTO() {
        this.themes.add(Theme.INDIGO_PINK.getCode());
    }

    public PersonMeDTO(Integer id, String name, String email, Integer profile, LocalDate creationDate, Integer theme) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profiles = new HashSet<>();
        if (profile != null) {
            this.profiles.add(profile);
        }
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
        this.themes = new HashSet<>();
        if (theme != null) {
            this.themes.add(theme);
        }
        if (this.themes.isEmpty()) {
            this.themes.add(Theme.INDIGO_PINK.getCode());
        }
    }

    @Schema(description = "Create DTO from domain model")
    public static PersonMeDTO fromDomainModel(Person person) {
        return new PersonMeDTO(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getProfile(),
                person.getCreationDate(),
                person.getTheme()
        );
    }

    @Schema(description = "Convert DTO to domain model for update")
    public Person toUpdateDomainModel(Person existingPerson) {
        return Person.builder()
                .withId(existingPerson.getId())
                .withName(existingPerson.getName())
                .withEmail(existingPerson.getEmail())
                .withPassword(existingPerson.getPassword())
                .withCpf(existingPerson.getCpf())
                .withCreationDate(existingPerson.getCreationDate())
                .withProfile(this.getProfile() != null ? this.getProfile() : existingPerson.getProfile())
                .withTheme(this.getTheme() != null ? this.getTheme() : existingPerson.getTheme())
                .withAddress(existingPerson.getAddress())
                .build();
    }

    @Schema(description = "Get the person's ID")
    public Integer getId() {
        return id;
    }

    @Schema(description = "Get the person's name")
    public String getName() {
        return name;
    }

    @Schema(description = "Get the person's email")
    public String getEmail() {
        return email;
    }

    @Schema(description = "Get the person's current password")
    public String getCurrentPassword() {
        return currentPassword;
    }

    @Schema(description = "Set the person's current password")
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    @Schema(description = "Get the person's new password")
    public String getNewPassword() {
        return newPassword;
    }

    @Schema(description = "Set the person's new password")
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Schema(description = "Get the person's profile code")
    public Integer getProfile() {
        return profiles.isEmpty() ? null : profiles.iterator().next();
    }

    @Schema(description = "Set the person's profile code")
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

    @Schema(description = "Get the person's theme code")
    public Integer getTheme() {
        return themes.isEmpty() ? null : themes.iterator().next();
    }

    @Schema(description = "Set the person's theme code")
    public void setTheme(Integer theme) {
        this.themes.clear();
        if (theme != null) {
            this.themes.add(theme);
        }
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
                ", profile=" + getProfile() +
                ", creationDate=" + creationDate +
                ", theme=" + getTheme() +
                '}';
    }
}