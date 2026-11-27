package com.skillsynch.ui;

import com.skillsynch.service.CompanyService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UI layer only: builds JavaFX screens and delegates all data access
 * and validation to CompanyService.
 */
public class CompanyDashboard {

    private final BorderPane root = new BorderPane();
    private final VBox content = new VBox(20);

    private final CompanyService service = new CompanyService();

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

<<<<<<< HEAD
    // DASHBOARD
=======

>>>>>>> 0f3fa947df1da6b62e4ca02d8012b336292e5988
    private void showDashboard() {

        content.getChildren().clear();

        Label heading = new Label("Company Dashboard");
        heading.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        Label welcome = new Label("Welcome to SkillSynch!");

        String internshipCount = "0";
        String applicationCount = "0";
        String shortlistedCount = "0";

        try (Connection connection = DatabaseConfig.getConnection()) {

            InternshipDAO internshipDAO = new InternshipDAO();
            ApplicationDAO applicationDAO = new ApplicationDAO();

            // Count internships posted by this company
            List<Map<String, Object>> internships =
                    internshipDAO.getCompanyInternships(
                            connection, companyId
                    );

            // Count applications received by this company
            List<Map<String, Object>> applications =
                    applicationDAO.getCompanyApplications(
                            connection, companyId
                    );

            // Count shortlisted students
            List<Map<String, Object>> shortlisted =
                    applicationDAO.getShortlistedStudents(
                            connection, companyId
                    );

            internshipCount = String.valueOf(internships.size());
            applicationCount = String.valueOf(applications.size());
            shortlistedCount = String.valueOf(shortlisted.size());

        } catch (Exception ex) {

            ex.printStackTrace();

            new Alert(
                    Alert.AlertType.ERROR,
                    "Unable to load dashboard statistics: " + ex.getMessage()
            ).showAndWait();
        }

        HBox cards = new HBox(20);

        cards.getChildren().addAll(
                createCard("Posted Internships", internshipCount),
                createCard("Applications", applicationCount),
                createCard("Shortlisted Students", shortlistedCount)
        );

        content.getChildren().addAll(heading, welcome, cards);
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

<<<<<<< HEAD
    // POST INTERNSHIP
=======

>>>>>>> 0f3fa947df1da6b62e4ca02d8012b336292e5988
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

<<<<<<< HEAD
        // Skill id -> its level ComboBox (the ComboBox's userData holds the CheckBox)
        Map<Integer, ComboBox<Integer>> selectedSkills = new HashMap<>();

        try {

            List<Map<String, Object>> skills = service.getAllSkills();
=======
        Map<Integer, ComboBox<Integer>> selectedSkills = new HashMap<>();

        // Load skills added by Admin
        try (Connection connection = DatabaseConfig.getConnection()) {

            SkillDAO skillDAO = new SkillDAO();

            List<Map<String, Object>> skills =
                    skillDAO.getAllSkills(connection);
>>>>>>> 0f3fa947df1da6b62e4ca02d8012b336292e5988

            if (skills.isEmpty()) {
                skillsBox.getChildren().add(
                        new Label("No skills available. Ask the admin to add skills.")
                );
            }

            for (Map<String, Object> skill : skills) {

                int skillId = ((Number) skill.get("skill_id")).intValue();
                String skillName = String.valueOf(skill.get("skill_name"));

                CheckBox skillCheckBox = new CheckBox(skillName);

                ComboBox<Integer> levelBox = new ComboBox<>();
                levelBox.getItems().addAll(1, 2, 3, 4, 5);
                levelBox.setValue(1);
                levelBox.setPromptText("Minimum level");
                levelBox.setDisable(true);

                skillCheckBox.setOnAction(e ->
                        levelBox.setDisable(!skillCheckBox.isSelected())
                );

                HBox skillRow = new HBox(15, skillCheckBox, levelBox);
                skillRow.setAlignment(Pos.CENTER_LEFT);

                skillsBox.getChildren().add(skillRow);

                selectedSkills.put(skillId, levelBox);

<<<<<<< HEAD
                // Remember which checkbox belongs to this level box
=======
>>>>>>> 0f3fa947df1da6b62e4ca02d8012b336292e5988
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

            // Collect the ticked skills and their minimum levels
            Map<Integer, Integer> requiredSkills = new HashMap<>();

            for (Map.Entry<Integer, ComboBox<Integer>> entry : selectedSkills.entrySet()) {

                ComboBox<Integer> levelBox = entry.getValue();
                CheckBox checkBox = (CheckBox) levelBox.getUserData();

                if (checkBox.isSelected()) {
                    requiredSkills.put(entry.getKey(), levelBox.getValue());
                }
            }

            try {

                int internshipId = service.postInternship(
                        companyId,
                        roleField.getText(),
                        descriptionField.getText(),
                        cgpaField.getText(),
                        positionsField.getText(),
                        deadlinePicker.getValue(),
                        requiredSkills
                );

                showAlert(
                        "Internship posted successfully!\n" +
                                "Internship ID: " + internshipId + "\n" +
                                "Status: Pending admin approval."
                );

                showDashboard();

<<<<<<< HEAD
            } catch (CompanyService.ValidationException ex) {
                showAlert(ex.getMessage());
=======
                if (deadline.isBefore(LocalDate.now())) {
                    showAlert("Deadline cannot be in the past.");
                    return;
                }

                // Collect selected skills and their minimum levels
                Map<Integer, Integer> requiredSkills = new HashMap<>();

                for (Map.Entry<Integer, ComboBox<Integer>> entry
                        : selectedSkills.entrySet()) {

                    ComboBox<Integer> levelBox = entry.getValue();

                    CheckBox checkBox = (CheckBox) levelBox.getUserData();

                    if (checkBox.isSelected()) {

                        if (levelBox.getValue() == null) {
                            showAlert("Please select a minimum level for every selected skill.");
                            return;
                        }

                        requiredSkills.put(
                                entry.getKey(),
                                levelBox.getValue()
                        );
                    }
                }

                if (requiredSkills.isEmpty()) {
                    showAlert("Please select at least one required skill.");
                    return;
                }

                // Save internship and required skills
                try (Connection connection = DatabaseConfig.getConnection()) {

                    connection.setAutoCommit(false);

                    try {

                        // Fetch the actual registered company name
                        CompanyDAO companyDAO = new CompanyDAO();

                        Map<String, Object> company =
                                companyDAO.getCompanyById(connection, companyId);

                        if (company == null || company.get("company_name") == null) {
                            throw new Exception("Registered company details not found.");
                        }

                        String registeredCompanyName =
                                String.valueOf(company.get("company_name"));

                        InternshipDAO internshipDAO = new InternshipDAO();

                        // Create internship using the real company name
                        int internshipId = internshipDAO.createInternship(
                                connection,
                                companyId,
                                registeredCompanyName,
                                roleField.getText().trim(),
                                descriptionField.getText().trim(),
                                cgpa,
                                positions,
                                Date.valueOf(deadline)
                        );

                        // Save required skills
                        internshipDAO.addRequiredSkills(
                                connection,
                                internshipId,
                                requiredSkills
                        );

                        connection.commit();

                        showAlert(
                                "Internship posted successfully!\n" +
                                        "Internship ID: " + internshipId + "\n" +
                                        "Company: " + registeredCompanyName + "\n" +
                                        "Status: Pending admin approval."
                        );

                        showDashboard();

                    } catch (Exception ex) {

                        connection.rollback();
                        throw ex;
                    }
                }

            } catch (NumberFormatException ex) {

                showAlert(
                        "CGPA must be a number and positions must be an integer."
                );

>>>>>>> 0f3fa947df1da6b62e4ca02d8012b336292e5988
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

<<<<<<< HEAD
    // MY INTERNSHIPS
=======

>>>>>>> 0f3fa947df1da6b62e4ca02d8012b336292e5988
    private void showMyInternships() {

        content.getChildren().clear();

        Label heading = new Label("My Internships");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        VBox internshipsBox = new VBox(15);

        try {

            List<Map<String, Object>> internships =
                    service.getCompanyInternships(companyId);

            if (internships.isEmpty()) {
                internshipsBox.getChildren().add(
                        new Label("You have not posted any internships yet.")
                );
            }

            for (Map<String, Object> internship : internships) {

                Label role = new Label(String.valueOf(internship.get("role")));
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

    // APPLICATIONS
    private void showApplications() {

        content.getChildren().clear();

        Label heading = new Label("Student Applications");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        VBox applicationsBox = new VBox(15);

        try {

            List<Map<String, Object>> applications =
                    service.getCompanyApplications(companyId);

            if (applications.isEmpty()) {
                applicationsBox.getChildren().add(
                        new Label("No students have applied yet.")
                );
            }

            for (Map<String, Object> application : applications) {

                Label name = new Label("Student: " + application.get("student_name"));
                Label email = new Label("Email: " + application.get("email"));
                Label branch = new Label("Branch: " + application.get("branch"));
                Label cgpa = new Label("CGPA: " + application.get("cgpa"));
                Label role = new Label("Internship: " + application.get("role"));
                Label status = new Label("Current Status: " + application.get("status"));

                ComboBox<String> statusBox = new ComboBox<>();

                statusBox.getItems().addAll(
                        "Shortlisted",
                        "Interview",
                        "Selected",
                        "Rejected"
                );

                statusBox.setPromptText("Select new status");

                Button updateBtn = new Button("Update Status");

                int applicationId = (int) application.get("application_id");

                updateBtn.setOnAction(e -> {

                    try {

                        service.updateApplicationStatus(
                                applicationId,
                                companyId,
                                statusBox.getValue()
                        );

                        showAlert("Application status updated successfully!");

                        showApplications();

                    } catch (CompanyService.ValidationException ex) {
                        showAlert(ex.getMessage());
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

    // SHORTLISTED STUDENTS
    private void showShortlistedStudents() {

        content.getChildren().clear();

        Label heading = new Label("Shortlisted Students");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        VBox studentsBox = new VBox(15);

        try {

            List<Map<String, Object>> students =
                    service.getShortlistedStudents(companyId);

            if (students.isEmpty()) {
                studentsBox.getChildren().add(
                        new Label("No shortlisted students yet.")
                );
            }

            for (Map<String, Object> student : students) {

                Label name = new Label("Student: " + student.get("student_name"));
                Label email = new Label("Email: " + student.get("email"));
                Label branch = new Label("Branch: " + student.get("branch"));
                Label cgpa = new Label("CGPA: " + student.get("cgpa"));
                Label role = new Label("Internship: " + student.get("role"));
                Label status = new Label("Status: " + student.get("status"));

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

    // COMPANY PROFILE
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

        try {

            Map<String, Object> company = service.getCompany(companyId);

            if (company == null) {
                showAlert("Company profile not found.");
                return;
            }

            nameField.setText(String.valueOf(company.get("company_name")));
            industryField.setText(String.valueOf(company.get("industry")));
            descriptionArea.setText(String.valueOf(company.get("description")));

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Unable to load company profile.");
            return;
        }

        updateBtn.setOnAction(e -> {

            try {

                boolean updated = service.updateCompanyProfile(
                        companyId,
                        nameField.getText(),
                        industryField.getText(),
                        descriptionArea.getText()
                );

                if (updated) {
                    showAlert("Company profile updated successfully!");
                } else {
                    showAlert("Profile update failed.");
                }

            } catch (CompanyService.ValidationException ex) {
                showAlert(ex.getMessage());
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

    // OFFERS
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

        // Load applications whose status is "Selected"
        try {

            applicationDropdown.getItems().addAll(
                    service.getSelectedApplications(companyId)
            );

        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }

        // Create offer
        createButton.setOnAction(e -> {

            Map<String, Object> selected = applicationDropdown.getValue();

            try {

                Integer applicationId = selected == null
                        ? null
                        : (Integer) selected.get("application_id");

                boolean created = service.createOffer(
                        companyId,
                        applicationId,
                        joiningDatePicker.getValue()
                );

                if (created) {
                    messageLabel.setText("Offer created successfully!");
                    showOffers();
                }

            } catch (CompanyService.ValidationException ex) {
                messageLabel.setText(ex.getMessage());
            } catch (Exception ex) {
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Display existing offers
        VBox offerList = new VBox(12);

        try {

            List<Map<String, Object>> offers = service.getCompanyOffers(companyId);

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