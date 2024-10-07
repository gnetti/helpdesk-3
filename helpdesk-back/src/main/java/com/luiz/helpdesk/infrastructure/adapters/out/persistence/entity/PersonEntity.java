package com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity;

import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils.RecursionControlWrapperUtil;
import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    @Column(name = "profile")
    private Set<Integer> profiles = new HashSet<>();

    @Column(nullable = false)
    private LocalDate creationDate;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private AddressEntity address;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "person_themes", joinColumns = @JoinColumn(name = "person_id"))
    @Column(name = "theme")
    private Set<Integer> themes = new HashSet<>();

    public PersonEntity() {
    }

    @PrePersist
    @PreUpdate
    private void encodePassword() {
        if (this.password != null && !this.password.startsWith("$2a$")) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            this.password = encoder.encode(this.password);
        }
    }

    public void updateFromDomainModel(Person person) {
        RecursionControlWrapperUtil.executeWithRecursionControl(() -> {
            this.id = person.getId();
            this.name = person.getName();
            this.cpf = person.getCpf();
            this.email = person.getEmail();
            this.password = person.getPassword();
            this.profiles.clear();
            this.profiles.add(person.getProfile());
            this.creationDate = person.getCreationDate();
            this.themes.clear();
            this.themes.add(person.getTheme());
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
                        .withProfile(this.profiles.isEmpty() ? null : this.profiles.iterator().next())
                        .withCreationDate(this.creationDate)
                        .withAddress(this.address != null ? this.address.toDomainModel() : null)
                        .withTheme(this.themes.isEmpty() ? null : this.themes.iterator().next())
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


    public void updateCurrentUser(Integer newTheme, String newPassword) {
        if (newTheme != null) {
            this.themes.clear();
            this.themes.add(newTheme);
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            this.password = newPassword;
        }
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

    public Integer getProfile() {
        return profiles.isEmpty() ? null : profiles.iterator().next();
    }

    public void setProfile(Integer profile) {
        this.profiles.clear();
        if (profile != null) {
            this.profiles.add(profile);
        }
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

    public Integer getTheme() {
        return themes.isEmpty() ? null : themes.iterator().next();
    }

    public void setTheme(Integer theme) {
        this.themes.clear();
        if (theme != null) {
            this.themes.add(theme);
        }
    }
}