# Rental Service

A production-grade Rental Management System for handling properties, tenants, leases, rent payments, and maintenance workflows.

## Features

- User authentication (registration and login) with JWT.
- Email verification for new users.
- Property management for owners.
- Tenant portal for managing leases and payments.
- Secure API endpoints.

## Technologies Used

- Java 21
- Spring Boot
- Spring Security (with JWT authentication)
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok
- Spring Mail

## Prerequisites

Before you begin, ensure you have the following installed:

- [Java JDK 21](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html)
- [Apache Maven](https://maven.apache.org/download.cgi)
- [PostgreSQL](https://www.postgresql.org/download/)

## Setup and Installation

1. **Clone the repository:**

    ```bash
    git clone https://github.com/AnkitV15/rental-service.git
    cd rental-service
    ```

2. **Configure your environment:**
    Create a file named `.env` in the root of the project and populate it with the necessary environment variables. See the [Configuration](#configuration) section for details.

3. **Build the project:**

    ```bash
    mvn clean install
    ```

## Configuration

The application requires the following environment variables to be set. For local development, you can create a `.env` file in the project root.

```dotenv
# Application URL
APP_URL=http://localhost:8080

# PostgreSQL Database
DB_URL=jdbc:postgresql://localhost:5432/rentalservice
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# Email SMTP Server
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_email_password

# JWT Secret
JWT_SECRET=your_super_secret_jwt_key_that_is_long_and_random
```

## API Endpoints

Below is a summary of the available API endpoints. For detailed information on request and response formats, please refer to the controller classes in the source code.

### Authentication

- `POST /api/auth/register`: Register a new user.
- `POST /api/auth/login`: Login an existing user and get a JWT token.
- `GET /api/auth/verify`: Verify a user's email address with a token.

### Owner

- `GET /api/owner/properties`: Get all properties for the authenticated owner.
- `POST /api/owner/properties`: Add a new property.
- `PUT /api/owner/properties/{id}`: Update an existing property.
- `DELETE /api/owner/properties/{id}`: Delete a property.

### Tenant

- `GET /api/tenant/leases`: Get all leases for the authenticated tenant.
- `GET /api/tenant/invoices`: Get all invoices for the authenticated tenant.

## Running the application

You can run the application using the following Maven command:

```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`.

## Running tests

To run the tests, use the following command:

```bash
mvn test
```

## Contributing

Contributions are welcome! If you have a suggestion or find a bug, please open an issue or submit a pull request.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.
