package com.skillsynch.ui;

import com.skillsynch.model.Application;
import com.skillsynch.model.ApplicationStats;
import com.skillsynch.model.Company;
import com.skillsynch.model.Internship;
import com.skillsynch.model.InternshipStatus;
import com.skillsynch.model.Student;
import com.skillsynch.service.AdminService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * UI layer only: builds JavaFX screens and delegates all data access,
 * validation and searching to AdminService.
 */
public class AdminDashboard {

    private final BorderPane root = new BorderPane();
    private final VBox content = new VBox(18);

    private final AdminService service = new AdminService();
    private Button activeNavButton;

    public Parent getView(Runnable logout) {

        // Sidebar
        VBox sidebar = new VBox(6);
        sidebar.setPadding(new Insets(22, 16, 22, 16));
        sidebar.setPrefWidth(230);
        sidebar.getStyleClass().add("sidebar");

        Label logo = new Label("SkillSynch");
        logo.getStyleClass().add("brand-title");

        Label portalLabel = new Label("Admin Portal");
        portalLabel.getStyleClass().add("brand-subtitle");

        VBox brandBox = new VBox(2, logo, portalLabel);
        brandBox.setPadding(new Insets(0, 6, 8, 6));

        Button dashboardBtn = createButton("Dashboard");
        Button studentsBtn = createButton("Manage Students");
        Button companiesBtn = createButton("Manage Companies");
        Button internshipsBtn = createButton("Approve Internships");
        Button manageInternshipsBtn = createButton("Manage Internships");
        Button applicationsBtn = createButton("Applications");
        Button reportsBtn = createButton("Placement Reports");
        Button skillsBtn = createButton("Manage Skills");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setAlignment(Pos.CENTER_LEFT);
        logoutBtn.getStyleClass().add("nav-logout");

        sidebar.getChildren().addAll(
                brandBox,
                new Separator(),
                dashboardBtn,
                studentsBtn,
                companiesBtn,
                skillsBtn,
                manageInternshipsBtn,
                internshipsBtn,
                applicationsBtn,
                reportsBtn,
                spacer,
                logoutBtn
        );

        // Main content
        content.setPadding(new Insets(28));
        content.getStyleClass().add("content-area");

        root.setLeft(sidebar);
        root.setCenter(content);

        // Navigation
        dashboardBtn.setOnAction(e -> { setActiveNav(dashboardBtn); showDashboard(); });
        skillsBtn.setOnAction(e -> { setActiveNav(skillsBtn); showManageSkills(); });
        studentsBtn.setOnAction(e -> { setActiveNav(studentsBtn); showManageStudents(); });
        companiesBtn.setOnAction(e -> { setActiveNav(companiesBtn); showManageCompanies(); });
        manageInternshipsBtn.setOnAction(e -> { setActiveNav(manageInternshipsBtn); showManageInternships(); });
        internshipsBtn.setOnAction(e -> { setActiveNav(internshipsBtn); showInternshipApprovals(); });
        applicationsBtn.setOnAction(e -> { setActiveNav(applicationsBtn); showApplications(); });
        reportsBtn.setOnAction(e -> { setActiveNav(reportsBtn); showPlacementReports(); });
        logoutBtn.setOnAction(e -> logout.run());

        setActiveNav(dashboardBtn);
        showDashboard();

        return root;
    }

    private void setActiveNav(Button button) {
        if (activeNavButton != null) {
            activeNavButton.getStyleClass().remove("nav-button-active");
            if (!activeNavButton.getStyleClass().contains("nav-button")) {
                activeNavButton.getStyleClass().add("nav-button");
            }
        }
        activeNavButton = button;
        activeNavButton.getStyleClass().remove("nav-button");
        if (!activeNavButton.getStyleClass().contains("nav-button-active")) {
            activeNavButton.getStyleClass().add("nav-button-active");
        }
    }

    private Button createButton(String text) {

        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add("nav-button");

        return button;
    }

