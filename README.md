# Diploma CRM - Campus Office Management System

A comprehensive Customer Relationship Management (CRM) system built for managing software engineering diploma programs. This application streamlines student enrollment, course management, instructor coordination, payment tracking, and student interactions.

## 🚀 Features

### Core Modules
- **Dashboard** - Real-time statistics and activity overview
- **Student Management** - Complete student lifecycle tracking
- **Course Management** - Course catalog and scheduling
- **Instructor Management** - Faculty profiles and course assignments
- **Enrollment Management** - Student-course relationship tracking with grades
- **Interaction Tracking** - CRM-style communication logging (emails, calls, meetings)
- **Payment Management** - Billing, invoicing, and payment status tracking

### Technical Features
- ✅ Spring Boot backend with JPA/Hibernate
- ✅ Vaadin 24 modern UI framework
- ✅ Spring Security with role-based access control
- ✅ BCrypt password encryption
- ✅ PostgreSQL database
- ✅ Lombok for clean code
- ✅ JPA Auditing for automatic timestamps
- ✅ RESTful architecture ready

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17+ | Backend language |
| Spring Boot | 3.x | Application framework |
| Spring Security | 6.x | Authentication & Authorization |
| Vaadin | 24.x | UI Framework |
| PostgreSQL | 14+ | Database |
| Lombok | Latest | Code generation |
| Maven | 3.8+ | Build tool |

## 📋 Prerequisites

- JDK 17 or higher
- Maven 3.8+
- PostgreSQL 14+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🔧 Installation

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/diploma-crm.git
cd diploma-crm
```

### 2. Configure Database
Create a PostgreSQL database:
```sql
CREATE DATABASE campus_office;
```

Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/campus_office
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Generate Initial Users
Run the password encoder utility to generate test users:
```bash
mvn compile exec:java -Dexec.mainClass="nibm.project.campus_office.util.PasswordEncoderUtil"
```

This will output SQL INSERT statements. Execute them in your database.

### 4. Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

Access the application at: **http://localhost:8080**

## 🔐 Default Login Credentials

| Username | Password | Role |
|----------|----------|------|
| admin | admin | ADMIN |
| staff | staff123 | STAFF |
| instructor | instructor456 | INSTRUCTOR |

⚠️ **Change these credentials in production!**

## 📁 Project Structure

```
src/main/java/
├── config/
│   ├── JpaConfig.java              # JPA Auditing configuration
│   └── SecurityConfig.java         # Spring Security setup
├── entity/
│   ├── BaseEntity.java             # Base entity with audit fields
│   ├── User.java                   # Authentication entity
│   ├── Student.java
│   ├── Instructor.java
│   ├── Course.java
│   ├── Enrollment.java
│   ├── Interaction.java
│   └── Payment.java
├── repository/
│   ├── UserRepository.java
│   ├── StudentRepository.java
│   ├── InstructorRepository.java
│   ├── CourseRepository.java
│   ├── EnrollmentRepository.java
│   ├── InteractionRepository.java
│   └── PaymentRepository.java
├── security/
│   ├── UserDetailsServiceImpl.java
│   └── SecurityService.java
├── util/
│   └── PasswordEncoderUtil.java    # Password encoding utility
└── views/
    ├── LoginView.java
    ├── MainLayout.java
    ├── DashboardView.java
    ├── StudentListView.java
    ├── CourseListView.java
    ├── InstructorListView.java
    ├── EnrollmentListView.java
    ├── InteractionListView.java
    └── PaymentListView.java
```

## 🎯 Usage Guide

### Managing Students
1. Navigate to **Students** from the sidebar
2. Click **Add Student** to create new records
3. Click on any row to edit
4. Use the filter to search by name

### Enrolling Students
1. Go to **Enrollments**
2. Click **Add Enrollment**
3. Select student and course
4. Set enrollment date and status
5. Add grades when completed

### Tracking Interactions
1. Open **Interactions**
2. Log every communication (email, call, meeting)
3. Add notes and set contacted by field
4. View complete interaction history per student

### Managing Payments
1. Navigate to **Payments**
2. Create payment records with due dates
3. Update status as payments are received
4. Track pending/overdue payments from dashboard

## 🔒 Security Features

- **Authentication**: Form-based login with session management
- **Authorization**: Role-based access control (ADMIN, STAFF, INSTRUCTOR)
- **Password Encryption**: BCrypt hashing
- **CSRF Protection**: Handled by Vaadin
- **Session Management**: Secure logout functionality

### Adding Role-Based Access
Use `@RolesAllowed` annotation on views:
```java
@Route("admin-panel")
@RolesAllowed("ADMIN")
public class AdminPanelView extends VerticalLayout { }
```

## 📊 Database Schema

### Key Relationships
- **Student** ↔ **Enrollment** ↔ **Course** (Many-to-Many through Enrollment)
- **Instructor** ↔ **Course** (One-to-Many)
- **Student** ↔ **Interaction** (One-to-Many)
- **Student** ↔ **Payment** (One-to-Many)
- **User** ↔ **Instructor** (One-to-One, optional)

All entities extend `BaseEntity` with:
- `id` - Primary key
- `createdAt` - Auto-populated on creation
- `updatedAt` - Auto-updated on modification
- `active` - Soft delete flag


## 🗺️ Roadmap

- [ ] Email notifications for due payments
- [ ] Report generation (PDF/Excel)
- [ ] Student portal (self-service)
- [ ] Mobile responsive design
- [ ] API endpoints for external integrations
- [ ] Advanced analytics dashboard
- [ ] Attendance tracking module
- [ ] Assignment submission system

---

**Made with ❤️ for Software Engineering Diploma Programs**