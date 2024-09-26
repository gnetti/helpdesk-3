package com.luiz.helpdesk.domain.model;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.AddressDTO;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.PersonDTO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

public class Person {

    private final Integer id;
    private final String name;
    private final String cpf;
    private final String email;
    private final String password;
    private final Set<Profile> profiles;
    private final LocalDate creationDate;
    private final Address address;
    private final String theme;

    private Person(Builder builder) {
        validateFields(builder.name, builder.cpf, builder.email, builder.password);
        this.id = builder.id;
        this.name = builder.name;
        this.cpf = builder.cpf;
        this.email = builder.email;
        this.password = builder.password;
        this.profiles = new HashSet<>(builder.profiles);
        this.creationDate = builder.creationDate != null ? builder.creationDate : LocalDate.now();
        this.address = builder.address;
        this.theme = builder.theme != null ? builder.theme : "indigoPink";
    }

    private void validateFields(String name, String cpf, String email, String password) {
        List<String> invalidFields = new ArrayList<>();

        validateField(name, "Name", invalidFields);
        validateField(cpf, "CPF", invalidFields);
        validateField(email, "Email", invalidFields);
        validateField(password, "Password", invalidFields);

        if (!invalidFields.isEmpty()) {
            throw new IllegalArgumentException(buildErrorMessage(invalidFields));
        }
    }

    private void validateField(String field, String fieldName, List<String> invalidFields) {
        if (field == null || field.trim().isEmpty()) {
            invalidFields.add(fieldName);
        }
    }

    private String buildErrorMessage(List<String> invalidFields) {
        StringBuilder errorMessage = new StringBuilder();
        for (int i = 0; i < invalidFields.size(); i++) {
            if (i > 0) {
                errorMessage.append(i == invalidFields.size() - 1 ? " and " : ", ");
            }
            errorMessage.append(invalidFields.get(i));
        }
        errorMessage.append(invalidFields.size() > 1 ? " are" : " is");
        errorMessage.append(" invalid or empty");
        return errorMessage.toString();
    }

    public Person withEncodedPassword(BCryptPasswordEncoder encoder) {
        return this.withPassword(encoder.encode(this.password));
    }

    public Person updateAddress(Address newAddress) {
        return this.updateWithAddress(newAddress);
    }

    public Person updateFieldsAndAddress(Person updatedPerson, BCryptPasswordEncoder passwordEncoder) {
        Person updatedFields = this.updateFields(updatedPerson, passwordEncoder);
        return updatedFields.updateWithAddress(updatedPerson.getAddress());
    }

    public Person updateWithPersonAndAddress(PersonDTO personDTO, AddressDTO addressDTO) {
        Person updatedPerson = this.updateFields(personDTO.toDomainModel());
        return updatedPerson.updateWithAddress(addressDTO.toDomainModel());
    }

    public Person withAddress(Address address) {
        return toBuilder().withAddress(address).build();
    }

    public Person withPassword(String newPassword) {
        return toBuilder().withPassword(newPassword).build();
    }

    public Person updateFields(Person newData) {
        return toBuilder()
                .withName(newData.getName())
                .withCpf(newData.getCpf())
                .withEmail(newData.getEmail())
                .withPassword(newData.getPassword())
                .withProfiles(newData.getProfiles())
                .withTheme(newData.getTheme())
                .build();
    }

    public Person updateFields(Person updatedPerson, BCryptPasswordEncoder passwordEncoder) {
        return toBuilder()
                .withName(updatedPerson.getName())
                .withCpf(updatedPerson.getCpf())
                .withEmail(updatedPerson.getEmail())
                .withPassword(updatedPerson.getPassword() != null ? passwordEncoder.encode(updatedPerson.getPassword()) : this.getPassword())
                .withProfiles(updatedPerson.getProfiles())
                .withTheme(updatedPerson.getTheme())
                .build();
    }

    public Person addProfile(Profile profile) {
        Set<Profile> newProfiles = new HashSet<>(this.profiles);
        newProfiles.add(profile);
        return toBuilder().withProfiles(newProfiles).build();
    }

    public Person updateWithAddress(Address newAddress) {
        Address updatedAddress = (this.address == null)
                ? Address.createNew(newAddress.getStreet(), newAddress.getComplement(), newAddress.getNeighborhood(),
                newAddress.getCity(), newAddress.getState(), newAddress.getZipCode(),
                newAddress.getNumber(), this.id)
                : Address.reconstitute(this.address.getId(), newAddress.getStreet(), newAddress.getComplement(),
                newAddress.getNeighborhood(), newAddress.getCity(), newAddress.getState(),
                newAddress.getZipCode(), newAddress.getNumber(), this.id);

        return toBuilder().withAddress(updatedAddress).build();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<Profile> getProfiles() {
        return Collections.unmodifiableSet(profiles);
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Address getAddress() {
        return address;
    }

    public String getTheme() {
        return theme;
    }

    public Builder toBuilder() {
        return new Builder()
                .withId(this.id)
                .withName(this.name)
                .withCpf(this.cpf)
                .withEmail(this.email)
                .withPassword(this.password)
                .withProfiles(this.profiles)
                .withCreationDate(this.creationDate)
                .withAddress(this.address)
                .withTheme(this.theme);
    }

    public static class Builder {
        private Integer id;
        private String name;
        private String cpf;
        private String email;
        private String password;
        private Set<Profile> profiles = new HashSet<>();
        private LocalDate creationDate;
        private Address address;
        private String theme;

        public Builder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withCpf(String cpf) {
            this.cpf = cpf;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withProfiles(Set<Profile> profiles) {
            this.profiles = new HashSet<>(profiles);
            return this;
        }

        public Builder withCreationDate(LocalDate creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder withAddress(Address address) {
            this.address = address;
            return this;
        }

        public Builder withTheme(String theme) {
            this.theme = theme;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(name, person.name) &&
                Objects.equals(cpf, person.cpf) &&
                Objects.equals(email, person.email) &&
                Objects.equals(password, person.password) &&
                Objects.equals(profiles, person.profiles) &&
                Objects.equals(creationDate, person.creationDate) &&
                Objects.equals(address, person.address) &&
                Objects.equals(theme, person.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cpf, email, password, profiles, creationDate, address, theme);
    }

    @Override
    public String toString() {
        return new StringBuilder("Person{")
                .append("id=").append(id)
                .append(", name='").append(name).append('\'')
                .append(", cpf='").append(cpf).append('\'')
                .append(", email='").append(email).append('\'')
                .append(", profiles=").append(profiles)
                .append(", creationDate=").append(creationDate)
                .append(", address=").append(address)
                .append(", theme='").append(theme).append('\'')
                .append('}')
                .toString();
    }
}