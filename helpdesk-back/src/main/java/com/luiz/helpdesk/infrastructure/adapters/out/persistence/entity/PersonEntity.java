package com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.enums.Theme;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils.RecursionControlWrapperUtil;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "persons")
public class PersonEntity {

    private static final ThreadLocal<Integer> recursionDepth = new ThreadLocal<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "person_profiles", joinColumns = @JoinColumn(name = "person_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "profile")
    private Set<Profile> profiles = new HashSet<>();

    @Column(nullable = false)
    private LocalDate creationDate;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private AddressEntity address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Theme theme;

    public PersonEntity() {
    }

    public void updateFromDomainModel(Person person) {
        RecursionControlWrapperUtil.executeWithRecursionControl(() -> {
            this.id = person.getId();
            this.name = person.getName();
            this.cpf = person.getCpf();
            this.email = person.getEmail();
            this.password = person.getPassword();
            this.profiles = new HashSet<>(person.getProfiles());
            this.creationDate = person.getCreationDate();
            this.theme = person.getTheme();
            updateAddress(person);
            return null;
        });
    }

    private void updateAddress(Person person) {
        RecursionControlWrapperUtil.executeWithRecursionControl(() -> {
            if (person.getAddress() != null) {
                if (this.address == null) {
                    this.address = new AddressEntity();
                    this.address.setPerson(this);
                }
                this.address.updateFromDomainModel(person.getAddress());
            } else if (this.address != null) {
                this.address.setPerson(null);
                this.address = null;
            }
            return null;
        });
    }

    public Person toDomainModel() {
        return RecursionControlWrapperUtil.executeWithRecursionControl(() ->
                Person.builder()
                        .withId(this.id)
                        .withName(this.name)
                        .withCpf(this.cpf)
                        .withEmail(this.email)
                        .withPassword(this.password)
                        .withProfiles(new HashSet<>(this.profiles))
                        .withCreationDate(this.creationDate)
                        .withAddress(this.address != null ? this.address.toDomainModel() : null)
                        .withTheme(this.theme)
                        .build()
        );
    }

    public static PersonEntity fromDomainModel(Person person) {
        return RecursionControlWrapperUtil.executeWithRecursionControl(() -> {
            PersonEntity entity = new PersonEntity();
            entity.updateFromDomainModel(person);
            return entity;
        });
    }

    public PersonEntity updateFromDomainModelGetMe(Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.cpf = person.getCpf();
        this.email = person.getEmail();
        this.profiles.clear();
        this.profiles.addAll(person.getProfiles());
        this.theme = person.getTheme() != null ? person.getTheme() : this.theme;
        this.creationDate = person.getCreationDate() != null ? person.getCreationDate() : LocalDate.now();
        return this;
    }

    public PersonEntity updateFromDomainModelPutMe(Person person) {
        this.name = person.getName();
        this.cpf = person.getCpf();
        this.email = person.getEmail();
        if (person.getPassword() != null && !person.getPassword().isEmpty()) {
            this.password = person.getPassword();
        }
        this.profiles.clear();
        this.profiles.addAll(person.getProfiles());
        this.theme = person.getTheme() != null ? person.getTheme() : this.theme;
        return this;
    }

    public Person toDomainModelGetMe() {
        return Person.builder()
                .withId(id)
                .withName(name)
                .withCpf(cpf)
                .withEmail(email)
                .withCreationDate(creationDate)
                .withProfiles(new HashSet<>(profiles))
                .withTheme(theme)
                .build();
    }

    public static void incrementRecursionDepth() {
        recursionDepth.set(getRecursionDepth() + 1);
    }

    public static void decrementRecursionDepth() {
        recursionDepth.set(getRecursionDepth() - 1);
    }

    private static int getRecursionDepth() {
        return recursionDepth.get() == null ? 0 : recursionDepth.get();
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

    public void setAddress(AddressEntity address) {
        this.address = address;
        if (address != null) {
            address.setPerson(this);
        }
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}