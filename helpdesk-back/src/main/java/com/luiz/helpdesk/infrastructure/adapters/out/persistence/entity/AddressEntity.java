package com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity;

import com.luiz.helpdesk.domain.model.Address;
import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String street;

    private String complement;

    @Column(nullable = false)
    private String neighborhood;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String number;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private PersonEntity person;

    public AddressEntity() {
    }

    public void updateFromDomainModel(Address address) {
        this.id = address.getId();
        this.street = address.getStreet();
        this.complement = address.getComplement();
        this.neighborhood = address.getNeighborhood();
        this.city = address.getCity();
        this.state = address.getState();
        this.zipCode = address.getZipCode();
        this.number = address.getNumber();
    }

    public Address toDomainModel() {
        return Address.builder()
                .withId(this.id)
                .withStreet(this.street)
                .withComplement(this.complement)
                .withNeighborhood(this.neighborhood)
                .withCity(this.city)
                .withState(this.state)
                .withZipCode(this.zipCode)
                .withNumber(this.number)
                .withPersonId(this.person != null ? this.person.getId() : null)
                .build();
    }

    public static AddressEntity fromDomainModel(Address address) {
        AddressEntity entity = new AddressEntity();
        entity.updateFromDomainModel(address);
        return entity;
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

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
        if (this.person != null) {
            this.person.setAddress(null);
        }
        this.person = person;
        if (person != null && person.getAddress() != this) {
            person.setAddress(this);
        }
    }
}