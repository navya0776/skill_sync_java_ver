package com.skillsynch.ui;
import java.util.Locale;
import com.skillsynch.dao.InternshipDAO;
import javafx.util.StringConverter;
import com.skillsynch.dao.StudentDAO;
import com.skillsynch.dao.CompanyDAO;
import java.util.List;
import java.util.Map;
import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.dao.SkillDAO;
import java.sql.Connection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdminDashboard {

    private final BorderPane root = new BorderPane();
    private final VBox content = new VBox(20);

    public Parent getView(Runnable logout) {

        // Sidebar
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(25));
        sidebar.setPrefWidth(230);
        sidebar.setStyle("-fx-background-color: #1E293B;");

        Label logo = new Label("SkillSynch");
        logo.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 25px;" +
                        "-fx-font-weight: bold;"
        );

        Button dashboardBtn = createButton("Dashboard");
        Button studentsBtn = createButton("Manage Students");
        Button companiesBtn = createButton("Manage Companies");
        Button internshipsBtn = createButton("Approve Internships");
        Button manageInternshipsBtn = createButton("Manage Internships");
        Button applicationsBtn = createButton("Applications");
        Button reportsBtn = createButton("Placement Reports");
        Button logoutBtn = createButton("Logout");
        Button skillsBtn = createButton("Manage Skills");
        sidebar.getChildren().addAll(
                logo,
                new Separator(),
                dashboardBtn,
                studentsBtn,
                companiesBtn,
                skillsBtn,
                manageInternshipsBtn,
                internshipsBtn,
                applicationsBtn,
                reportsBtn,
                logoutBtn
        );

        // Main content
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #F8FAFC;");

        root.setLeft(sidebar);
        root.setCenter(content);

        // Navigation
        dashboardBtn.setOnAction(e -> showDashboard());
        skillsBtn.setOnAction(e -> showManageSkills());
        studentsBtn.setOnAction(e -> showManageStudents());

        companiesBtn.setOnAction(e ->
                showManageCompanies()
        );
        manageInternshipsBtn.setOnAction(e -> showManageInternships());

        internshipsBtn.setOnAction(e -> showInternshipApprovals());
        applicationsBtn.setOnAction(e ->
                showPage("Applications",
                        "Monitor student internship applications.")
        );

        reportsBtn.setOnAction(e ->
                showPlacementReports()
        );

        logoutBtn.setOnAction(e -> logout.run());

        showDashboard();

        return root;
    }

    private Button createButton(String text) {

        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);

        button.setStyle(
                "-fx-background-color: #334155;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 12px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: hand;"
        );

        return button;
    }

    private void showDashboard() {

        content.getChildren().clear();

        Label heading = createHeading("Admin Dashboard");
        Label welcome = new Label("Welcome to SkillSynch Admin Panel!");

        HBox cards = new HBox(20);

        cards.getChildren().addAll(
                createCard("Total Students", "0"),
                createCard("Total Companies", "0"),
                createCard("Internships", "0")
        );

        content.getChildren().addAll(
                heading,
                welcome,
                cards
        );
    }

    private VBox createCard(String title, String value) {

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
                "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #4F46E5;"
        );

        VBox card = new VBox(15, titleLabel, valueLabel);

        card.setPadding(new Insets(25));
        card.setPrefWidth(220);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 12px;"
        );

        return card;
    }

    private Label createHeading(String title) {

        Label heading = new Label(title);

        heading.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        return heading;
    }

    private void showPage(String title, String description) {

        content.getChildren().clear();

        Label heading = createHeading(title);
        Label message = new Label(description);

        message.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #64748B;"
        );

        content.getChildren().addAll(heading, message);
    }


    private void showInternshipApprovals() {

        content.getChildren().clear();

        Label heading = createHeading("Approve Internships");

        Label message = new Label(
                "Review and manage internship requests."
        );

        VBox internshipList = new VBox(15);

        try (Connection connection = DatabaseConfig.getConnection()) {

            InternshipDAO internshipDAO = new InternshipDAO();

            List<Map<String, Object>> internships =
                    internshipDAO.getAllInternships(connection);

            if (internships.isEmpty()) {

                internshipList.getChildren().add(
                        new Label("No internships available yet.")
                );

            } else {

                for (Map<String, Object> internship : internships) {

                    int internshipId =
                            ((Number) internship.get("intern_id")).intValue();

                    String companyName =
                            String.valueOf(internship.get("company_name"));

                    String role =
                            String.valueOf(internship.get("role"));

                    String description =
                            String.valueOf(internship.get("description"));

                    String status =
                            String.valueOf(internship.get("status"));

                    Label roleLabel = new Label(role);
                    roleLabel.setStyle(
                            "-fx-font-size: 18px; -fx-font-weight: bold;"
                    );

                    Label companyLabel = new Label(
                            "Company: " + companyName
                    );

                    Label descriptionLabel = new Label(
                            "Description: " + description
                    );

                    Label cgpaLabel = new Label(
                            "Minimum CGPA: " + internship.get("min_cgpa")
                    );

                    Label positionsLabel = new Label(
                            "Positions: " +
                                    internship.get("number_of_positions")
                    );

                    Label deadlineLabel = new Label(
                            "Application Deadline: " +
                                    internship.get("application_deadline")
                    );

                    Label statusLabel = new Label(
                            "Status: " + status
                    );

                    HBox buttons = new HBox(15);

                    if (status.equals("Pending")) {

                        Button approveBtn = new Button("Approve");
                        Button rejectBtn = new Button("Reject");

                        approveBtn.setStyle(
                                "-fx-background-color: #16A34A;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-padding: 10px;"
                        );

                        rejectBtn.setStyle(
                                "-fx-background-color: #DC2626;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-padding: 10px;"
                        );

                        approveBtn.setOnAction(e -> {

                            updateInternshipStatus(
                                    internshipId,
                                    "Approved"
                            );

                        });

                        rejectBtn.setOnAction(e -> {

                            updateInternshipStatus(
                                    internshipId,
                                    "Rejected"
                            );

                        });

                        buttons.getChildren().addAll(
                                approveBtn,
                                rejectBtn
                        );
                    }

                    VBox internshipCard = new VBox(
                            10,
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

                    internshipCard.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 10px;"
                    );

                    internshipList.getChildren().add(internshipCard);
                }
            }

        } catch (Exception e) {

            internshipList.getChildren().add(
                    new Label("Error loading internships: " + e.getMessage())
            );
        }

        content.getChildren().addAll(
                heading,
                message,
                internshipList
        );
    }

    private void updateInternshipStatus(
            int internshipId,
            String newStatus
    ) {

        try (Connection connection = DatabaseConfig.getConnection()) {

            InternshipDAO internshipDAO = new InternshipDAO();

            internshipDAO.updateInternshipStatus(
                    connection,
                    internshipId,
                    newStatus
            );

            showInternshipApprovals();

        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Internship Update Failed");
            alert.setHeaderText("Could not update internship status");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    private void showPlacementReports() {

        content.getChildren().clear();

        Label heading = createHeading("Placement Reports");

        HBox cards = new HBox(20);

        cards.getChildren().addAll(
                createCard("Total Applications", "0"),
                createCard("Selected Students", "0"),
                createCard("Offers Extended", "0")
        );

        content.getChildren().addAll(
                heading,
                cards
        );
    } // End of showPlacementReports()


    private void showManageSkills() {

        content.getChildren().clear();

        Label heading = createHeading("Manage Skills");

        Label description = new Label(
                "Add new skills that students can select from their dashboard."
        );

        TextField skillNameField = new TextField();
        skillNameField.setPromptText("Enter Skill Name");
        skillNameField.setMaxWidth(350);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Enter Category");
        categoryField.setMaxWidth(350);

        Button addSkillBtn = new Button("Add Skill");

        Label statusLabel = new Label();

        addSkillBtn.setOnAction(e -> {

            String skillName = skillNameField.getText().trim();
            String category = categoryField.getText().trim();

            if (skillName.isEmpty() || category.isEmpty()) {
                statusLabel.setText("Please fill in both fields.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            try (Connection conn = DatabaseConfig.getConnection()) {

                SkillDAO skillDAO = new SkillDAO();
                skillDAO.addSkill(conn, skillName, category);

                statusLabel.setText("Skill added successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");

                skillNameField.clear();
                categoryField.clear();

            } catch (Exception ex) {

                ex.printStackTrace();
                statusLabel.setText("Error adding skill: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        VBox form = new VBox(
                15,
                new Label("Skill Name"),
                skillNameField,
                new Label("Category"),
                categoryField,
                addSkillBtn,
                statusLabel
        );

        form.setPadding(new Insets(25));
        form.setMaxWidth(450);

        form.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 12px;"
        );

        content.getChildren().addAll(
                heading,
                description,
                form
        );
    }
    private void showManageCompanies() {

        content.getChildren().clear();

        Label heading = new Label("Manage Companies");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField companyNameField = new TextField();
        companyNameField.setPromptText("Enter company name");

        TextField industryField = new TextField();
        industryField.setPromptText("Enter industry");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Enter company description");
        descriptionField.setPrefRowCount(3);

        Button addButton = new Button("Add Company");

        Label messageLabel = new Label();

        VBox form = new VBox(10,
                new Label("Company Name"),
                companyNameField,
                new Label("Industry"),
                industryField,
                new Label("Description"),
                descriptionField,
                addButton,
                messageLabel
        );

        form.setPadding(new Insets(15));
        form.setMaxWidth(500);

        Label listHeading = new Label("Registered Companies");
        listHeading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox companyList = new VBox(10);

        Runnable loadCompanies = () -> {

            companyList.getChildren().clear();

            try (Connection connection = DatabaseConfig.getConnection()) {

                CompanyDAO companyDAO = new CompanyDAO();

                List<Map<String, Object>> companies =
                        companyDAO.getAllCompanies(connection);

                if (companies.isEmpty()) {
                    companyList.getChildren().add(
                            new Label("No companies added yet.")
                    );
                    return;
                }

                for (Map<String, Object> company : companies) {

                    Label companyLabel = new Label(
                            company.get("company_name") + " - "
                                    + company.get("industry")
                    );

                    companyLabel.setStyle(
                            "-fx-font-size: 14px; -fx-padding: 8px;"
                    );

                    companyList.getChildren().add(companyLabel);
                }

            } catch (Exception e) {
                messageLabel.setText("Error loading companies: " + e.getMessage());
            }
        };

        addButton.setOnAction(e -> {

            String companyName = companyNameField.getText().trim();
            String industry = industryField.getText().trim();
            String description = descriptionField.getText().trim();

            if (companyName.isEmpty() || industry.isEmpty()) {
                messageLabel.setText("Company name and industry are required.");
                return;
            }

            try (Connection connection = DatabaseConfig.getConnection()) {

                CompanyDAO companyDAO = new CompanyDAO();

                companyDAO.createCompany(
                        connection,
                        companyName,
                        industry,
                        description
                );

                messageLabel.setText("Company added successfully!");

                companyNameField.clear();
                industryField.clear();
                descriptionField.clear();

                loadCompanies.run();

            } catch (Exception ex) {
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(
                heading,
                form,
                listHeading,
                companyList
        );

        loadCompanies.run();
    }

    private void showManageInternships() {

        content.getChildren().clear();

        Label heading = new Label("Manage Internships");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Company dropdown
        ComboBox<Map<String, Object>> companyDropdown = new ComboBox<>();
        companyDropdown.setPromptText("Select a company");
        companyDropdown.setMaxWidth(Double.MAX_VALUE);

        companyDropdown.setConverter(new StringConverter<>() {
            @Override
            public String toString(Map<String, Object> company) {
                return company == null ? "" :
                        company.get("company_name").toString();
            }

            @Override
            public Map<String, Object> fromString(String string) {
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

        Button addButton = new Button("Add Internship");
        Label messageLabel = new Label();

        VBox form = new VBox(10,
                new Label("Select Company"),
                companyDropdown,
                new Label("Job Role"),
                roleField,
                new Label("Description"),
                descriptionField,
                new Label("Minimum CGPA"),
                cgpaField,
                new Label("Number of Positions"),
                positionsField,
                new Label("Application Deadline"),
                deadlinePicker,
                addButton,
                messageLabel
        );

        form.setPadding(new Insets(15));
        form.setMaxWidth(500);

        // Load companies from database
        try (Connection connection = DatabaseConfig.getConnection()) {

            CompanyDAO companyDAO = new CompanyDAO();

            List<Map<String, Object>> companies =
                    companyDAO.getAllCompanies(connection);

            companyDropdown.getItems().addAll(companies);

        } catch (Exception e) {
            messageLabel.setText(
                    "Error loading companies: " + e.getMessage()
            );
        }

        // Add internship
        addButton.setOnAction(e -> {

            Map<String, Object> selectedCompany =
                    companyDropdown.getValue();

            String role = roleField.getText().trim();
            String description = descriptionField.getText().trim();

            if (selectedCompany == null ||
                    role.isEmpty() ||
                    description.isEmpty() ||
                    cgpaField.getText().trim().isEmpty() ||
                    positionsField.getText().trim().isEmpty() ||
                    deadlinePicker.getValue() == null) {

                messageLabel.setText("Please fill in all fields.");
                return;
            }

            try {

                double minCgpa =
                        Double.parseDouble(cgpaField.getText().trim());

                int positions =
                        Integer.parseInt(positionsField.getText().trim());

                if (minCgpa < 0 || minCgpa > 10) {
                    messageLabel.setText("CGPA must be between 0 and 10.");
                    return;
                }

                if (positions <= 0) {
                    messageLabel.setText(
                            "Number of positions must be greater than 0."
                    );
                    return;
                }

                int companyId =
                        ((Number) selectedCompany.get("company_id")).intValue();

                String companyName =
                        selectedCompany.get("company_name").toString();

                java.sql.Date deadline =
                        java.sql.Date.valueOf(deadlinePicker.getValue());

                try (Connection connection =
                             DatabaseConfig.getConnection()) {

                    InternshipDAO internshipDAO = new InternshipDAO();

                    internshipDAO.createInternship(
                            connection,
                            companyId,
                            companyName,
                            role,
                            description,
                            minCgpa,
                            positions,
                            deadline
                    );
                }

                messageLabel.setText(
                        "Internship added successfully! Status: Pending"
                );

                companyDropdown.setValue(null);
                roleField.clear();
                descriptionField.clear();
                cgpaField.clear();
                positionsField.clear();
                deadlinePicker.setValue(null);

            } catch (NumberFormatException ex) {

                messageLabel.setText(
                        "Enter valid numbers for CGPA and positions."
                );

            } catch (Exception ex) {

                messageLabel.setText(
                        "Error: " + ex.getMessage()
                );
            }
        });

        content.getChildren().addAll(heading, form);
    }
    private void showManageStudents() {

        content.getChildren().clear();

        Label heading = createHeading("Manage Students");

        Label description = new Label(
                "View and search all registered students."
        );

        TextField searchField = new TextField();
        searchField.setPromptText("Search by student name or email...");
        searchField.setMaxWidth(400);

        VBox studentList = new VBox(12);

        List<Map<String, Object>> allStudents = new java.util.ArrayList<>();

        // Load students from database
        try (Connection connection = DatabaseConfig.getConnection()) {

            StudentDAO studentDAO = new StudentDAO();

            allStudents.addAll(
                    studentDAO.getAllStudents(connection)
            );

        } catch (Exception e) {

            studentList.getChildren().add(
                    new Label("Error loading students: " + e.getMessage())
            );
        }

        // Display students
        Runnable displayStudents = () -> {

            studentList.getChildren().clear();

            String search = searchField.getText()
                    .trim()
                    .toLowerCase();

            int count = 0;

            for (Map<String, Object> student : allStudents) {

                String name = String.valueOf(student.get("name"));
                String email = String.valueOf(student.get("email"));

                String normalizedName = name.trim()
                        .toLowerCase(Locale.ROOT)
                        .replaceAll("\\s+", " ");

                String normalizedEmail = email.trim()
                        .toLowerCase(Locale.ROOT);

                String normalizedSearch = search.trim()
                        .toLowerCase(Locale.ROOT)
                        .replaceAll("\\s+", " ");

                if (!normalizedName.contains(normalizedSearch)
                        && !normalizedEmail.contains(normalizedSearch)) {
                    continue;
                }

                count++;

                Label nameLabel = new Label(name);
                nameLabel.setStyle(
                        "-fx-font-size: 18px; -fx-font-weight: bold;"
                );

                Label emailLabel = new Label("Email: " + email);

                Label branchLabel = new Label(
                        "Branch: " + student.get("branch")
                );

                Label yearLabel = new Label(
                        "Year: " + student.get("year")
                );

                Label cgpaLabel = new Label(
                        "CGPA: " + student.get("cgpa")
                );

                Label resumeLabel = new Label(
                        "Resume: " + (
                                student.get("resume_url") == null
                                        ? "Not uploaded"
                                        : student.get("resume_url")
                        )
                );
                int studentId = ((Number) student.get("student_id")).intValue();

                Button deleteBtn = new Button("Delete Student");

                deleteBtn.setStyle(
                        "-fx-background-color: #DC2626;" +
                                "-fx-text-fill: white;" +
                                "-fx-padding: 9px;" +
                                "-fx-background-radius: 6px;"
                );

                deleteBtn.setOnAction(e -> {

                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);

                    confirmation.setTitle("Delete Student");
                    confirmation.setHeaderText("Delete " + name + "?");
                    confirmation.setContentText(
                            "This action cannot be undone. Continue?"
                    );

                    confirmation.showAndWait().ifPresent(response -> {

                        if (response == ButtonType.OK) {

                            try (Connection connection =
                                         DatabaseConfig.getConnection()) {

                                StudentDAO studentDAO = new StudentDAO();

                                studentDAO.deleteStudent(connection, studentId);

                                allStudents.remove(student);

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
                Label idLabel = new Label(
                        "Student ID: " + student.get("student_id")
                );

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

                studentCard.setPadding(new Insets(18));

                studentCard.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 10px;" +
                                "-fx-border-color: #E2E8F0;" +
                                "-fx-border-radius: 10px;"
                );

                studentList.getChildren().add(studentCard);
            }

            if (count == 0) {
                studentList.getChildren().add(
                        new Label("No matching students found.")
                );
            }
        };

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        displayStudents.run()
        );

        ScrollPane scrollPane = new ScrollPane(studentList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(450);
        scrollPane.setStyle("-fx-background: #F8FAFC;");

        content.getChildren().addAll(
                heading,
                description,
                searchField,
                scrollPane
        );

        displayStudents.run();
    }
}

