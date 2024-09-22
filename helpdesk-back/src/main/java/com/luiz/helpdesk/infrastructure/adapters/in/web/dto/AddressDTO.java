package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import com.luiz.helpdesk.domain.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

public class AddressDTO {

    private Integer id;

    @NotBlank(message = "Street is required")
    @Pattern(regexp = "\\S+.*\\S+", message = "Street cannot be blank or only whitespace")
    private String street;

    private String complement;

    @NotBlank(message = "Neighborhood is required")
    @Pattern(regexp = "\\S+.*\\S+", message = "Neighborhood cannot be blank or only whitespace")
    private String neighborhood;

    @NotBlank(message = "City is required")
    @Pattern(regexp = "\\S+.*\\S+", message = "City cannot be blank or only whitespace")
    private String city;

    @NotBlank(message = "State is required")
    @Pattern(regexp = "[A-Z]{2}", message = "State must be a 2-letter code")
    private String state;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "Invalid zip code format")
    private String zipCode;

    @NotBlank(message = "Number is required")
    private String number;

    @NotNull(message = "Person ID is required")
    private Integer personId;

    public AddressDTO() {
    }

    public AddressDTO(Integer id, String street, String complement, String neighborhood, String city, String state, String zipCode, String number, Integer personId) {
        this.id = id;
        this.street = street;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.number = number;
        this.personId = personId;
    }

    public Address toDomainModel() {
        return Address.createNew(street, complement, neighborhood, city, state, zipCode, number, personId);
    }

    public static AddressDTO fromDomainModel(Address address) {
        return new AddressDTO(address.getId(), address.getStreet(), address.getComplement(), address.getNeighborhood(), address.getCity(), address.getState(), address.getZipCode(), address.getNumber(), address.getPersonId());
    }

    public static class Builder {
        private Integer id;
        private String street;
        private String complement;
        private String neighborhood;
        private String city;
        private String state;
        private String zipCode;
        private String number;
        private Integer personId;

        public Builder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder withStreet(String street) {
            this.street = street;
            return this;
        }

        public Builder withComplement(String complement) {
            this.complement = complement;
            return this;
        }

        public Builder withNeighborhood(String neighborhood) {
            this.neighborhood = neighborhood;
            return this;
        }

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withState(String state) {
            this.state = state;
            return this;
        }

        public Builder withZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Builder withNumber(String number) {
            this.number = number;
            return this;
        }

        public Builder withPersonId(Integer personId) {
            this.personId = personId;
            return this;
        }

        public AddressDTO build() {
            return new AddressDTO(id, street, complement, neighborhood, city, state, zipCode, number, personId);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressDTO that = (AddressDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(street, that.street) && Objects.equals(complement, that.complement) && Objects.equals(neighborhood, that.neighborhood) && Objects.equals(city, that.city) && Objects.equals(state, that.state) && Objects.equals(zipCode, that.zipCode) && Objects.equals(number, that.number) && Objects.equals(personId, that.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street, complement, neighborhood, city, state, zipCode, number, personId);
    }

    @Override
    public String toString() {
        return "AddressDTO{" + "id=" + id + ", street='" + street + '\'' + ", complement='" + complement + '\'' + ", neighborhood='" + neighborhood + '\'' + ", city='" + city + '\'' + ", state='" + state + '\'' + ", zipCode='" + zipCode + '\'' + ", number='" + number + '\'' + ", personId=" + personId + '}';
    }
}