package com.skillsynch.ui;

import com.skillsynch.model.Application;
import com.skillsynch.model.ApplicationStatus;
import com.skillsynch.model.Company;
import com.skillsynch.model.Internship;
import com.skillsynch.model.Offer;
import com.skillsynch.model.Skill;
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
    private final VBox content = new VBox(18);

    private final CompanyService service = new CompanyService();

    private int companyId;
    private Button activeNavButton;

    public Parent getView(Runnable logout, int companyId) {

        this.companyId = companyId;

        // Sidebar
        VBox sidebar = new VBox(6);
        sidebar.setPadding(new Insets(22, 16, 22, 16));
        sidebar.setPrefWidth(230);
        sidebar.getStyleClass().add("sidebar");

        Label logo = new Label("SkillSynch");
        logo.getStyleClass().add("brand-title");

        Label portalLabel = new Label("Company Portal");
        portalLabel.getStyleClass().add("brand-subtitle");

        VBox brandBox = new VBox(2, logo, portalLabel);
        brandBox.setPadding(new Insets(0, 6, 8, 6));

        Button dashboardBtn = createButton("Dashboard");
        Button profileBtn = createButton("Company Profile");
        Button postBtn = createButton("Post Internship");
        Button internshipsBtn = createButton("My Internships");
        Button applicationsBtn = createButton("Applications");
        Button shortlistBtn = createButton("Shortlisted Students");
        Button offersBtn = createButton("Offers");

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
                profileBtn,
                postBtn,
                internshipsBtn,
                applicationsBtn,
                shortlistBtn,
                offersBtn,
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
        profileBtn.setOnAction(e -> { setActiveNav(profileBtn); showCompanyProfile(); });
        postBtn.setOnAction(e -> { setActiveNav(postBtn); showPostInternship(); });
        internshipsBtn.setOnAction(e -> { setActiveNav(internshipsBtn); showMyInternships(); });
        applicationsBtn.setOnAction(e -> { setActiveNav(applicationsBtn); showApplications(); });
        shortlistBtn.setOnAction(e -> { setActiveNav(shortlistBtn); showShortlistedStudents(); });
        offersBtn.setOnAction(e -> { setActiveNav(offersBtn); showOffers(); });
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

        Label heading = new Label("Company Dashboard");
        heading.getStyleClass().add("hero-title");

        Label welcome = new Label("Welcome to SkillSynch!");
        welcome.getStyleClass().add("hero-subtitle");

        VBox banner = new VBox(6, heading, welcome);
        banner.setPadding(new Insets(24));
        banner.getStyleClass().add("hero-banner");

        HBox cards = new HBox(16);

        try {

            // Total internships posted by this company
            int internshipCount =
                    service.getCompanyInternships(companyId).size();

            // Total applications received by this company
            int applicationCount =
                    service.getCompanyApplications(companyId).size();

            // Students who are shortlisted, in interview, or selected
            int shortlistedCount =
                    service.getShortlistedStudents(companyId).size();

            cards.getChildren().addAll(
                    createCard("Posted Internships",
                            String.valueOf(internshipCount)),

                    createCard("Applications",
                            String.valueOf(applicationCount)),

                    createCard("Shortlisted Students",
                            String.valueOf(shortlistedCount))
            );

        } catch (Exception e) {

            e.printStackTrace();

            Label error = new Label(
                    "Unable to load dashboard statistics: " + e.getMessage()
            );

            content.getChildren().addAll(banner, error);
            return;
        }

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

    private void showPage(String title, String description) {

        content.getChildren().clear();

        Label heading = new Label(title);
        heading.getStyleClass().add("heading");

        Label message = new Label(description);
        message.getStyleClass().add("subheading");

        content.getChildren().addAll(heading, message);
    }

    // POST INTERNSHIP
    private void showPostInternship() {

        content.getChildren().clear();

        Label heading = new Label("Post New Internship");
        heading.getStyleClass().add("heading");

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
        deadlinePicker.setMaxWidth(Double.MAX_VALUE);

        Label skillsHeading = new Label("Select Required Skills");
        skillsHeading.getStyleClass().add("section-heading");

        VBox skillsBox = new VBox(10);

        // Skill id -> its level ComboBox (the ComboBox's userData holds the CheckBox)
        Map<Integer, ComboBox<Integer>> selectedSkills = new HashMap<>();

        try {

            List<Skill> skills = service.getAllSkills();

            if (skills.isEmpty()) {
                skillsBox.getChildren().add(
                        new Label("No skills available. Ask the admin to add skills.")
                );
            }

            for (Skill skill : skills) {

                int skillId = skill.getSkillId();
                String skillName = skill.getSkillName();

                CheckBox skillCheckBox = new CheckBox(skillName);
                skillCheckBox.setStyle(
                        "-fx-text-fill: #222222;" +
                                "-fx-font-size: 14px;" +
                                "-fx-opacity: 1;"
                );
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
                skillRow.setStyle(
                        "-fx-background-color: #eeeeee;" +
                                "-fx-padding: 10;"
                );

                skillsBox.setStyle("-fx-background-color: white;");
                skillsBox.getChildren().add(skillRow);
                selectedSkills.put(skillId, levelBox);

                // Remember which checkbox belongs to this level box
                levelBox.setUserData(skillCheckBox);
            }

        } catch (Exception ex) {
            showAlert("Could not load skills: " + ex.getMessage());
            return;
        }

        Button submitBtn = new Button("Post Internship");
        submitBtn.getStyleClass().add("primary-button");

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

            } catch (CompanyService.ValidationException ex) {
                showAlert(ex.getMessage());
            } catch (Exception ex) {
                showAlert("Could not post internship: " + ex.getMessage());
            }
        });

        VBox form = new VBox(
                14,
                roleField,
                descriptionField,
                cgpaField,
                positionsField,
                deadlinePicker,
                skillsHeading,
                skillsBox,
                submitBtn
        );

        form.setPadding(new Insets(24));
        form.setMaxWidth(600);
        form.getStyleClass().add("card");

        ScrollPane scrollPane = new ScrollPane(form);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(heading, scrollPane);
    }

    // MY INTERNSHIPS
    private void showMyInternships() {

        content.getChildren().clear();

        Label heading = new Label("My Internships");
        heading.getStyleClass().add("heading");

        VBox internshipsBox = new VBox(14);

        try {

            List<Internship> internships =
                    service.getCompanyInternships(companyId);

            if (internships.isEmpty()) {
                internshipsBox.getChildren().add(
                        new Label("You have not posted any internships yet.")
                );
            }

            for (Internship internship : internships) {

                Label role = new Label(internship.getRole());
                role.getStyleClass().add("card-title");

                Label description = new Label(
                        "Description: " + (internship.getDescription() == null
                                ? "No description" : internship.getDescription())
                );
                description.getStyleClass().add("body-text");
                description.setWrapText(true);

                Label positions = new Label(
                        "Positions: " + internship.getNumberOfPositions()
                );
                positions.getStyleClass().add("body-text");

                Label cgpa = new Label(
                        "Minimum CGPA: " + (internship.getMinCgpa() == null
                                ? "Not specified" : internship.getMinCgpa())
                );
                cgpa.getStyleClass().add("body-text");

                Label deadline = new Label(
                        "Deadline: " + (internship.getApplicationDeadline() == null
                                ? "Not specified" : internship.getApplicationDeadline())
                );
                deadline.getStyleClass().add("muted-text");

                Label status = new Label(
                        "Status: " + internship.getStatus()
                );
                status.getStyleClass().add("status-badge");

                VBox card = new VBox(
                        8, role, description, positions, cgpa, deadline, status
                );

                card.setPadding(new Insets(20));
                card.getStyleClass().add("card");

                internshipsBox.getChildren().add(card);
            }

        } catch (Exception ex) {
            showAlert("Could not load internships: " + ex.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane(internshipsBox);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(heading, scrollPane);
    }

    // APPLICATIONS
    private void showApplications() {

        content.getChildren().clear();

        Label heading = new Label("Student Applications");
        heading.getStyleClass().add("heading");

        VBox applicationsBox = new VBox(14);

        try {

            List<Application> applications =
                    service.getCompanyApplications(companyId);

            if (applications.isEmpty()) {
                applicationsBox.getChildren().add(
                        new Label("No students have applied yet.")
                );
            }

            for (Application application : applications) {

                Label name = new Label("Student: " + application.getStudentName());
                name.getStyleClass().add("card-title");

                Label email = new Label("Email: " + (application.getStudentEmail() == null ? "N/A" : application.getStudentEmail()));
                email.getStyleClass().add("body-text");

                Label branch = new Label("Branch: " + application.getBranch());
                branch.getStyleClass().add("body-text");

                Label cgpa = new Label("CGPA: " + (application.getCgpa() == null ? "N/A" : application.getCgpa()));
                cgpa.getStyleClass().add("body-text");

                Label role = new Label("Internship: " + application.getRole());
                role.getStyleClass().add("body-text");

                Label status = new Label("Current Status: " + application.getStatus());
                status.getStyleClass().add("status-badge");

                ComboBox<ApplicationStatus> statusBox = new ComboBox<>();

                statusBox.getItems().addAll(
                        ApplicationStatus.SHORTLISTED,
                        ApplicationStatus.INTERVIEW,
                        ApplicationStatus.SELECTED,
                        ApplicationStatus.REJECTED
                );

                statusBox.setPromptText("Select new status");

                Button updateBtn = new Button("Update Status");
                updateBtn.getStyleClass().add("primary-button");

                int applicationId = application.getApplicationId();

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

                HBox actionRow = new HBox(10, statusBox, updateBtn);
                actionRow.setAlignment(Pos.CENTER_LEFT);

                VBox card = new VBox(
                        8,
                        name, email, branch, cgpa, role,
                        status, actionRow
                );

                card.setPadding(new Insets(20));
                card.getStyleClass().add("card");

                applicationsBox.getChildren().add(card);
            }

        } catch (Exception ex) {
            showAlert("Could not load applications: " + ex.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane(applicationsBox);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(heading, scrollPane);
    }

    // SHORTLISTED STUDENTS
    private void showShortlistedStudents() {

        content.getChildren().clear();

        Label heading = new Label("Shortlisted Students");
        heading.getStyleClass().add("heading");

        VBox studentsBox = new VBox(14);

        try {

            List<Application> students =
                    service.getShortlistedStudents(companyId);

            if (students.isEmpty()) {
                studentsBox.getChildren().add(
                        new Label("No shortlisted students yet.")
                );
            }

            for (Application student : students) {

                Label name = new Label("Student: " + student.getStudentName());
                name.getStyleClass().add("card-title");

                Label email = new Label("Email: " + (student.getStudentEmail() == null ? "N/A" : student.getStudentEmail()));
                email.getStyleClass().add("body-text");

                Label branch = new Label("Branch: " + student.getBranch());
                branch.getStyleClass().add("body-text");

                Label cgpa = new Label("CGPA: " + (student.getCgpa() == null ? "N/A" : student.getCgpa()));
                cgpa.getStyleClass().add("body-text");

                Label role = new Label("Internship: " + student.getRole());
                role.getStyleClass().add("body-text");

                Label status = new Label("Status: " + student.getStatus());
                status.getStyleClass().add("status-badge");

                VBox card = new VBox(
                        8, name, email, branch, cgpa, role, status
                );

                card.setPadding(new Insets(20));
                card.getStyleClass().add("card");

                studentsBox.getChildren().add(card);
            }

        } catch (Exception ex) {
            showAlert("Could not load shortlisted students: " + ex.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane(studentsBox);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

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
        heading.getStyleClass().add("heading");

        TextField nameField = new TextField();
        nameField.setPromptText("Company Name");

        TextField industryField = new TextField();
        industryField.setPromptText("Industry");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Company Description");
        descriptionArea.setPrefRowCount(5);

        Button updateBtn = new Button("Update Profile");
        updateBtn.getStyleClass().add("primary-button");

        try {

            Company company = service.getCompany(companyId);

            if (company == null) {
                showAlert("Company profile not found.");
                return;
            }

            nameField.setText(company.getCompanyName() == null ? "" : company.getCompanyName());
            industryField.setText(company.getIndustry() == null ? "" : company.getIndustry());
            descriptionArea.setText(company.getDescription() == null ? "" : company.getDescription());

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

        Label nameLabel = new Label("Company Name");
        nameLabel.getStyleClass().add("field-label");
        Label industryLabel = new Label("Industry");
        industryLabel.getStyleClass().add("field-label");
        Label descLabel = new Label("Description");
        descLabel.getStyleClass().add("field-label");

        VBox profileCard = new VBox(
                12,
                nameLabel,
                nameField,
                industryLabel,
                industryField,
                descLabel,
                descriptionArea,
                updateBtn
        );
        profileCard.setPadding(new Insets(24));
        profileCard.setMaxWidth(580);
        profileCard.getStyleClass().add("card");

        content.getChildren().addAll(heading, profileCard);
    }

    // OFFERS
    private void showOffers() {

        content.getChildren().clear();

        Label heading = new Label("Manage Offers");
        heading.getStyleClass().add("heading");

        Label description = new Label(
                "Create offers for selected students and track their responses."
        );
        description.getStyleClass().add("subheading");

        ComboBox<Application> applicationDropdown = new ComboBox<>();
        applicationDropdown.setPromptText("Select a selected student");
        applicationDropdown.setMaxWidth(400);

        applicationDropdown.setConverter(new StringConverter<>() {
            @Override
            public String toString(Application application) {
                if (application == null) return "";
                return application.getStudentName() + " - " + application.getRole();
            }

            @Override
            public Application fromString(String string) {
                return null;
            }
        });

        DatePicker joiningDatePicker = new DatePicker();
        joiningDatePicker.setPromptText("Select joining date");

        Button createButton = new Button("Create Offer");
        createButton.getStyleClass().add("primary-button");

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("feedback-message");

        Label studentLabel = new Label("Select Student:");
        studentLabel.getStyleClass().add("field-label");
        Label joiningLabel = new Label("Joining Date:");
        joiningLabel.getStyleClass().add("field-label");

        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setMaxWidth(580);
        form.getStyleClass().add("card");
        form.getChildren().addAll(
                studentLabel,
                applicationDropdown,
                joiningLabel,
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

            Application selected = applicationDropdown.getValue();

            try {

                Integer applicationId = selected == null
                        ? null
                        : selected.getApplicationId();

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
        VBox offerList = new VBox(14);

        try {

            List<Offer> offers = service.getCompanyOffers(companyId);

            for (Offer offer : offers) {

                VBox card = new VBox(8);
                card.setPadding(new Insets(20));
                card.getStyleClass().add("card");

                Label offerStudent = new Label("Student: " + offer.getStudentName());
                offerStudent.getStyleClass().add("card-title");

                Label offerEmail = new Label("Email: " + (offer.getStudentEmail() == null ? "N/A" : offer.getStudentEmail()));
                offerEmail.getStyleClass().add("body-text");

                Label offerRole = new Label("Role: " + offer.getRole());
                offerRole.getStyleClass().add("body-text");

                Label offerJoining = new Label("Joining Date: " + (offer.getJoiningDate() == null ? "Not specified" : offer.getJoiningDate()));
                offerJoining.getStyleClass().add("muted-text");

                Label offerStatus = new Label("Offer Status: " + offer.getStatus());
                offerStatus.getStyleClass().add("status-badge");

                card.getChildren().addAll(
                        offerStudent,
                        offerEmail,
                        offerRole,
                        offerJoining,
                        offerStatus
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