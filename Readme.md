# SkillSynch – Placement Management System

SkillSynch is a Java-based placement management system designed to streamline the campus recruitment process by connecting students, companies, and college placement administrators through a centralized platform.

The application provides role-based access for students, companies, and college administrators, enabling them to manage placement-related activities through dedicated dashboards.

## Tech Stack

| Component | Technology |
|---|---|
| Programming Language | Java 17 |
| Frontend | JavaFX |
| Backend | Core Java |
| Database | MySQL 8.x |
| Database Connectivity | JDBC |
| Build Tool | Maven |
| Password Security | jBCrypt |
| IDE | IntelliJ IDEA |

## Features

### Student
- Register and log in securely.
- Maintain academic and personal details.
- Manage skills and proficiency information.
- Browse placement and internship opportunities.
- Apply for available opportunities.
- Track application status and offers.
- Access a dedicated student dashboard.

### Company
- Register and log in.
- Maintain company information.
- Create placement and internship opportunities.
- View student applications.
- Shortlist and select candidates.
- Manage offers.
- Access a dedicated company dashboard.

### College Admin
- Secure login using a pre-created administrator account.
- Review company opportunities.
- Approve or reject opportunities.
- Publish approved opportunities to students.
- Monitor placement activities.
- Access an administrative dashboard.

**Implementation note:** Dashboard interfaces and core application components are available. Some dashboard operations currently use sample data or prototype functionality and may require further database integration.

## Project Structure

```text
SkillSynch/
│
├── pom.xml
├── Readme.md
├── .gitignore
│
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── skillsynch/
        │           ├── Main.java
        │           ├── DatabaseConnectionTest.java
        │           ├── GenerateAdminHash.java
        │           │
        │           ├── config/
        │           │   └── DatabaseConfig.java
        │           │
        │           ├── model/
        │           │   └── User.java
        │           │
        │           ├── dao/
        │           │   ├── UserDAO.java
        │           │   ├── StudentDAO.java
        │           │   ├── CompanyDAO.java
        │           │   └── SkillDAO.java
        │           │
        │           ├── service/
        │           │   ├── AuthService.java
        │           │   └── RegistrationService.java
        │           │
        │           └── ui/
        │               ├── LoginView.java
        │               ├── RegisterView.java
        │               ├── StudentDashboard.java
        │               ├── CompanyDashboard.java
        │               └── AdminDashboard.java
        │
        └── resources/
            └── css/
                └── style.css
```

The `db-credentials.properties` file is created locally and is intentionally excluded from version control.

## Prerequisites

Install the following before setting up the project:

- Java Development Kit (JDK) 17
- IntelliJ IDEA
- MySQL Server 8.x
- MySQL Workbench
- Git

Maven dependencies are managed through `pom.xml`. IntelliJ IDEA's bundled Maven can be used without installing Maven separately.

## Installation and Setup

### 1. Clone the repository

```bash
git clone https://github.com/navya0776/skill_sync_java_ver.git
cd skill_sync_java_ver
```

Open the project in IntelliJ IDEA.

### 2. Configure Java and Maven

1. Set the Project SDK to Java 17.
2. Ensure IntelliJ recognizes `pom.xml` as a Maven project.
3. Reload Maven dependencies.
4. Verify that JavaFX dependencies are downloaded successfully.

### 3. Set up the MySQL database

Start MySQL Server and open MySQL Workbench.

Create the database:

```sql
CREATE DATABASE skillsynch;
```

Select the database:

```sql
USE skillsynch;
```

Import the SkillSynch SQL schema using MySQL Workbench, if the schema file is available.

Verify the database tables:

```sql
SHOW TABLES;
```

### 4. Configure database credentials

Create the following local configuration file:

```text
src/main/resources/db-credentials.properties
```

Add your MySQL connection details:

```properties
db.url=jdbc:mysql://localhost:3306/skillsynch
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```

Replace `YOUR_MYSQL_PASSWORD` with your local MySQL password.

**Security:** Never commit this file to GitHub or share your database password.

### 5. Run the application

Open the IntelliJ terminal in the project directory and execute:

```bash
mvn clean javafx:run
```

Alternatively, run the `Main.java` class directly through IntelliJ IDEA.

## Initial Admin Account

The College Admin account is created manually through MySQL Workbench.

Public administrator registration is not available.

For password security, store a BCrypt password hash in the `password_hash` column instead of storing a plain-text password.

The `GenerateAdminHash.java` utility can be used to generate a BCrypt hash.

## Database Tables

The SkillSynch database schema includes the following tables:

- Users
- Students
- Companies
- Skills
- Student_Skills
- Internships
- Internship_Skills
- Student_Languages
- Internship_Languages
- Eligibility
- Applications
- Offers

## Authentication and Security

- Passwords are hashed using BCrypt.
- Database operations use JDBC prepared statements.
- Registration uses database transactions to maintain data consistency.
- Database credentials are stored locally and excluded from version control.
- College Admin accounts are created separately from public registration.
- Role-based access directs users to their respective dashboards.

## Development Workflow

For collaborative development, create a separate branch for each feature:

```bash
git checkout -b feature/your-feature-name
```

After making changes:

```bash
git add .
git commit -m "Describe your changes"
git push -u origin feature/your-feature-name
```

Create a Pull Request on GitHub to merge your changes into the `main` branch.

## Future Enhancements

- Complete database integration for all dashboard operations.
- Automated eligibility calculation.
- Advanced student skill matching.
- Application tracking and shortlisting.
- Interview scheduling and offer management.
- Placement analytics and reporting.
- Improved dashboard visualizations.

---

## SkillSynch

*A centralized platform for smarter campus placement management.*
