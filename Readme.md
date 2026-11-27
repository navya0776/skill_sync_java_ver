

# SkillSynch – Placement Management System

SkillSynch is a Java-based Placement Management System developed to simplify and organize the campus recruitment process. It connects students, companies, and college administrators through a centralized desktop application.

The system supports the complete placement workflow, including internship posting, administrator approval, student applications, candidate shortlisting, selection, and placement offer management.

## Project Repository

**GitHub:** https://github.com/navya0776/skill_sync_java_ver

## Technology Stack

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

### 1. Student Module
- Student registration and secure login.
- View and update personal and academic profile information.
- Upload, view, and download resumes.
- Add skills and proficiency levels.
- Browse approved internship opportunities.
- Apply for internships.
- Track application status.
- View internship eligibility and matching information.
- View, accept, or reject placement offers.

### 2. Company Module
- Company registration and login.
- Manage company profile.
- Post internship opportunities with eligibility requirements.
- Specify required skills and minimum proficiency levels.
- Submit internships for administrator approval.
- View posted internships and their approval status.
- Review student applications.
- Shortlist and select candidates.
- Create placement offers.
- Track student offer responses.

### 3. College Administrator Module
- Secure administrator login.
- View dashboard statistics.
- Manage student records.
- Manage company records.
- Add and manage skills.
- Manage internship postings.
- Approve or reject internship requests.
- View and monitor student applications.
- Monitor placement offers.
- View placement reports and statistics.

## Application Workflow

1. Students and companies register and log in.
2. Companies create internship opportunities.
3. Administrators review and approve or reject internship postings.
4. Students browse approved opportunities and apply.
5. Companies review applications and update candidate statuses.
6. Companies shortlist and select suitable candidates.
7. Selected candidates receive placement offers.
8. Students accept or reject the offers.
9. Companies and administrators monitor the final placement status.

## Project Structure

```text
SkillSynch/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    └── main/
        ├── java/com/skillsynch/
        │   ├── Main.java
        │   ├── DatabaseConnectionTest.java
        │   ├── GenerateAdminHash.java
        │   ├── config/
        │   │   └── DatabaseConfig.java
        │   ├── dao/
        │   │   ├── ApplicationDAO.java
        │   │   ├── CompanyDAO.java
        │   │   ├── EligibilityDAO.java
        │   │   ├── InternshipDAO.java
        │   │   ├── SkillDAO.java
        │   │   ├── StudentDAO.java
        │   │   └── UserDAO.java
        │   ├── model/
        │   │   └── User.java
        │   ├── service/
        │   │   ├── AuthService.java
        │   │   └── RegistrationService.java
        │   └── ui/
        │       ├── AdminDashboard.java
        │       ├── CompanyDashboard.java
        │       ├── LoginView.java
        │       ├── RegisterView.java
        │       └── StudentDashboard.java
        └── resources/
            └── css/
                └── style.css
```

## Setup and Execution

### Prerequisites

- JDK 17
- Maven
- MySQL Server 8.x
- IntelliJ IDEA

### 1. Clone the Repository

```bash
git clone https://github.com/navya0776/skill_sync_java_ver.git
```

Open the project in IntelliJ IDEA.

### 2. Database Configuration

1. Create the MySQL database named `skillsynch`.
2. Create the required database tables and constraints.
3. Create a local file named `db-credentials.properties` inside `src/main/resources/`.
4. Add your database configuration:

```properties
db.url=jdbc:mysql://localhost:3306/skillsynch
db.username=YOUR_MYSQL_USERNAME
db.password=YOUR_MYSQL_PASSWORD
```

Replace the placeholders with your actual MySQL credentials.

**Important:** Do not upload database passwords or credentials to GitHub.

### 3. Build the Project

Run the following command from the project root:

```bash
mvn clean compile
```

### 4. Run the Application

Run `com.skillsynch.Main` from IntelliJ IDEA.

Alternatively, use:

```bash
mvn javafx:run
```

## Security

- Passwords are securely hashed using jBCrypt.
- Database operations use JDBC prepared statements.
- Database credentials are maintained locally.
- Role-based authentication separates student, company, and administrator access.
- Input validation is applied during registration and authentication.

## Project Contributors

1. Vidhi Sidana
2. Navya Arora

