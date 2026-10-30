# SkillSynch – Placement Management System

SkillSynch is a Java-based placement management system designed to streamline the campus recruitment process by connecting students, companies, and college placement administrators through a centralized platform.

The application provides role-based access for students, companies, and college administrators, allowing them to manage placement-related activities efficiently.

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
- Register and log in.
- Maintain academic and personal details.
- Manage skills and proficiency information.
- Browse eligible placement opportunities.
- Apply for internships and jobs.
- Track application status and offers.

### Company
- Register and log in.
- Maintain company information.
- Create placement opportunities.
- View student applications.
- Shortlist and select candidates.
- Manage offers.

### College Admin
- Secure login using a pre-created administrator account.
- Review company opportunities.
- Approve or reject opportunities.
- Publish approved opportunities to students.
- Monitor placement activities and statistics.

## Project Structure

```text
SkillSynch/
│
├── pom.xml
├── README.md
├── .gitignore
│
├── database/
│   └── SkillSynch_Final_Schema.sql
│
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── skillsynch/
        │           ├── Main.java
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
        │           │   └── CompanyDAO.java
        │           │
        │           ├── service/
        │           │   ├── AuthService.java
        │           │   └── RegistrationService.java
        │           │
        │           ├── controller/
        │           │
        │           └── ui/
        │               ├── LoginView.java
        │               └── RegisterView.java
        │
        └── resources/
            ├── css/
            │   └── style.css
            └── db-credentials.properties
```

*Note: The credentials file is created locally and is not committed to GitHub. The structure above describes the current application organization and intended package locations.*

## Prerequisites

Install the following before setting up the project:

1. Java Development Kit (JDK) 17.
2. IntelliJ IDEA.
3. MySQL Server 8.x.
4. MySQL Workbench.
5. Git.

Maven dependencies are managed through the project's `pom.xml`. A separate Maven installation is not required when using IntelliJ's bundled Maven.

## Installation and Setup

### 1. Clone the repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

Navigate into the project:

```bash
cd YOUR_PROJECT_FOLDER
```

Open the project in IntelliJ IDEA.

### 2. Configure Java and Maven

- Set the Project SDK to Java 17.
- Ensure IntelliJ recognizes `pom.xml` as a Maven project.
- Reload Maven dependencies.

### 3. Set up the MySQL database

Start your MySQL Server and open MySQL Workbench.

Create the database:

```sql
CREATE DATABASE skillsynch;
```

Select the database:

```sql
USE skillsynch;
```

Import the schema:

1. Open `database/SkillSynch_Final_Schema.sql` in MySQL Workbench.
2. Execute the SQL script.
3. Refresh the Schemas panel.
4. Verify the tables:

```sql
SHOW TABLES;
```

### 4. Configure database credentials

Create the following local file:

`src/main/resources/db-credentials.properties`

Add your MySQL connection details:

```properties
db.url=jdbc:mysql://localhost:3306/skillsynch
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```

Replace `YOUR_MYSQL_PASSWORD` with your local MySQL password.

**Security:** Do not commit this file or share your database password.

### 5. Run the application

Open the IntelliJ terminal in the project directory.

Execute:

```bash
mvn clean javafx:run
```

Alternatively, run the `Main.java` class directly through IntelliJ.

## Initial Admin Account

The College Admin account is created manually through MySQL Workbench.

Admin registration is not publicly available.

For password security, store a BCrypt hash in the `password_hash` column rather than a plain-text password.

## Database Tables

The SkillSynch database currently contains the following tables:

1. Companies
2. Students
3. Skills
4. Internships
5. Users
6. Student_Skills
7. Internship_Skills
8. Student_Languages
9. Internship_Languages
10. Eligibility
11. Applications
12. Offers

## Authentication and Security

- Passwords are hashed using BCrypt.
- Database operations use JDBC prepared statements.
- Registration uses database transactions to maintain data consistency.
- Database credentials are stored locally and excluded from version control.
- College Admin accounts are created separately from public registration.

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

Create a Pull Request on GitHub to merge your changes into the main branch.

## Future Enhancements

- Role-specific dashboards.
- Student profile and skill management.
- Company opportunity management.
- Automated eligibility calculation.
- Application tracking and shortlisting.
- Interview and offer management.
- Placement analytics and reporting.

---

**SkillSynch**  
A centralized platform for campus placement management.
