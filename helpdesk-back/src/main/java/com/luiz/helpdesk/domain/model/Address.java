package com.luiz.helpdesk.domain.model;

import java.util.Objects;

public class Address {

    private final Integer id;
    private final String street;
    private final String complement;
    private final String neighborhood;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String number;
    private final Integer personId;

    private Address(Builder builder) {
        this.id = builder.id;
        this.street = builder.street;
        this.complement = builder.complement;
        this.neighborhood = builder.neighborhood;
        this.city = builder.city;
        this.state = builder.state;
        this.zipCode = builder.zipCode;
        this.number = builder.number;
        this.personId = builder.personId;
    }

    public Address updateFields(Address newData) {
        return new Builder()
                .withId(this.id)
                .withStreet(newData.getStreet())
                .withComplement(newData.getComplement())
                .withNeighborhood(newData.getNeighborhood())
                .withCity(newData.getCity())
                .withState(newData.getState())
                .withZipCode(newData.getZipCode())
                .withNumber(newData.getNumber())
                .withPersonId(this.personId)
                .build();
    }

    public Address withPersonId(Integer personId) {
        return new Builder()
                .withId(this.id)
                .withStreet(this.street)
                .withComplement(this.complement)
                .withNeighborhood(this.neighborhood)
                .withCity(this.city)
                .withState(this.state)
                .withZipCode(this.zipCode)
                .withNumber(this.number)
                .withPersonId(personId)
                .build();
    }

    public static Address createWithPersonId(
                Address address,
                Integer personId) {
        return new Builder()
                .withStreet(address.getStreet())
                .withComplement(address.getComplement())
                .withNeighborhood(address.getNeighborhood())
                .withCity(address.getCity())
                .withState(address.getState())
                .withZipCode(address.getZipCode())
                .withNumber(address.getNumber())
                .withPersonId(personId)
                .build();
    }

    public static Address createNew(
                String street,
                String complement,
                String neighborhood,
                String city,
                String state,
                String zipCode,
                String number,
                Integer personId) {
        return new Builder()
                .withStreet(street)
                .withComplement(complement)
                .withNeighborhood(neighborhood)
                .withCity(city)
                .withState(state)
                .withZipCode(zipCode)
                .withNumber(number)
                .withPersonId(personId)
                .build();
    }

    public static Address reconstitute(
                   Integer id,
                   String street,
                   String complement,
                   String neighborhood,
                   String city,
                   String state,
                   String zipCode,
                   String number,
                   Integer personId) {
        return new Builder()
                .withId(id)
                .withStreet(street)
                .withComplement(complement)
                .withNeighborhood(neighborhood)
                .withCity(city)
                .withState(state)
                .withZipCode(zipCode)
                .withNumber(number)
                .withPersonId(personId)
                .build();
    }

    public Integer getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getComplement() {
        return complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getNumber() {
        return number;
    }

    public Integer getPersonId() {
        return personId;
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

        public Address build() {
            return new Address(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(id, address.id) &&
                Objects.equals(street, address.street) &&
                Objects.equals(complement, address.complement) &&
                Objects.equals(neighborhood, address.neighborhood) &&
                Objects.equals(city, address.city) &&
                Objects.equals(state, address.state) &&
                Objects.equals(zipCode, address.zipCode) &&
                Objects.equals(number, address.number) &&
                Objects.equals(personId, address.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street, complement, neighborhood, city, state, zipCode, number, personId);
    }

    @Override
    public String toString() {
        return new StringBuilder("Address{")
                .append("id=").append(id)
                .append(", street='").append(street).append('\'')
                .append(", complement='").append(complement).append('\'')
                .append(", neighborhood='").append(neighborhood).append('\'')
                .append(", city='").append(city).append('\'')
                .append(", state='").append(state).append('\'')
                .append(", zipCode='").append(zipCode).append('\'')
                .append(", number='").append(number).append('\'')
                .append(", personId=").append(personId)
                .append('}')
                .toString();
    }
}