    // DASHBOARD
    private void showDashboard() {

        content.getChildren().clear();

        Label heading = new Label("Admin Dashboard");
        heading.getStyleClass().add("hero-title");

        Label welcome = new Label("Welcome to SkillSynch Admin Panel!");
        welcome.getStyleClass().add("hero-subtitle");

        VBox banner = new VBox(6, heading, welcome);
        banner.setPadding(new Insets(24));
        banner.getStyleClass().add("hero-banner");

        HBox cards = new HBox(16);

        cards.getChildren().addAll(
                createCard("Total Students", "0"),
                createCard("Total Companies", "0"),
                createCard("Internships", "0")
        );

        content.getChildren().addAll(banner, cards);
    }

    private VBox createCard(String title, String value) {

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        VBox card = new VBox(10, titleLabel, valueLabel);

        card.setPadding(new Insets(22));
        card.setPrefWidth(220);
        card.getStyleClass().add("stat-card");

        return card;
    }

    private Label createHeading(String title) {

        Label heading = new Label(title);
        heading.getStyleClass().add("heading");

        return heading;
    }

    private void showPage(String title, String description) {

        content.getChildren().clear();

        Label heading = createHeading(title);
        Label message = new Label(description);
        message.getStyleClass().add("subheading");

        content.getChildren().addAll(heading, message);
    }

    // APPROVE INTERNSHIPS
    private void showInternshipApprovals() {

        content.getChildren().clear();

        Label heading = createHeading("Approve Internships");

        Label message = new Label("Review and manage internship requests.");
        message.getStyleClass().add("subheading");

        VBox internshipList = new VBox(14);

        try {

            List<Internship> internships = service.getAllInternships();

            if (internships.isEmpty()) {

                internshipList.getChildren().add(
                        new Label("No internships available yet.")
                );

            } else {

                for (Internship internship : internships) {

                    int internshipId = internship.getInternId();

                    String companyName = internship.getCompanyName();
                    String role = internship.getRole();
                    String description = internship.getDescription() == null
                            ? "No description" : internship.getDescription();
                    InternshipStatus status = internship.getStatus();

                    Label roleLabel = new Label(role);
                    roleLabel.getStyleClass().add("card-title");

                    Label companyLabel = new Label("Company: " + companyName);
                    companyLabel.getStyleClass().add("body-text");

                    Label descriptionLabel = new Label("Description: " + description);
                    descriptionLabel.getStyleClass().add("body-text");
                    descriptionLabel.setWrapText(true);

                    Label cgpaLabel = new Label(
                            "Minimum CGPA: " + (internship.getMinCgpa() == null
                                    ? "Not specified" : internship.getMinCgpa())
                    );
                    cgpaLabel.getStyleClass().add("body-text");

                    Label positionsLabel = new Label(
                            "Positions: " + internship.getNumberOfPositions()
                    );
                    positionsLabel.getStyleClass().add("body-text");

                    Label deadlineLabel = new Label(
                            "Application Deadline: " + (internship.getApplicationDeadline() == null
                                    ? "Not specified" : internship.getApplicationDeadline())
                    );
                    deadlineLabel.getStyleClass().add("muted-text");

                    Label statusLabel = new Label("Status: " + status);
                    statusLabel.getStyleClass().add("status-badge");

                    HBox buttons = new HBox(10);

                    if (status == InternshipStatus.PENDING) {

                        Button approveBtn = new Button("Approve");
                        approveBtn.getStyleClass().add("primary-button");

                        Button rejectBtn = new Button("Reject");
                        rejectBtn.getStyleClass().add("secondary-button");

                        approveBtn.setOnAction(e ->
                                updateInternshipStatus(internshipId, InternshipStatus.APPROVED)
                        );

                        rejectBtn.setOnAction(e ->
                                updateInternshipStatus(internshipId, InternshipStatus.REJECTED)
                        );

                        buttons.getChildren().addAll(approveBtn, rejectBtn);
                    }

                    VBox internshipCard = new VBox(
                            8,
                            roleLabel,
                            companyLabel,
                            descriptionLabel,
                            cgpaLabel,
                            positionsLabel,
                            deadlineLabel,
                            statusLabel,
                            buttons
                    );

                    internshipCard.setPadding(new Insets(20));
                    internshipCard.getStyleClass().add("card");

                    internshipList.getChildren().add(internshipCard);
                }
            }

        } catch (Exception e) {

            internshipList.getChildren().add(
                    new Label("Error loading internships: " + e.getMessage())
            );
        }

        ScrollPane scrollPane = new ScrollPane(internshipList);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(heading, message, scrollPane);
    }

