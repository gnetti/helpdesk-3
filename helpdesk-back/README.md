
# HelpDesk Backend

## Overview
This project is a HelpDesk system based on a hexagonal architecture (ports and adapters) implemented with Spring Boot 3.3.0 and Java 17. It utilizes JWT authentication and offers pagination features for efficient querying.

## Project Structure


│com.luiz.helpdesk
│
├── application
│ ├── services
│ └── ports
│ ├── in
│ │ └── input
│ └── out
│
├── domain
│ ├── model
│ ├── factory
│ ├── enums
│ └── exception
│
└── infrastructure
└── adapters
├── in
│ └── web
│ └── rest
└── out
├── config
├── persistence
└── security

./mvnw test
This README provides a comprehensive overview of your HelpDesk backend project, including its structure, technologies used, key features, setup and usage instructions, as well as information on security, testing, and contribution. You can adjust and expand it as needed to better fit the specifics of your project.


## Technologies Used
- Java 17
- Spring Boot 3.3.0
- Spring Security
- JSON Web Tokens (JWT)
- Spring Data JPA
- Hibernate
- PostgreSQL (or your preferred database)
- Maven

## Hexagonal Architecture
This project follows the principles of hexagonal architecture, also known as "Ports and Adapters". This architecture aims to create a highly modular, testable, and framework-independent system.

### Main Layers:
1. **Domain**: Contains core business logic and entities.
2. **Application**: Defines input and output ports and implements use cases.
3. **Infrastructure**: Contains adapters to interact with the external world (web, database, etc.).

## Key Features
- Authentication and Authorization using JWT
- CRUD operations for Support Tickets
- User and Role Management
- Pagination and Sorting of Results
- Custom Exception Handling

## Setup and Installation
1. Clone the repository:
   ```
   git clone https://github.com/your-username/helpdesk-backend.git
   ```
2. Configure the database in `application.properties`.
3. Run the project:
   ```
   ./mvnw spring-boot:run
   ```

## Authentication
The system uses JWT for authentication. To obtain a token:

1. Make a POST request to `/api/auth/login` with valid credentials.
2. Use the returned token in the `Authorization` header for subsequent requests.

## Pagination
APIs returning lists support pagination. Use the parameters:
- `page`: Page number (starting from 0)
- `size`: Page size
- `sort`: Field and direction for sorting (e.g., `name,asc`)

Example: `/api/tickets?page=0&size=20&sort=createdAt,desc`

## Main Endpoints
- `/api/auth/*`: Authentication endpoints
- `/api/tickets/*`: Ticket management
- `/api/users/*`: User management

## Testing
Run tests with:


## Security
- All passwords are stored using bcrypt hashing.
- JWT tokens are signed and have configurable expiration.
- CORS is configured to allow only specific origins.

## Exception Handling
Custom exceptions are defined in `domain.exception` and handled globally to return consistent error responses.

## Logging
Detailed logging is configured using SLF4J with Logback. Log files are rotated daily.

## Monitoring
Spring Actuator endpoints are enabled for application health and metrics monitoring.

## API Documentation
API documentation is automatically generated using Springdoc-OpenAPI and can be accessed at `/swagger-ui.html` when the application is running.

## Contributing
Contributions are welcome! Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE.md file for details.



# HelpDesk Frontend

# AngularMaterialApp

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 18.2.4, implementing a hexagonal architecture with Angular Material.

## Prerequisites

Ensure you have the following installed:

- Node.js (version 14.x or higher)
- npm (usually comes with Node.js)
- Angular CLI (`npm install -g @angular/cli`)

## Installation

1. Clone the repository:
   ```
   git clone https://github.com/your-username/AngularMaterialApp.git
   ```
2. Navigate to the project directory:
   ```
   cd AngularMaterialApp
   ```
3. Install dependencies:
   ```
   npm install
   ```

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

For automatic browser opening:

ng serve --open

## Code Structure

The project follows a hexagonal architecture with Angular best practices:

src/
├── app/
│   ├── core/
│   │   ├── domain/
│   │   │   ├── models/
│   │   │   └── ports/
│   │   │       ├── in/
│   │   │       └── out/
│   │   ├── application/
│   │   │   └── services/
│   │   └── infrastructure/
│   │       ├── adapters/
│   │       │   ├── in/
│   │       │   │   └── web/
│   │       │   └── out/
│   │       │       └── persistence/
│   │       └── config/
│   ├── shared/
│   │   ├── components/
│   │   ├── directives/
│   │   ├── pipes/
│   │   └── utils/
│   └── modules/
│       
│       
├── assets/
└── environments/

ng build --configuration production
ng test --code-coverage
npm run compodoc
This README:

1. Is entirely in English.
2. Includes information about the hexagonal architecture.
3. Provides a more detailed project structure that reflects the hexagonal architecture.
4. Maintains all the essential information about building, testing, and running the Angular application.
5. Includes sections on contributing, versioning, authors, and licensing.
   Remember to adjust the links, usernames, and other project-specific information as needed. This comprehensive README will help new developers understand the project structure, the hexagonal architecture implementation, and how to contribute to the project effectively.

## Code scaffolding

Generate new components, services, etc. using Angular CLI. Examples:

- Component: `ng generate component features/component-name`
- Service: `ng generate service core/services/service-name`
- Module: `ng generate module features/module-name --routing`

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

For production build:

ng build --configuration production

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

For coverage report:

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via a platform of your choice.

Note: To use this command, you need to first add a package that implements end-to-end testing capabilities.

## Linting and Formatting

- Run linter: `ng lint`
- Format code: `npm run format` (requires additional setup)

## Documentation

Generate project documentation:

npm run compodoc

## Hexagonal Architecture

This project implements a hexagonal architecture (also known as Ports and Adapters) with the following key components:

- **Domain**: Contains the core business logic.
- **Ports**: Define interfaces for the application core.
- **Adapters**: Implement the interfaces defined by the ports.
- **Use Cases**: Orchestrate the flow of data to and from the entities.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your-username/AngularMaterialApp/tags).

## Authors

* **Your Name** - *Initial work* - [YourUsername](https://github.com/YourUsername)

See also the list of [contributors](https://github.com/your-username/AngularMaterialApp/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc
