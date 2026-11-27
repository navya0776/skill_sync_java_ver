package com.skillsynch.ui;

import com.skillsynch.model.Application;
import com.skillsynch.model.Eligibility;
import com.skillsynch.model.Internship;
import com.skillsynch.model.Offer;
import com.skillsynch.model.OfferStatus;
import com.skillsynch.model.Skill;
import com.skillsynch.model.Student;
import com.skillsynch.model.StudentSkill;
import com.skillsynch.service.StudentService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.*;

public class StudentDashboard {

    private final BorderPane root = new BorderPane();
    private final VBox content = new VBox(18);

    private final StudentService service = new StudentService();

    private int studentId;
    private Button activeNavButton;

    // Receives the logged-in student's ID from Main.java
    public Parent getView(Runnable logout, int studentId) {

        this.studentId = studentId;

        // SIDEBAR
        VBox sidebar = new VBox(6);
        sidebar.setPadding(new Insets(22, 16, 22, 16));
        sidebar.setPrefWidth(230);
        sidebar.getStyleClass().add("sidebar");

        Label logo = new Label("SkillSynch");
        logo.getStyleClass().add("brand-title");

        Label portalLabel = new Label("Student Portal");
        portalLabel.getStyleClass().add("brand-subtitle");

        VBox brandBox = new VBox(2, logo, portalLabel);
        brandBox.setPadding(new Insets(0, 6, 8, 6));

        Button dashboardBtn = createButton("Dashboard");
        Button profileBtn = createButton("My Profile");
        Button skillsBtn = createButton("My Skills");
        Button internshipsBtn = createButton("Internships");
        Button eligibilityBtn = createButton("Eligibility");
        Button applicationsBtn = createButton("My Applications");
        Button offersBtn = createButton("My Offers");

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
                skillsBtn,
                internshipsBtn,
                eligibilityBtn,
                applicationsBtn,
                offersBtn,
                spacer,
                logoutBtn
        );

        // MAIN CONTENT
        content.setPadding(new Insets(28));
        content.getStyleClass().add("content-area");

        root.setLeft(sidebar);
        root.setCenter(content);

        // BUTTON ACTIONS
        dashboardBtn.setOnAction(e -> { setActiveNav(dashboardBtn); showDashboard(); });
        profileBtn.setOnAction(e -> { setActiveNav(profileBtn); showProfile(); });
        skillsBtn.setOnAction(e -> { setActiveNav(skillsBtn); showSkills(); });
        offersBtn.setOnAction(e -> { setActiveNav(offersBtn); showMyOffers(); });
        internshipsBtn.setOnAction(e -> { setActiveNav(internshipsBtn); showInternships(); });
        eligibilityBtn.setOnAction(e -> { setActiveNav(eligibilityBtn); showEligibility(); });
        applicationsBtn.setOnAction(e -> { setActiveNav(applicationsBtn); showMyApplications(); });
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

    // SIDEBAR BUTTON DESIGN
    private Button createButton(String text) {

        Button button = new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add("nav-button");

        return button;
    }

    // MAIN DASHBOARD
    private void showDashboard() {

        content.getChildren().clear();

        Label heading = new Label("Student Dashboard");
        heading.getStyleClass().add("hero-title");

        Label welcome = new Label("Welcome to SkillSynch!");
        welcome.getStyleClass().add("hero-subtitle");

        VBox banner = new VBox(6, heading, welcome);
        banner.setPadding(new Insets(24));
        banner.getStyleClass().add("hero-banner");

        HBox cards = new HBox(16);

        VBox card1 = createCard("Available Internships", "0");
        VBox card2 = createCard("Applications", "0");
        VBox card3 = createCard("Eligible Internships", "0");

        cards.getChildren().addAll(card1, card2, card3);

        content.getChildren().addAll(banner, cards);
    }

    // DASHBOARD CARDS
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

