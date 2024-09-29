package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.InitializeDatabaseUseCasePort;
import com.luiz.helpdesk.application.ports.out.PasswordEncoderPort;
import com.luiz.helpdesk.application.ports.out.PersonPersistenceOutputPort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.enums.Theme;
import com.luiz.helpdesk.domain.factory.AddressFactory;
import com.luiz.helpdesk.domain.factory.PersonFactory;
import com.luiz.helpdesk.domain.model.Address;
import com.luiz.helpdesk.domain.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class DatabaseInitializationService implements InitializeDatabaseUseCasePort {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializationService.class);
    private final PersonPersistenceOutputPort personRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final PersonFactory personFactory;
    private final AddressFactory addressFactory;
    private final Set<String> usedEmails = new HashSet<>();
    private final Set<String> usedCPFs = new HashSet<>();

    public DatabaseInitializationService(
            PersonPersistenceOutputPort personRepository,
            PasswordEncoderPort passwordEncoder,
            PersonFactory personFactory,
            AddressFactory addressFactory
    ) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.personFactory = personFactory;
        this.addressFactory = addressFactory;
    }

    @Override
    @Transactional
    public void initializeDatabase() {
        createAdmin();
        createRandomPeople();
    }

    private void createAdmin() {
        logger.info("Starting admin user creation process");

        String rawPassword = "L@ndQLYN5yvx";
        logger.debug("Raw admin password: {}", rawPassword);

        String encodedPassword = passwordEncoder.encode(rawPassword);
        logger.debug("Encoded admin password: {}", encodedPassword);

        Set<Profile> profiles = new HashSet<>(Arrays.asList(Profile.ADMIN, Profile.CLIENT));
        logger.debug("Admin profiles: {}", profiles);

        LocalDate creationDate = LocalDate.now();
        logger.debug("Admin creation date: {}", creationDate);

        Theme adminTheme = Theme.PINK_BLUE_GREY;
        logger.debug("Admin theme: {}", adminTheme);

        if (personRepository.findByEmail("admin@email.com").isEmpty()) {
            logger.info("Admin user does not exist. Creating new admin user.");

            Address address = addressFactory.createAddress(
                    "Admin Street",
                    "Apt 123",
                    "Admin Neighborhood",
                    "Admin City",
                    "Admin State",
                    "12345-678",
                    "100"
            );
            logger.debug("Created admin address: {}", address);

            Person admin = personFactory
                    .createPerson("admin", "12345678900", "admin@email.com",
                            encodedPassword, profiles, creationDate, String.valueOf(adminTheme))
                    .withAddress(address);
            logger.debug("Created admin person: {}", admin);

            personRepository.save(admin);
            logger.info("Admin user saved to database");

            usedEmails.add("admin@mail.com");
            usedCPFs.add("12345678900");
            logger.debug("Added admin email and CPF to used lists");
        } else {
            logger.info("Admin user already exists. Skipping creation.");
        }

        logger.info("Admin user creation process completed");
    }

    private void createRandomPeople() {
        String encodedPassword = passwordEncoder.encode("password");
        LocalDate creationDate = LocalDate.now();

        for (int i = 0; i < 100; i++) {
            Person person = createRandomPerson(encodedPassword, creationDate);
            personRepository.save(person);
        }
    }

    private Person createRandomPerson(String encodedPassword, LocalDate creationDate) {
        Set<Profile> clientProfile = new HashSet<>(Collections.singletonList(Profile.CLIENT));
        Address address = generateRandomAddress();
        String name = generateRandomName();
        String email = generateUniqueEmail(name);
        String cpf = generateUniqueCPF();
        Theme randomTheme = getRandomTheme();

        return personFactory.createPerson(name, cpf, email, encodedPassword,
                        clientProfile, creationDate, String.valueOf(randomTheme))
                .withAddress(address);
    }

    private Theme getRandomTheme() {
        Theme[] themes = Theme.values();
        return themes[new Random().nextInt(themes.length)];
    }

    private String generateUniqueCPF() {
        String cpf;
        do {
            cpf = generateValidCPF();
        } while (usedCPFs.contains(cpf));
        usedCPFs.add(cpf);
        return cpf;
    }

    private String generateValidCPF() {
        Random random = new Random();
        int[] cpf = new int[11];
        for (int i = 0; i < 9; i++) {
            cpf[i] = random.nextInt(10);
        }

        for (int i = 9; i < 11; i++) {
            int sum = 0;
            int weight = i + 1;
            for (int j = 0; j < i; j++) {
                sum += cpf[j] * weight;
                weight--;
            }
            int digit = 11 - (sum % 11);
            cpf[i] = (digit > 9) ? 0 : digit;
        }

        return String.format("%d%d%d.%d%d%d.%d%d%d-%d%d",
                cpf[0], cpf[1], cpf[2], cpf[3], cpf[4], cpf[5], cpf[6], cpf[7], cpf[8], cpf[9], cpf[10]);
    }

    private String generateRandomName() {
        String[] firstNames = {"James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda",
                "William", "Elizabeth", "David", "Barbara", "Richard", "Margaret", "Joseph", "Susan"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
                "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas"};

        Random random = new Random();
        return firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
    }

    private String generateUniqueEmail(String name) {
        String baseEmail = name.toLowerCase().replace(" ", ".");
        String email = baseEmail + "@email.com";
        int suffix = 1;
        while (usedEmails.contains(email)) {
            email = baseEmail + suffix + "@email.com";
            suffix++;
        }
        usedEmails.add(email);
        return email;
    }

    private Address generateRandomAddress() {
        String[] streets = {"Oak", "Maple", "Cedar", "Pine", "Elm", "Birch", "Willow", "Walnut", "Cherry", "Spruce"};
        String[] cities = {"Springfield", "Rivertown", "Lakeside", "Hillview", "Meadowbrook", "Sunnyville",
                "Fairview", "Greenville", "Brookside", "Millbrook"};
        String[] states = {"CA", "NY", "TX", "FL", "IL", "PA", "OH", "GA", "NC", "MI"};

        Random random = new Random();
        return addressFactory.createAddress(
                streets[random.nextInt(streets.length)] + " Street",
                String.valueOf(random.nextInt(1000) + 1),
                "Neighborhood " + (random.nextInt(10) + 1),
                cities[random.nextInt(cities.length)],
                states[random.nextInt(states.length)],
                String.format("%05d-%03d", random.nextInt(100000), random.nextInt(1000)),
                String.valueOf(random.nextInt(1000) + 1)
        );
    }
}