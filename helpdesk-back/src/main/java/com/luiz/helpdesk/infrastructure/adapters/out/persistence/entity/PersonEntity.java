package com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.Address;
import com.luiz.helpdesk.domain.model.Person;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "persons")
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "person_profiles", joinColumns = @JoinColumn(name = "person_profiles_id"))
    @Enumerated(EnumType.STRING)
    private Set<Profile> profiles = new HashSet<>();

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDate creationDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    public PersonEntity() {
    }

    public PersonEntity(Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.cpf = person.getCpf();
        this.email = person.getEmail();
        this.password = person.getPassword();
        this.profiles = new HashSet<>(person.getProfiles());
        this.creationDate = person.getCreationDate();
        if (person.getAddress() != null) {
            this.address = AddressEntity.fromDomainModel(person.getAddress());
            this.address.setPerson(this);
        }
    }

    public void setAddress(AddressEntity address) {
        if (address == null) {
            if (this.address != null) {
                this.address.setPerson(null);
            }
        } else {
            address.setPerson(this);
        }
        this.address = address;
    }

    public Person toDomainModel() {
        Person.Builder builder = Person.builder()
                .withId(id)
                .withName(name)
                .withCpf(cpf)
                .withEmail(email)
                .withPassword(password)
                .withCreationDate(creationDate)
                .withProfiles(new HashSet<>(profiles));

        if (address != null) {
            builder.withAddress(address.toDomainModel());
        }

        return builder.build();
    }

    public static PersonEntity fromDomainModel(Person person) {
        return new PersonEntity(person);
    }

    public void updateFromDomainModel(Person person) {
        this.name = person.getName();
        this.cpf = person.getCpf();
        this.email = person.getEmail();
        this.password = person.getPassword();
        this.profiles.clear();
        this.profiles.addAll(person.getProfiles());

        Address personAddress = person.getAddress();
        if (personAddress != null) {
            if (this.address == null) {
                this.address = new AddressEntity();
                this.address.setPerson(this);
            }
            this.address.updateFromDomainModel(personAddress);
        } else if (this.address != null) {
            this.address.setPerson(null);
            this.address = null;
        }
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

    public Set<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public AddressEntity getAddress() {
        return address;
    }
}