    private void updateInternshipStatus(int internshipId, InternshipStatus newStatus) {

        try {

            service.updateInternshipStatus(internshipId, newStatus);

            showInternshipApprovals();

        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Internship Update Failed");
            alert.setHeaderText("Could not update internship status");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // PLACEMENT REPORTS
    private void showPlacementReports() {

        content.getChildren().clear();

        Label heading = createHeading("Placement Reports");

        try {

            ApplicationStats stats = service.getApplicationStatistics();

            FlowPane cards = new FlowPane(16, 16);

            cards.getChildren().addAll(
                    createCard(
                            "Total Applications",
                            String.valueOf(stats.getTotalApplications())
                    ),
                    createCard(
                            "Selected Students",
                            String.valueOf(stats.getSelected())
                    ),
                    createCard(
                            "Shortlisted Students",
                            String.valueOf(stats.getShortlisted())
                    ),
                    createCard(
                            "Rejected Applications",
                            String.valueOf(stats.getRejected())
                    ),
                    createCard(
                            "Offers Extended",
                            String.valueOf(stats.getOffersExtended())
                    )
            );

            content.getChildren().addAll(heading, cards);

        } catch (Exception e) {

            Label errorLabel = new Label(
                    "Error loading placement reports: " + e.getMessage()
            );

            content.getChildren().addAll(heading, errorLabel);

            e.printStackTrace();
        }
    }

    // MANAGE SKILLS
    private void showManageSkills() {

        content.getChildren().clear();

        Label heading = createHeading("Manage Skills");

        Label description = new Label(
                "Add new skills that students can select from their dashboard."
        );
        description.getStyleClass().add("subheading");

        TextField skillNameField = new TextField();
        skillNameField.setPromptText("Enter Skill Name");
        skillNameField.setMaxWidth(Double.MAX_VALUE);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Enter Category");
        categoryField.setMaxWidth(Double.MAX_VALUE);

        Button addSkillBtn = new Button("Add Skill");
        addSkillBtn.getStyleClass().add("primary-button");

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("feedback-message");

        addSkillBtn.setOnAction(e -> {

            try {

                service.addSkill(skillNameField.getText(), categoryField.getText());

                statusLabel.setText("Skill added successfully!");

                skillNameField.clear();
                categoryField.clear();

            } catch (AdminService.ValidationException ex) {

                statusLabel.setText(ex.getMessage());

            } catch (Exception ex) {

                ex.printStackTrace();
                statusLabel.setText("Error adding skill: " + ex.getMessage());
            }
        });

        Label skillNameLabel = new Label("Skill Name");
        skillNameLabel.getStyleClass().add("field-label");
        Label categoryLabel = new Label("Category");
        categoryLabel.getStyleClass().add("field-label");

        VBox form = new VBox(
                12,
                skillNameLabel,
                skillNameField,
                categoryLabel,
                categoryField,
                addSkillBtn,
                statusLabel
        );

        form.setPadding(new Insets(24));
        form.setMaxWidth(520);
        form.getStyleClass().add("card");

        content.getChildren().addAll(heading, description, form);
    }

    // MANAGE COMPANIES
    private void showManageCompanies() {

        content.getChildren().clear();

        Label heading = createHeading("Manage Companies");

        TextField companyNameField = new TextField();
        companyNameField.setPromptText("Enter company name");

        TextField industryField = new TextField();
        industryField.setPromptText("Enter industry");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Enter company description");
        descriptionField.setPrefRowCount(3);

        Button addButton = new Button("Add Company");
        addButton.getStyleClass().add("primary-button");

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("feedback-message");

        Label nameLabel = new Label("Company Name");
        nameLabel.getStyleClass().add("field-label");
        Label industryLabel = new Label("Industry");
        industryLabel.getStyleClass().add("field-label");
        Label descLabel = new Label("Description");
        descLabel.getStyleClass().add("field-label");

        VBox form = new VBox(10,
                nameLabel,
                companyNameField,
                industryLabel,
                industryField,
                descLabel,
                descriptionField,
                addButton,
                messageLabel
        );

        form.setPadding(new Insets(22));
        form.setMaxWidth(560);
        form.getStyleClass().add("card");

        Label listHeading = new Label("Registered Companies");
        listHeading.getStyleClass().add("section-heading");

        VBox companyList = new VBox(10);

        Runnable loadCompanies = () -> {

            companyList.getChildren().clear();

            try {

                List<Company> companies = service.getAllCompanies();

                if (companies.isEmpty()) {
                    companyList.getChildren().add(
                            new Label("No companies added yet.")
                    );
                    return;
                }

                for (Company company : companies) {

                    String industryText = company.getIndustry() == null
                            ? "N/A" : company.getIndustry();

                    Label companyLabel = new Label(
                            company.getCompanyName() + " - " + industryText
                    );
                    companyLabel.getStyleClass().add("body-text");

                    VBox companyRow = new VBox(companyLabel);
                    companyRow.setPadding(new Insets(12, 16, 12, 16));
                    companyRow.getStyleClass().add("card");

                    companyList.getChildren().add(companyRow);
                }

            } catch (Exception e) {
                messageLabel.setText("Error loading companies: " + e.getMessage());
            }
        };

        addButton.setOnAction(e -> {

            try {

                service.addCompany(
                        companyNameField.getText(),
                        industryField.getText(),
                        descriptionField.getText()
                );

                messageLabel.setText("Company added successfully!");

                companyNameField.clear();
                industryField.clear();
                descriptionField.clear();

                loadCompanies.run();

            } catch (AdminService.ValidationException ex) {
                messageLabel.setText(ex.getMessage());
            } catch (Exception ex) {
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });

        VBox pageBody = new VBox(16, form, listHeading, companyList);
        ScrollPane scrollPane = new ScrollPane(pageBody);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(
                heading,
                scrollPane
        );

        loadCompanies.run();
    }

    // MANAGE INTERNSHIPS
    private void showManageInternships() {

        content.getChildren().clear();

        Label heading = createHeading("Manage Internships");

        // Company dropdown
        ComboBox<Company> companyDropdown = new ComboBox<>();
        companyDropdown.setPromptText("Select a company");
        companyDropdown.setMaxWidth(Double.MAX_VALUE);

        companyDropdown.setConverter(new StringConverter<>() {
            @Override
            public String toString(Company company) {
                return company == null ? "" : company.getCompanyName();
            }

            @Override
            public Company fromString(String string) {
                return null;
            }
        });

        // Internship fields
        TextField roleField = new TextField();
        roleField.setPromptText("Enter job role");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Enter internship description");
        descriptionField.setPrefRowCount(3);

        TextField cgpaField = new TextField();
        cgpaField.setPromptText("Minimum CGPA (0 to 10)");

        TextField positionsField = new TextField();
        positionsField.setPromptText("Number of positions");

        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Application deadline");
        deadlinePicker.setMaxWidth(Double.MAX_VALUE);

        Button addButton = new Button("Add Internship");
        addButton.getStyleClass().add("primary-button");

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("feedback-message");

        Label companyLbl = new Label("Select Company");
        companyLbl.getStyleClass().add("field-label");
        Label roleLbl = new Label("Job Role");
        roleLbl.getStyleClass().add("field-label");
        Label descLbl = new Label("Description");
        descLbl.getStyleClass().add("field-label");
        Label cgpaLbl = new Label("Minimum CGPA");
        cgpaLbl.getStyleClass().add("field-label");
        Label posLbl = new Label("Number of Positions");
        posLbl.getStyleClass().add("field-label");
        Label deadlineLbl = new Label("Application Deadline");
        deadlineLbl.getStyleClass().add("field-label");

        VBox form = new VBox(10,
                companyLbl,
                companyDropdown,
                roleLbl,
                roleField,
                descLbl,
                descriptionField,
                cgpaLbl,
                cgpaField,
                posLbl,
                positionsField,
                deadlineLbl,
                deadlinePicker,
                addButton,
                messageLabel
        );

        form.setPadding(new Insets(24));
        form.setMaxWidth(580);
        form.getStyleClass().add("card");

        // Load companies
        try {

            companyDropdown.getItems().addAll(service.getAllCompanies());

        } catch (Exception e) {
            messageLabel.setText("Error loading companies: " + e.getMessage());
        }

        // Add internship
        addButton.setOnAction(e -> {

            try {

                service.addInternship(
                        companyDropdown.getValue(),
                        roleField.getText(),
                        descriptionField.getText(),
                        cgpaField.getText(),
                        positionsField.getText(),
                        deadlinePicker.getValue()
                );

                messageLabel.setText("Internship added successfully! Status: Pending");

                companyDropdown.setValue(null);
                roleField.clear();
                descriptionField.clear();
                cgpaField.clear();
                positionsField.clear();
                deadlinePicker.setValue(null);

            } catch (AdminService.ValidationException ex) {

                messageLabel.setText(ex.getMessage());

            } catch (Exception ex) {

                messageLabel.setText("Error: " + ex.getMessage());
            }
        });

        ScrollPane scrollPane = new ScrollPane(form);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(heading, scrollPane);
    }

    // MANAGE STUDENTS
    private void showManageStudents() {

        content.getChildren().clear();

        Label heading = createHeading("Manage Students");

        Label description = new Label("View and search all registered students.");
        description.getStyleClass().add("subheading");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by student name or email...");
        searchField.setMaxWidth(420);

        VBox studentList = new VBox(14);

        List<Student> allStudents = new ArrayList<>();

        // Load students
        try {

            allStudents.addAll(service.getAllStudents());

        } catch (Exception e) {

            studentList.getChildren().add(
                    new Label("Error loading students: " + e.getMessage())
            );
        }

        // Display (filtered) students
        Runnable displayStudents = () -> {

            studentList.getChildren().clear();

            List<Student> matches =
                    service.filterStudents(allStudents, searchField.getText());

            for (Student student : matches) {

                String name = student.getName();
                String email = student.getEmail() == null ? "Not provided" : student.getEmail();

                Label nameLabel = new Label(name);
                nameLabel.getStyleClass().add("card-title");

                Label idLabel = new Label("Student ID: " + student.getStudentId());
                idLabel.getStyleClass().add("muted-text");

                Label emailLabel = new Label("Email: " + email);
                emailLabel.getStyleClass().add("body-text");

                Label branchLabel = new Label("Branch: " + student.getBranch());
                branchLabel.getStyleClass().add("body-text");

                Label yearLabel = new Label("Year: " + (student.getYear() == null ? "N/A" : student.getYear()));
                yearLabel.getStyleClass().add("body-text");

                Label cgpaLabel = new Label("CGPA: " + (student.getCgpa() == null ? "N/A" : student.getCgpa()));
                cgpaLabel.getStyleClass().add("body-text");

                Label resumeLabel = new Label(
                        "Resume: " + (
                                student.getResumeUrl() == null || student.getResumeUrl().isBlank()
                                        ? "Not uploaded"
                                        : student.getResumeUrl()
                        )
                );
                resumeLabel.getStyleClass().add("muted-text");

                int studentId = student.getStudentId();

                Button deleteBtn = new Button("Delete Student");
                deleteBtn.getStyleClass().add("secondary-button");

                deleteBtn.setOnAction(e -> {

                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);

                    confirmation.setTitle("Delete Student");
                    confirmation.setHeaderText("Delete " + name + "?");
                    confirmation.setContentText(
                            "This action cannot be undone. Continue?"
                    );

                    confirmation.showAndWait().ifPresent(response -> {

                        if (response == ButtonType.OK) {

                            try {

                                service.deleteStudent(studentId);

                                showManageStudents();

                                Alert success = new Alert(Alert.AlertType.INFORMATION);
                                success.setTitle("Success");
                                success.setHeaderText(null);
                                success.setContentText("Student deleted successfully!");
                                success.showAndWait();

                            } catch (Exception ex) {

                                Alert error = new Alert(Alert.AlertType.WARNING);
                                error.setTitle("Cannot Delete Student");
                                error.setHeaderText("Student deletion failed");
                                error.setContentText(
                                        "This student may have existing applications. " +
                                                "Their application history must be preserved.\n\n" +
                                                ex.getMessage()
                                );
                                error.showAndWait();
                            }
                        }
                    });
                });

                VBox studentCard = new VBox(
                        8,
                        nameLabel,
                        idLabel,
                        emailLabel,
                        branchLabel,
                        yearLabel,
                        cgpaLabel,
                        resumeLabel,
                        deleteBtn
                );

                studentCard.setPadding(new Insets(20));
                studentCard.getStyleClass().add("card");

                studentList.getChildren().add(studentCard);
            }

            if (matches.isEmpty()) {
                studentList.getChildren().add(
                        new Label("No matching students found.")
                );
            }
        };

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> displayStudents.run()
        );

        ScrollPane scrollPane = new ScrollPane(studentList);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(
                heading,
                description,
                searchField,
                scrollPane
        );

        displayStudents.run();
    }

    // ALL APPLICATIONS
    private void showApplications() {

        content.getChildren().clear();

        Label heading = createHeading("All Student Applications");

        Label description = new Label(
                "Monitor applications submitted by students across all companies."
        );
        description.getStyleClass().add("subheading");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by student name, email or company...");
        searchField.setMaxWidth(450);

        VBox applicationList = new VBox(14);

        List<Application> allApplications = new ArrayList<>();

        try {

            allApplications.addAll(service.getAllApplications());

        } catch (Exception e) {
            applicationList.getChildren().add(
                    new Label("Error loading applications: " + e.getMessage())
            );
        }

        Runnable displayApplications = () -> {

            applicationList.getChildren().clear();

            List<Application> matches =
                    service.filterApplications(allApplications, searchField.getText());

            for (Application application : matches) {

                Label studentLabel = new Label(
                        "Student: " + application.getStudentName()
                );
                studentLabel.getStyleClass().add("card-title");

                Label idLabel = new Label(
                        "Application ID: " + application.getApplicationId()
                );
                idLabel.getStyleClass().add("muted-text");

                Label emailLabel = new Label("Email: " + (application.getStudentEmail() == null ? "N/A" : application.getStudentEmail()));
                emailLabel.getStyleClass().add("body-text");

                Label branchLabel = new Label("Branch: " + application.getBranch());
                branchLabel.getStyleClass().add("body-text");

                Label cgpaLabel = new Label("CGPA: " + (application.getCgpa() == null ? "N/A" : application.getCgpa()));
                cgpaLabel.getStyleClass().add("body-text");

                Label companyLabel = new Label("Company: " + application.getCompanyName());
                companyLabel.getStyleClass().add("body-text");

                Label roleLabel = new Label(
                        "Internship Role: " + application.getRole()
                );
                roleLabel.getStyleClass().add("body-text");

                Label dateLabel = new Label(
                        "Application Date: " + application.getApplicationDate()
                );
                dateLabel.getStyleClass().add("muted-text");

                Label statusLabel = new Label("Status: " + application.getStatus());
                statusLabel.getStyleClass().add("status-badge");

                VBox card = new VBox(
                        8,
                        studentLabel,
                        idLabel,
                        emailLabel,
                        branchLabel,
                        cgpaLabel,
                        companyLabel,
                        roleLabel,
                        dateLabel,
                        statusLabel
                );

                card.setPadding(new Insets(20));
                card.getStyleClass().add("card");

                applicationList.getChildren().add(card);
            }

            if (matches.isEmpty()) {
                applicationList.getChildren().add(
                        new Label("No matching applications found.")
                );
            }
        };

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> displayApplications.run()
        );

        ScrollPane scrollPane = new ScrollPane(applicationList);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(
                heading,
                description,
                searchField,
                scrollPane
        );

        displayApplications.run();
    }
}