    // MY PROFILE PAGE
    private void showProfile() {

        content.getChildren().clear();

        Label heading = createHeading("My Profile");

        try {

            Student student = service.getStudentProfile(studentId);

            if (student == null) {
                content.getChildren().add(
                        new Label("Student profile not found.")
                );
                return;
            }

            TextField nameField = new TextField(student.getName() == null ? "" : student.getName());
            TextField emailField = new TextField(student.getEmail() == null ? "" : student.getEmail());
            TextField branchField = new TextField(student.getBranch() == null ? "" : student.getBranch());
            TextField yearField = new TextField(student.getYear() == null ? "" : String.valueOf(student.getYear()));
            TextField cgpaField = new TextField(student.getCgpa() == null ? "" : String.valueOf(student.getCgpa()));

            String resumeValue = student.getResumeUrl();

            TextField resumeField = new TextField(
                    resumeValue == null ? "" : resumeValue
            );

            final File[] selectedResume = {null};
            Label messageLabel = new Label();
            messageLabel.getStyleClass().add("feedback-message");

            Button chooseResumeButton = new Button("Choose PDF");
            chooseResumeButton.getStyleClass().add("secondary-button");
            Button viewResumeButton = new Button("View Resume");
            viewResumeButton.getStyleClass().add("secondary-button");
            Button downloadResumeButton = new Button("Download Resume");
            downloadResumeButton.getStyleClass().add("secondary-button");

            viewResumeButton.setOnAction(event -> {

                try {

                    File resumeFile = findResumeFile(selectedResume[0], resumeValue, messageLabel);

                    if (resumeFile == null) {
                        return;
                    }

                    java.awt.Desktop.getDesktop().open(resumeFile);

                } catch (Exception ex) {

                    messageLabel.setText(
                            "Unable to open resume: " + ex.getMessage()
                    );
                }
            });

            downloadResumeButton.setOnAction(event -> {

                try {

                    File resumeFile = findResumeFile(selectedResume[0], resumeValue, messageLabel);

                    if (resumeFile == null) {
                        return;
                    }

                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Resume");
                    fileChooser.setInitialFileName("My_Resume.pdf");
                    fileChooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                    );

                    File destination = fileChooser.showSaveDialog(root.getScene().getWindow());

                    if (destination != null) {
                        service.copyResume(resumeFile, destination);
                        messageLabel.setText("Resume downloaded successfully!");
                    }

                } catch (Exception ex) {
                    messageLabel.setText("Download failed: " + ex.getMessage());
                }
            });

            chooseResumeButton.setOnAction(event -> {

                FileChooser fileChooser = new FileChooser();

                fileChooser.setTitle("Select Your Resume");

                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                );

                File file = fileChooser.showOpenDialog(root.getScene().getWindow());

                if (file != null) {
                    selectedResume[0] = file;
                    resumeField.setText(file.getName());
                }
            });

            nameField.setPromptText("Enter your name");
            emailField.setPromptText("Enter your email");
            branchField.setPromptText("Enter your branch");
            yearField.setPromptText("Enter your year");
            cgpaField.setPromptText("Enter your CGPA");
            resumeField.setPromptText("Enter your resume URL");

            HBox resumeActions = new HBox(10, chooseResumeButton, viewResumeButton, downloadResumeButton);
            resumeActions.setAlignment(Pos.CENTER_LEFT);

            VBox form = new VBox(10);

            form.getChildren().addAll(
                    createProfileLabel("Name"),
                    nameField,
                    createProfileLabel("Email"),
                    emailField,
                    createProfileLabel("Branch"),
                    branchField,
                    createProfileLabel("Year"),
                    yearField,
                    createProfileLabel("CGPA"),
                    cgpaField,
                    createProfileLabel("Resume"),
                    resumeField,
                    resumeActions
            );

            Button saveButton = new Button("Save Changes");
            saveButton.getStyleClass().add("primary-button");

