# Rental Service Backend

A production-grade, Spring Boot-based backend for the **RentFlow** property management platform. This service handles the core business logic for properties, tenants, leases, automated billing, payments, and maintenance workflows.

## 🚀 Key Features

### 🏢 Property & Unit Management
- **Hierarchical Structure**: Manage Properties and their individual Units.
- **Unit Details**: Track status (OCCUPIED/VACANT), usage types (Metered/Flat), and base rent.
- **Owner Dashboard**: Aggregated statistics for revenue, occupancy, and active concerns.

### 👥 Tenant & Lease Management
- **Digital Leases**: Create and track lease agreements with start/end dates.
- **Magic Link Authentication**: Secure, passwordless access for tenants via unique tokens.
- **Lease History**: Full audit trail of past and current leases.

### 💰 Billing & Payments
- **Automated Invoicing**: 
  - **Flat Rate**: Standard monthly rent.
  - **Metered**: Calculate utilities based on consumption (e.g., electricity readings).
- **Payment Integration**: Seamless payment processing via **Razorpay**.
- **Financial Tracking**: Real-time invoice status (PAID/PENDING/OVERDUE).

### 🛠️ Maintenance System
- **Ticket Management**: Tenants can raise issues with priority levels (Low to Emergency).
- **Workflow**: Owners can track status (Pending -> In Progress -> Completed).
- **Context Aware**: Requests are automatically linked to the active unit and tenant.

---

## 🛠️ Technologies Stack

- **Core**: Java 21, Spring Boot 3.3.4
- **Database**: PostgreSQL
- **Security**: Spring Security (JWT for Owners, Token-based for Tenants)
- **Payments**: Razorpay SDK
- **Utilities**: Lombok, Spring Mail
- **Build Tool**: Maven

---

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new property owner
- `POST /api/auth/login` - Owner login (returns JWT)

### Properties & Units
- `GET /api/owner/properties` - List all properties
- `GET /api/units/{id}` - Get specific unit details
- `PATCH /api/units/{id}` - Update unit details

### Leases (Owner)
- `POST /api/leases` - Create a new lease
- `GET /api/leases/unit/{unitId}/active` - Get active lease & magic link for a unit

### Tenant Portal (Magic Link)
- `GET /api/leases/verify/{token}` - Validate tenant session
- `GET /api/leases/verify/{token}/invoices` - Fetch tenant invoices
- `GET /api/leases/verify/{token}/maintenance` - Fetch tenant requests

### Maintenance
- `POST /api/maintenance` - Create a request (Tenant)
- `GET /api/owner/maintenance` - List all requests (Owner)
- `PUT /api/owner/maintenance/{id}/status` - Update request status

### Billing & Payments
- `POST /api/invoices/generate` - Trigger invoice generation
- `POST /api/payments/create-order/{invoiceId}` - Initialize Razorpay order
- `POST /api/payments/verify` - Webhook/Callback for payment confirmation

---

## ⚡ Quick Start

### Prerequisites
- Java JDK 21
- PostgreSQL
- Maven

### Installation

1. **Clone & Configure**
   ```bash
   git clone https://github.com/AnkitV15/Rental-Service.git
   cd rental-service
   ```
   Create a `.env` file (or set system env vars):
   ```properties
   DB_URL=jdbc:postgresql://localhost:5432/rentalservice
   DB_USERNAME=postgres
   DB_PASSWORD=your_password
   JWT_SECRET=your_secure_secret
   RAZORPAY_KEY_ID=your_key_id
   RAZORPAY_KEY_SECRET=your_key_secret
   ```

2. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

3. **Access**
   The API will be live at `http://localhost:8080`.

---
*Built for the RentFlow Platform.*
