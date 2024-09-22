package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import com.luiz.helpdesk.domain.model.Person;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AuthenticationDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String token;
    private UserDTO user;

    public AuthenticationDTO() {
    }

    public AuthenticationDTO(String email, String password, String token, UserDTO user) {
        this.email = email;
        this.password = password;
        this.token = token;
        this.user = user;
    }

    public static AuthenticationDTO fromDomainModel(String token, Person person) {
        return new AuthenticationDTO(null, null, token, UserDTO.fromDomainModel(person));
    }

    public record UserDTO(Integer id, String email, List<String> profiles) {

        public static UserDTO fromDomainModel(Person person) {
            return new UserDTO(
                    person.getId(),
                    person.getEmail(),
                    person.getProfiles().stream().map(Enum::name).collect(Collectors.toList())
            );
        }
    }

    public static class Builder {
        private String email;
        private String password;
        private String token;
        private UserDTO user;

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withToken(String token) {
            this.token = token;
            return this;
        }

        public Builder withUser(UserDTO user) {
            this.user = user;
            return this;
        }

        public AuthenticationDTO build() {
            return new AuthenticationDTO(email, password, token, user);
        }
    }

    public static Builder builder() {
        return new Builder();
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticationDTO that = (AuthenticationDTO) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(password, that.password) &&
                Objects.equals(token, that.token) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, token, user);
    }

    @Override
    public String toString() {
        return "AuthenticationDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", user=" + user +
                '}';
    }
}