            saveButton.setOnAction(event -> {

                try {

                    service.updateProfile(
                            studentId,
                            nameField.getText(),
                            emailField.getText(),
                            branchField.getText(),
                            yearField.getText(),
                            cgpaField.getText(),
                            resumeField.getText(),
                            selectedResume[0]
                    );

                    messageLabel.setText("Profile updated successfully!");

                    showProfile();

                } catch (StudentService.ValidationException ex) {

                    messageLabel.setText(ex.getMessage());

                } catch (Exception ex) {

                    messageLabel.setText(
                            "Error updating profile: " + ex.getMessage()
                    );
                }
            });

            VBox profileCard = new VBox(16);
            profileCard.setPadding(new Insets(24));
            profileCard.setMaxWidth(580);
            profileCard.getStyleClass().add("card");

            profileCard.getChildren().addAll(form, saveButton, messageLabel);

            ScrollPane scrollPane = new ScrollPane(profileCard);
            scrollPane.setFitToWidth(true);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            content.getChildren().addAll(heading, scrollPane);

        } catch (Exception e) {

            content.getChildren().add(
                    new Label("Error loading profile: " + e.getMessage())
            );
            e.printStackTrace();
        }
    }


    private File findResumeFile(File selected, String storedValue, Label messageLabel) {

        String resumePath = service.resolveResumePath(selected, storedValue);

        if (resumePath.isEmpty()) {
            messageLabel.setText("Please upload a resume first.");
            return null;
        }

        File resumeFile = new File(resumePath);

        if (!resumeFile.exists()) {
            messageLabel.setText("Resume file not found.");
            return null;
        }

        return resumeFile;
    }

    // MY SKILLS PAGE
    private void showSkills() {

        content.getChildren().clear();

        Label heading = createHeading("My Skills");

        Label description = new Label(
                "Manage your technical skills and proficiency levels."
        );
        description.getStyleClass().add("subheading");

        // AVAILABLE SKILLS
        Label availableLabel = createProfileLabel("Select a Skill");

        ComboBox<Skill> availableSkills = new ComboBox<>();
        availableSkills.setPromptText("Choose a skill");
        availableSkills.setPrefWidth(260);

        // PROFICIENCY LEVEL
        Label proficiencyLabel = createProfileLabel("Proficiency Level");

        ComboBox<Integer> proficiencyBox = new ComboBox<>(
                FXCollections.observableArrayList(1, 2, 3, 4, 5)
        );

        proficiencyBox.setValue(3);
        proficiencyBox.setPrefWidth(150);

        // STUDENT'S SAVED SKILLS
        Label savedLabel = new Label("My Saved Skills");
        savedLabel.getStyleClass().add("section-heading");

        ListView<StudentSkill> savedSkillsList = new ListView<>();
        savedSkillsList.setPrefHeight(220);
        savedSkillsList.setPlaceholder(
                new Label("You haven't added any skills yet.")
        );
        savedSkillsList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(StudentSkill item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String category = item.getCategory() == null ? "Uncategorized" : item.getCategory();
                    setText(item.getSkillName() + " | " + category
                            + " | Proficiency: " + item.getProficiencyLevel() + "/5");
                }
            }
        });

        // LOAD SKILLS
        try {

            List<Skill> allSkills = service.getAllSkills();
            List<StudentSkill> mySkills = service.getStudentSkills(studentId);

            Set<Integer> alreadyAddedIds = new HashSet<>();

            // Display student's existing skills
            for (StudentSkill skill : mySkills) {
                alreadyAddedIds.add(skill.getSkillId());
            }

            savedSkillsList.setItems(
                    FXCollections.observableArrayList(mySkills)
            );

            // Show only skills not already added
            List<Skill> availableSkillList = new ArrayList<>();

            for (Skill skill : allSkills) {
                if (!alreadyAddedIds.contains(skill.getSkillId())) {
                    availableSkillList.add(skill);
                }
            }

            availableSkills.setItems(
                    FXCollections.observableArrayList(availableSkillList)
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            Label error = new Label("Unable to load skills from database.");
            error.getStyleClass().add("feedback-message");

            content.getChildren().addAll(heading, description, error);

            return;
        }

        // ADD SKILL BUTTON
        Button addButton = new Button("Add Skill");
        addButton.getStyleClass().add("primary-button");

        addButton.setOnAction(e -> {

            Skill selectedSkill = availableSkills.getValue();
            Integer selectedLevel = proficiencyBox.getValue();

            if (selectedSkill == null || selectedLevel == null) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Please select a skill and proficiency level."
                ).showAndWait();

                return;
            }

            try {

                service.addStudentSkill(studentId, selectedSkill.getSkillId(), selectedLevel);

                new Alert(
                        Alert.AlertType.INFORMATION,
                        "Skill added successfully!"
                ).showAndWait();

                // Refresh the page
                showSkills();

            } catch (Exception ex) {

                ex.printStackTrace();

                new Alert(
                        Alert.AlertType.ERROR,
                        "Unable to add skill: " + ex.getMessage()
                ).showAndWait();
            }
        });

        // REMOVE SKILL BUTTON
        Button removeButton = new Button("Remove Selected Skill");
        removeButton.getStyleClass().add("secondary-button");

        removeButton.setOnAction(e -> {

            StudentSkill selectedSkill = savedSkillsList.getSelectionModel().getSelectedItem();

            if (selectedSkill == null) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Please select a skill to remove."
                ).showAndWait();

                return;
            }

            try {

                service.removeStudentSkill(studentId, selectedSkill.getSkillId());

                new Alert(
                        Alert.AlertType.INFORMATION,
                        "Skill removed successfully!"
                ).showAndWait();

                // Refresh the page
                showSkills();

            } catch (Exception ex) {

                ex.printStackTrace();

                new Alert(
                        Alert.AlertType.ERROR,
                        "Unable to remove skill: " + ex.getMessage()
                ).showAndWait();
            }
        });

        // ADD SKILL SECTION
        VBox addSection = new VBox(14);
        addSection.setPadding(new Insets(20));
        addSection.getStyleClass().add("card");

        HBox selectionRow = new HBox(16);
        selectionRow.setAlignment(Pos.CENTER_LEFT);

        VBox skillBox = new VBox(6, availableLabel, availableSkills);
        VBox levelBox = new VBox(6, proficiencyLabel, proficiencyBox);

        selectionRow.getChildren().addAll(skillBox, levelBox);

        Label addSectionTitle = new Label("Add a New Skill");
        addSectionTitle.getStyleClass().add("section-heading");

        addSection.getChildren().addAll(
                addSectionTitle,
                selectionRow,
                addButton
        );

        // SAVED SKILLS SECTION
        VBox savedSection = new VBox(12);
        savedSection.setPadding(new Insets(20));
        savedSection.getStyleClass().add("card");

        savedSection.getChildren().addAll(
                savedLabel,
                savedSkillsList,
                removeButton
        );

        VBox skillsLayout = new VBox(16, addSection, savedSection);
        ScrollPane scrollPane = new ScrollPane(skillsLayout);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // DISPLAY EVERYTHING
        content.getChildren().addAll(
                heading,
                description,
                scrollPane
        );
    }

    // PLACEHOLDER PAGES
    private void showPage(String title, String description) {

        content.getChildren().clear();

        Label heading = createHeading(title);

        Label message = new Label(description);
        message.getStyleClass().add("subheading");

        content.getChildren().addAll(heading, message);
    }

    private Label createProfileLabel(String field) {
        Label label = new Label(field);
        label.getStyleClass().add("field-label");
        return label;
    }

    // INTERNSHIPS PAGE
    private void showInternships() {

        content.getChildren().clear();

        Label heading = createHeading("Available Internships");

        VBox internshipList = new VBox(14);

        try {

            // Service already returns only Approved / Published internships
            List<Internship> internships = service.getAvailableInternships();

            for (Internship internship : internships) {

                Label roleLabel = new Label(internship.getRole());
                roleLabel.getStyleClass().add("card-title");

                Label companyLabel = new Label("Company: " + internship.getCompanyName());
                companyLabel.getStyleClass().add("body-text");

                Label descriptionLabel = new Label(
                        "Description: " + (internship.getDescription() == null ? "No description" : internship.getDescription())
                );
                descriptionLabel.getStyleClass().add("body-text");
                descriptionLabel.setWrapText(true);

                Label metaLabel = new Label(
                        "Minimum CGPA: " + (internship.getMinCgpa() == null ? "Not specified" : internship.getMinCgpa()) +
                                "  |  Positions: " + internship.getNumberOfPositions() +
                                "  |  Deadline: " + (internship.getApplicationDeadline() == null ? "Not specified" : internship.getApplicationDeadline())
                );
                metaLabel.getStyleClass().add("muted-text");

                int internId = internship.getInternId();

                Button applyButton = new Button("Apply Now");
                applyButton.getStyleClass().add("primary-button");

                try {

                    if (service.hasApplied(studentId, internId)) {
                        applyButton.setText("Applied");
                        applyButton.setDisable(true);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                applyButton.setOnAction(e -> {

                    try {

                        boolean applied = service.applyForInternship(studentId, internId);

                        applyButton.setText("Applied");
                        applyButton.setDisable(true);

                        if (applied) {
                            new Alert(Alert.AlertType.INFORMATION,
                                    "Application submitted successfully!").showAndWait();
                        } else {
                            new Alert(Alert.AlertType.WARNING,
                                    "You have already applied for this internship.").showAndWait();
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        new Alert(Alert.AlertType.ERROR,
                                "Application failed: " + ex.getMessage()).showAndWait();
                    }
                });

                VBox internshipCard = new VBox(8, roleLabel, companyLabel, descriptionLabel, metaLabel, applyButton);
                internshipCard.setPadding(new Insets(20));
                internshipCard.getStyleClass().add("card");

                internshipList.getChildren().add(internshipCard);
            }

            if (internshipList.getChildren().isEmpty()) {
                internshipList.getChildren().add(
                        new Label("No internships available currently.")
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Error loading internships: " + ex.getMessage())
                    .showAndWait();
        }

        ScrollPane scrollPane = new ScrollPane(internshipList);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(heading, scrollPane);
    }

    // MY APPLICATIONS PAGE
    private void showMyApplications() {

        content.getChildren().clear();

        Label heading = createHeading("My Applications");

        VBox applicationList = new VBox(14);

        try {

            List<Application> applications =
                    service.getStudentApplications(studentId);

            for (Application application : applications) {

                Label roleLabel = new Label(application.getRole());
                roleLabel.getStyleClass().add("card-title");

                Label companyLabel = new Label("Company: " + application.getCompanyName());
                companyLabel.getStyleClass().add("body-text");

                Label dateLabel = new Label("Application Date: " + application.getApplicationDate());
                dateLabel.getStyleClass().add("muted-text");

                Label statusLabel = new Label("Status: " + application.getStatus());
                statusLabel.getStyleClass().add("status-badge");

                VBox card = new VBox(8, roleLabel, companyLabel, dateLabel, statusLabel);
                card.setPadding(new Insets(20));
                card.getStyleClass().add("card");

                applicationList.getChildren().add(card);
            }

            if (applications.isEmpty()) {
                applicationList.getChildren().add(
                        new Label("You have not applied for any internships yet.")
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();

            new Alert(
                    Alert.AlertType.ERROR,
                    "Error loading applications: " + ex.getMessage()
            ).showAndWait();
        }

        ScrollPane scrollPane = new ScrollPane(applicationList);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(heading, scrollPane);
    }

    // ELIGIBILITY PAGE
    private void showEligibility() {

        content.getChildren().clear();

        Label heading = createHeading("My Internship Eligibility");

        VBox eligibilityList = new VBox(14);

        try {

            List<Eligibility> results =
                    service.evaluateAndGetEligibility(studentId);

            for (Eligibility result : results) {

                Label roleLabel = new Label(result.getRole());
                roleLabel.getStyleClass().add("card-title");

                Label companyLabel = new Label("Company: " + result.getCompanyName());
                companyLabel.getStyleClass().add("body-text");

                Label matchLabel = new Label("Skill Match: " + result.getMatchPercentage() + "%");
                matchLabel.getStyleClass().add("body-text");

                Label statusLabel = new Label("Eligibility: " + result.getEligibilityStatus());
                statusLabel.getStyleClass().add("status-badge");

                VBox card = new VBox(8, roleLabel, companyLabel, matchLabel, statusLabel);
                card.setPadding(new Insets(20));
                card.getStyleClass().add("card");

                eligibilityList.getChildren().add(card);
            }

            if (results.isEmpty()) {
                eligibilityList.getChildren().add(
                        new Label("No internship eligibility results available.")
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();

            new Alert(
                    Alert.AlertType.ERROR,
                    "Error checking eligibility: " + ex.getMessage()
            ).showAndWait();
        }

        ScrollPane scrollPane = new ScrollPane(eligibilityList);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(heading, scrollPane);
    }

    // MY OFFERS PAGE
    private void showMyOffers() {

        content.getChildren().clear();

        Label heading = createHeading("My Offers");

        VBox offerList = new VBox(14);

        try {

            List<Offer> offers = service.getStudentOffers(studentId);

            if (offers.isEmpty()) {

                offerList.getChildren().add(
                        new Label("You have not received any offers yet.")
                );

            } else {

                for (Offer offer : offers) {

                    int offerId = offer.getOfferId();

                    OfferStatus status = offer.getStatus();

                    Label roleLabel = new Label(offer.getRole());
                    roleLabel.getStyleClass().add("card-title");

                    Label companyLabel = new Label("Company: " + offer.getCompanyName());
                    companyLabel.getStyleClass().add("body-text");

                    Label joiningLabel = new Label("Joining Date: " + (offer.getJoiningDate() == null ? "Not specified" : offer.getJoiningDate()));
                    joiningLabel.getStyleClass().add("muted-text");

                    Label statusLabel = new Label("Offer Status: " + status);
                    statusLabel.getStyleClass().add("status-badge");

                    Button acceptButton = new Button("Accept Offer");
                    acceptButton.getStyleClass().add("primary-button");

                    Button rejectButton = new Button("Reject Offer");
                    rejectButton.getStyleClass().add("secondary-button");

                    if (status == OfferStatus.PENDING) {

                        acceptButton.setOnAction(e -> respondToOffer(offerId, OfferStatus.ACCEPTED));
                        rejectButton.setOnAction(e -> respondToOffer(offerId, OfferStatus.REJECTED));

                    } else {

                        acceptButton.setDisable(true);
                        rejectButton.setDisable(true);
                    }

                    HBox buttons = new HBox(10, acceptButton, rejectButton);

                    VBox card = new VBox(
                            8,
                            roleLabel,
                            companyLabel,
                            joiningLabel,
                            statusLabel,
                            buttons
                    );

                    card.setPadding(new Insets(20));
                    card.getStyleClass().add("card");

                    offerList.getChildren().add(card);
                }
            }

        } catch (Exception e) {

            offerList.getChildren().add(
                    new Label("Error loading offers: " + e.getMessage())
            );
        }

        ScrollPane scrollPane = new ScrollPane(offerList);
        scrollPane.setFitToWidth(true);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(heading, scrollPane);
    }

    private void respondToOffer(int offerId, OfferStatus status) {

        String actionLabel = status.getLabel().toLowerCase(Locale.ROOT);

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Offer Confirmation");
        confirmation.setHeaderText("Confirm your decision");
        confirmation.setContentText(
                "Are you sure you want to " + actionLabel + " this offer?"
        );

        confirmation.showAndWait().ifPresent(response -> {

            if (response == ButtonType.OK) {

                try {

                    service.updateOfferStatus(offerId, studentId, status);

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("Offer " + actionLabel + " successfully!");
                    success.showAndWait();

                    showMyOffers();

                } catch (Exception e) {

                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("Could not update offer");
                    error.setContentText(e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    private Label createHeading(String text) {
        Label heading = new Label(text);
        heading.getStyleClass().add("heading");
        return heading;
    }
}