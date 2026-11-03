package com.skillsynch.ui;
import com.skillsynch.dao.ApplicationDAO;
import com.skillsynch.dao.CompanyDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.dao.InternshipDAO;
import com.skillsynch.dao.SkillDAO;
import javafx.util.StringConverter;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDate;
public class CompanyDashboard {

    private final BorderPane root = new BorderPane();
    private final VBox content = new VBox(20);
    private int companyId;

    public Parent getView(Runnable logout, int companyId) {
        this.companyId = companyId;
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
        Button profileBtn = createButton("Company Profile");
        Button postBtn = createButton("Post Internship");
        Button internshipsBtn = createButton("My Internships");
        Button applicationsBtn = createButton("Applications");
        Button shortlistBtn = createButton("Shortlisted Students");

        Button offersBtn = createButton("Offers");
        Button logoutBtn = createButton("Logout");


        sidebar.getChildren().addAll(
                logo,
                new Separator(),
                dashboardBtn,
                profileBtn,
                postBtn,
                internshipsBtn,
                applicationsBtn,
                shortlistBtn,
                offersBtn,
                logoutBtn
        );

        // Main content
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #F8FAFC;");

        root.setLeft(sidebar);
        root.setCenter(content);

        // Navigation
        dashboardBtn.setOnAction(e -> showDashboard());

        profileBtn.setOnAction(e -> showCompanyProfile());

        postBtn.setOnAction(e -> showPostInternship());

        internshipsBtn.setOnAction(e -> showMyInternships());

        applicationsBtn.setOnAction(e -> showApplications());
        shortlistBtn.setOnAction(e -> showShortlistedStudents());
        offersBtn.setOnAction(e -> showOffers());
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

        Label heading = new Label("Company Dashboard");
        heading.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        Label welcome = new Label("Welcome to SkillSynch!");

        HBox cards = new HBox(20);

        cards.getChildren().addAll(
                createCard("Posted Internships", "0"),
                createCard("Applications", "0"),
                createCard("Shortlisted Students", "0")
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

    private void showPage(String title, String description) {

        content.getChildren().clear();

        Label heading = new Label(title);
        heading.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        Label message = new Label(description);

        content.getChildren().addAll(heading, message);
    }

    private void showPostInternship() {

        content.getChildren().clear();

        Label heading = new Label("Post New Internship");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        TextField roleField = new TextField();
        roleField.setPromptText("Internship Role");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Internship Description");
        descriptionField.setPrefRowCount(3);

        TextField cgpaField = new TextField();
        cgpaField.setPromptText("Minimum CGPA");

        TextField positionsField = new TextField();
        positionsField.setPromptText("Number of Positions");

        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Application Deadline");

        Label skillsHeading = new Label("Select Required Skills");
        skillsHeading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox skillsBox = new VBox(10);

        // Store selected skill IDs and their minimum proficiency levels.
        Map<Integer, ComboBox<Integer>> selectedSkills = new HashMap<>();

        try (Connection connection = DatabaseConfig.getConnection()) {

            SkillDAO skillDAO = new SkillDAO();
            List<Map<String, Object>> skills = skillDAO.getAllSkills(connection);

            if (skills.isEmpty()) {
                skillsBox.getChildren().add(
                        new Label("No skills available. Ask the admin to add skills.")
                );
            }

            for (Map<String, Object> skill : skills) {

                int skillId = (int) skill.get("skill_id");
                String skillName = (String) skill.get("skill_name");

                CheckBox skillCheckBox = new CheckBox(skillName);

                ComboBox<Integer> levelBox = new ComboBox<>();
                levelBox.getItems().addAll(1, 2, 3, 4, 5);
                levelBox.setValue(1);
                levelBox.setPromptText("Minimum level");
                levelBox.setDisable(true);

                skillCheckBox.setOnAction(e -> {
                    levelBox.setDisable(!skillCheckBox.isSelected());
                });

                HBox skillRow = new HBox(15, skillCheckBox, levelBox);
                skillRow.setAlignment(Pos.CENTER_LEFT);

                skillsBox.getChildren().add(skillRow);
                selectedSkills.put(skillId, levelBox);

                // Store checkbox state in the ComboBox properties.
                levelBox.setUserData(skillCheckBox);
            }

        } catch (Exception ex) {
            showAlert("Could not load skills: " + ex.getMessage());
            return;
        }

        Button submitBtn = new Button("Post Internship");
        submitBtn.setStyle(
                "-fx-background-color: #4F46E5;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 12px;"
        );

        submitBtn.setOnAction(e -> {

            if (roleField.getText().isBlank()
                    || descriptionField.getText().isBlank()
                    || cgpaField.getText().isBlank()
                    || positionsField.getText().isBlank()
                    || deadlinePicker.getValue() == null) {

                showAlert("Please fill all internship fields.");
                return;
            }

            try {

                double cgpa = Double.parseDouble(cgpaField.getText().trim());
                int positions = Integer.parseInt(positionsField.getText().trim());

                if (cgpa < 0 || cgpa > 10 || positions <= 0) {
                    showAlert("Enter a valid CGPA and number of positions.");
                    return;
                }

                LocalDate deadline = deadlinePicker.getValue();

                if (deadline.isBefore(LocalDate.now())) {
                    showAlert("Deadline cannot be in the past.");
                    return;
                }

                Map<Integer, Integer> requiredSkills = new HashMap<>();

                for (Map.Entry<Integer, ComboBox<Integer>> entry
                        : selectedSkills.entrySet()) {

                    ComboBox<Integer> levelBox = entry.getValue();
                    CheckBox checkBox = (CheckBox) levelBox.getUserData();

                    if (checkBox.isSelected()) {
                        requiredSkills.put(entry.getKey(), levelBox.getValue());
                    }
                }

                if (requiredSkills.isEmpty()) {
                    showAlert("Please select at least one required skill.");
                    return;
                }

                try (Connection connection = DatabaseConfig.getConnection()) {

                    connection.setAutoCommit(false);

                    try {

                        InternshipDAO internshipDAO = new InternshipDAO();

                        int internshipId = internshipDAO.createInternship(
                                connection,
                                companyId,
                                "Company " + companyId,
                                roleField.getText().trim(),
                                descriptionField.getText().trim(),
                                cgpa,
                                positions,
                                Date.valueOf(deadline)
                        );

                        internshipDAO.addRequiredSkills(
                                connection,
                                internshipId,
                                requiredSkills
                        );

                        connection.commit();

                        showAlert(
                                "Internship posted successfully!\n" +
                                        "Internship ID: " + internshipId + "\n" +
                                        "Status: Pending admin approval."
                        );

                        showDashboard();

                    } catch (Exception ex) {
                        connection.rollback();
                        throw ex;
                    }
                }

            } catch (NumberFormatException ex) {
                showAlert("CGPA must be a number and positions must be an integer.");
            } catch (Exception ex) {
                showAlert("Could not post internship: " + ex.getMessage());
            }
        });

        VBox form = new VBox(
                15,
                heading,
                roleField,
                descriptionField,
                cgpaField,
                positionsField,
                deadlinePicker,
                skillsHeading,
                skillsBox,
                submitBtn
        );

        form.setMaxWidth(600);

        ScrollPane scrollPane = new ScrollPane(form);
        scrollPane.setFitToWidth(true);

        content.getChildren().add(scrollPane);
    }

    private void showMyInternships() {

        content.getChildren().clear();

        Label heading = new Label("My Internships");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        VBox internshipsBox = new VBox(15);

        try (Connection connection = DatabaseConfig.getConnection()) {

            InternshipDAO dao = new InternshipDAO();

            List<Map<String, Object>> internships =
                    dao.getCompanyInternships(connection, companyId);

            if (internships.isEmpty()) {
                internshipsBox.getChildren().add(
                        new Label("You have not posted any internships yet.")
                );
            }

            for (Map<String, Object> internship : internships) {

                Label role = new Label(
                        String.valueOf(internship.get("role"))
                );
                role.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

                Label description = new Label(
                        "Description: " + internship.get("description")
                );

                Label positions = new Label(
                        "Positions: " + internship.get("number_of_positions")
                );

                Label cgpa = new Label(
                        "Minimum CGPA: " + internship.get("min_cgpa")
                );

                Label deadline = new Label(
                        "Deadline: " + internship.get("application_deadline")
                );

                Label status = new Label(
                        "Status: " + internship.get("status")
                );
                status.setStyle("-fx-font-weight: bold; -fx-text-fill: #4F46E5;");

                VBox card = new VBox(
                        10, role, description, positions, cgpa, deadline, status
                );

                card.setPadding(new Insets(20));
                card.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 10;" +
                                "-fx-border-color: #E2E8F0;" +
                                "-fx-border-radius: 10;"
                );

                internshipsBox.getChildren().add(card);
            }

        } catch (Exception ex) {
            showAlert("Could not load internships: " + ex.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane(internshipsBox);
        scrollPane.setFitToWidth(true);

        content.getChildren().addAll(heading, scrollPane);
    }

    private void showApplications() {

        content.getChildren().clear();

        Label heading = new Label("Student Applications");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        VBox applicationsBox = new VBox(15);

        try (Connection connection = DatabaseConfig.getConnection()) {

            ApplicationDAO dao = new ApplicationDAO();

            List<Map<String, Object>> applications =
                    dao.getCompanyApplications(connection, companyId);

            if (applications.isEmpty()) {
                applicationsBox.getChildren().add(
                        new Label("No students have applied yet.")
                );
            }

            for (Map<String, Object> application : applications) {

                Label name = new Label(
                        "Student: " + application.get("student_name")
                );

                Label email = new Label(
                        "Email: " + application.get("email")
                );

                Label branch = new Label(
                        "Branch: " + application.get("branch")
                );

                Label cgpa = new Label(
                        "CGPA: " + application.get("cgpa")
                );

                Label role = new Label(
                        "Internship: " + application.get("role")
                );

                Label status = new Label(
                        "Current Status: " + application.get("status")
                );

                ComboBox<String> statusBox = new ComboBox<>();

                statusBox.getItems().addAll(
                        "Shortlisted",
                        "Interview",
                        "Selected",
                        "Rejected"
                );

                statusBox.setPromptText("Select new status");

                Button updateBtn = new Button("Update Status");

                int applicationId =
                        (int) application.get("application_id");

                updateBtn.setOnAction(e -> {

                    if (statusBox.getValue() == null) {
                        showAlert("Please select a status.");
                        return;
                    }

                    try (Connection updateConnection =
                                 DatabaseConfig.getConnection()) {

                        dao.updateApplicationStatus(
                                updateConnection,
                                applicationId,
                                companyId,
                                statusBox.getValue()
                        );

                        showAlert("Application status updated successfully!");

                        showApplications();

                    } catch (Exception ex) {
                        showAlert("Could not update status: " + ex.getMessage());
                    }
                });

                name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

                VBox card = new VBox(
                        10,
                        name, email, branch, cgpa, role,
                        status, statusBox, updateBtn
                );

                card.setPadding(new Insets(20));

                card.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 10;" +
                                "-fx-border-color: #E2E8F0;" +
                                "-fx-border-radius: 10;"
                );

                applicationsBox.getChildren().add(card);
            }

        } catch (Exception ex) {
            showAlert("Could not load applications: " + ex.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane(applicationsBox);
        scrollPane.setFitToWidth(true);

        content.getChildren().addAll(heading, scrollPane);
    }

    private void showShortlistedStudents() {

        content.getChildren().clear();

        Label heading = new Label("Shortlisted Students");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        VBox studentsBox = new VBox(15);

        try (Connection connection = DatabaseConfig.getConnection()) {

            ApplicationDAO dao = new ApplicationDAO();

            List<Map<String, Object>> students =
                    dao.getShortlistedStudents(connection, companyId);

            if (students.isEmpty()) {
                studentsBox.getChildren().add(
                        new Label("No shortlisted students yet.")
                );
            }

            for (Map<String, Object> student : students) {

                Label name = new Label(
                        "Student: " + student.get("student_name")
                );

                Label email = new Label(
                        "Email: " + student.get("email")
                );

                Label branch = new Label(
                        "Branch: " + student.get("branch")
                );

                Label cgpa = new Label(
                        "CGPA: " + student.get("cgpa")
                );

                Label role = new Label(
                        "Internship: " + student.get("role")
                );

                Label status = new Label(
                        "Status: " + student.get("status")
                );

                name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setStyle("-fx-font-weight: bold; -fx-text-fill: #16A34A;");

                VBox card = new VBox(
                        10, name, email, branch, cgpa, role, status
                );

                card.setPadding(new Insets(20));
                card.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 10;" +
                                "-fx-border-color: #E2E8F0;" +
                                "-fx-border-radius: 10;"
                );

                studentsBox.getChildren().add(card);
            }

        } catch (Exception ex) {
            showAlert("Could not load shortlisted students: " + ex.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane(studentsBox);
        scrollPane.setFitToWidth(true);

        content.getChildren().addAll(heading, scrollPane);
    }

    private void showAlert(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SkillSynch");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showCompanyProfile() {

        content.getChildren().clear();

        Label heading = new Label("Company Profile");
        heading.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Company Name");

        TextField industryField = new TextField();
        industryField.setPromptText("Industry");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Company Description");
        descriptionArea.setPrefRowCount(5);

        Button updateBtn = new Button("Update Profile");

        try (Connection connection = DatabaseConfig.getConnection()) {

            CompanyDAO dao = new CompanyDAO();

            Map<String, Object> company =
                    dao.getCompanyById(connection, companyId);

            if (company == null) {
                showAlert("Company profile not found.");
                return;
            }

            nameField.setText(String.valueOf(company.get("company_name")));
            industryField.setText(String.valueOf(company.get("industry")));
            descriptionArea.setText(
                    String.valueOf(company.get("description"))
            );

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Unable to load company profile.");
            return;
        }

        updateBtn.setOnAction(e -> {

            String name = nameField.getText().trim();
            String industry = industryField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (name.isEmpty() || industry.isEmpty()) {
                showAlert("Company name and industry are required.");
                return;
            }

            try (Connection connection = DatabaseConfig.getConnection()) {

                CompanyDAO dao = new CompanyDAO();

                boolean updated = dao.updateCompany(
                        connection,
                        companyId,
                        name,
                        industry,
                        description
                );

                if (updated) {
                    showAlert("Company profile updated successfully!");
                } else {
                    showAlert("Profile update failed.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Unable to update company profile.");
            }
        });

        content.getChildren().addAll(
                heading,
                new Label("Company Name"),
                nameField,
                new Label("Industry"),
                industryField,
                new Label("Description"),
                descriptionArea,
                updateBtn
        );
    }
    private void showOffers() {

        content.getChildren().clear();

        Label heading = new Label("Manage Offers");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label description = new Label(
                "Create offers for selected students and track their responses."
        );

        ComboBox<Map<String, Object>> applicationDropdown = new ComboBox<>();
        applicationDropdown.setPromptText("Select a selected student");
        applicationDropdown.setMaxWidth(400);

        applicationDropdown.setConverter(new StringConverter<>() {
            @Override
            public String toString(Map<String, Object> application) {
                if (application == null) return "";
                return application.get("student_name") + " - "
                        + application.get("role");
            }

            @Override
            public Map<String, Object> fromString(String string) {
                return null;
            }
        });

        DatePicker joiningDatePicker = new DatePicker();
        joiningDatePicker.setPromptText("Select joining date");

        Button createButton = new Button("Create Offer");
        Label messageLabel = new Label();

        VBox form = new VBox(12);
        form.setPadding(new Insets(15));
        form.getChildren().addAll(
                new Label("Select Student:"),
                applicationDropdown,
                new Label("Joining Date:"),
                joiningDatePicker,
                createButton,
                messageLabel
        );

        // Load selected applications
        try (Connection connection = DatabaseConfig.getConnection()) {

            ApplicationDAO dao = new ApplicationDAO();

            List<Map<String, Object>> applications =
                    dao.getCompanyApplications(connection, companyId);

            for (Map<String, Object> application : applications) {
                if ("Selected".equals(application.get("status"))) {
                    applicationDropdown.getItems().add(application);
                }
            }

        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }

        // Create offer
        createButton.setOnAction(e -> {

            Map<String, Object> selected =
                    applicationDropdown.getValue();

            if (selected == null || joiningDatePicker.getValue() == null) {
                messageLabel.setText("Please select a student and joining date.");
                return;
            }

            if (!joiningDatePicker.getValue().isAfter(LocalDate.now())) {
                messageLabel.setText("Joining date must be in the future.");
                return;
            }

            try (Connection connection = DatabaseConfig.getConnection()) {

                ApplicationDAO dao = new ApplicationDAO();

                boolean created = dao.createOffer(
                        connection,
                        (int) selected.get("application_id"),
                        companyId,
                        Date.valueOf(joiningDatePicker.getValue())
                );

                if (created) {
                    messageLabel.setText("Offer created successfully!");
                    showOffers();
                }

            } catch (Exception ex) {
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Display existing offers
        VBox offerList = new VBox(12);

        try (Connection connection = DatabaseConfig.getConnection()) {

            ApplicationDAO dao = new ApplicationDAO();

            List<Map<String, Object>> offers =
                    dao.getCompanyOffers(connection, companyId);

            for (Map<String, Object> offer : offers) {

                VBox card = new VBox(8);
                card.setPadding(new Insets(15));
                card.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 10;"
                );

                card.getChildren().addAll(
                        new Label("Student: " + offer.get("student_name")),
                        new Label("Email: " + offer.get("email")),
                        new Label("Role: " + offer.get("role")),
                        new Label("Joining Date: " + offer.get("joining_date")),
                        new Label("Offer Status: " + offer.get("status"))
                );

                offerList.getChildren().add(card);
            }

        } catch (Exception e) {
            offerList.getChildren().add(
                    new Label("Error: " + e.getMessage())
            );
        }

        ScrollPane scrollPane = new ScrollPane(offerList);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(
                heading,
                description,
                form,
                scrollPane
        );
    }
}
