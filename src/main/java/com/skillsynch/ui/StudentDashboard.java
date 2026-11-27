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
    private final VBox content = new VBox(20);

    private final StudentService service = new StudentService();

    private int studentId;

    // Receives the logged-in student's ID from Main.java
    public Parent getView(Runnable logout, int studentId) {

        this.studentId = studentId;

        // SIDEBAR
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(25));
        sidebar.setPrefWidth(230);

        sidebar.setStyle(
                "-fx-background-color: #1E293B;"
        );

        Label logo = new Label("SkillSynch");

        logo.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 25px;" +
                        "-fx-font-weight: bold;"
        );

        Button dashboardBtn = createButton("Dashboard");
        Button profileBtn = createButton("My Profile");
        Button skillsBtn = createButton("My Skills");
        Button internshipsBtn = createButton("Internships");
        Button eligibilityBtn = createButton("Eligibility");
        Button applicationsBtn = createButton("My Applications");
        Button offersBtn = createButton("My Offers");
        Button logoutBtn = createButton("Logout");

        sidebar.getChildren().addAll(
                logo,
                new Separator(),
                dashboardBtn,
                profileBtn,
                skillsBtn,
                internshipsBtn,
                eligibilityBtn,
                applicationsBtn,
                offersBtn,
                logoutBtn
        );

        // MAIN CONTENT
        content.setPadding(new Insets(30));

        content.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        root.setLeft(sidebar);
        root.setCenter(content);

        // BUTTON ACTIONS
        dashboardBtn.setOnAction(e -> showDashboard());
        profileBtn.setOnAction(e -> showProfile());
        skillsBtn.setOnAction(e -> showSkills());
        offersBtn.setOnAction(e -> showMyOffers());
        internshipsBtn.setOnAction(e -> showInternships());
        eligibilityBtn.setOnAction(e -> showEligibility());
        applicationsBtn.setOnAction(e -> showMyApplications());
        logoutBtn.setOnAction(e -> logout.run());

        showDashboard();

        return root;
    }

    // SIDEBAR BUTTON DESIGN
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

    // MAIN DASHBOARD

    private void showDashboard() {

        content.getChildren().clear();

        Label heading = new Label("Student Dashboard");
        heading.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        Label welcome = new Label("Welcome to SkillSynch!");
<<<<<<< HEAD

        welcome.setStyle("-fx-font-size: 18px;");

        HBox cards = new HBox(20);

        VBox card1 = createCard("Available Internships", "0");
        VBox card2 = createCard("Applications", "0");
        VBox card3 = createCard("Eligible Internships", "0");

        cards.getChildren().addAll(card1, card2, card3);

        content.getChildren().addAll(heading, welcome, cards);
=======
        welcome.setStyle("-fx-font-size: 18px;");

        String internshipCount = "0";
        String applicationCount = "0";
        String eligibleCount = "0";

        try (Connection connection = DatabaseConfig.getConnection()) {

            InternshipDAO internshipDAO = new InternshipDAO();
            ApplicationDAO applicationDAO = new ApplicationDAO();
            EligibilityDAO eligibilityDAO = new EligibilityDAO();

            // 1. Count approved and published internships
            List<Map<String, Object>> internships =
                    internshipDAO.getAllInternships(connection);

            int available = 0;

            for (Map<String, Object> internship : internships) {

                String status = String.valueOf(internship.get("status"));

                if (status.equals("Approved") ||
                        status.equals("Published")) {

                    available++;

                    // Calculate and save eligibility
                    int internId = Integer.parseInt(
                            String.valueOf(internship.get("intern_id"))
                    );

                    eligibilityDAO.evaluateAndSave(
                            connection, studentId, internId
                    );
                }
            }

            internshipCount = String.valueOf(available);

            // 2. Count student's applications
            List<Map<String, Object>> applications =
                    applicationDAO.getStudentApplications(
                            connection, studentId
                    );

            applicationCount = String.valueOf(applications.size());

            // 3. Count eligible internships
            List<Map<String, Object>> eligibilityResults =
                    eligibilityDAO.getStudentEligibility(
                            connection, studentId
                    );

            int eligible = 0;

            for (Map<String, Object> result : eligibilityResults) {

                if ("Eligible".equals(
                        String.valueOf(result.get("eligibility_status")))) {
                    eligible++;
                }
            }

            eligibleCount = String.valueOf(eligible);

        } catch (Exception ex) {
            ex.printStackTrace();

            new Alert(
                    Alert.AlertType.ERROR,
                    "Unable to load dashboard statistics: " + ex.getMessage()
            ).showAndWait();
        }

        HBox cards = new HBox(20);

        cards.getChildren().addAll(
                createCard("Available Internships", internshipCount),
                createCard("Applications", applicationCount),
                createCard("Eligible Internships", eligibleCount)
        );

        content.getChildren().addAll(
                heading,
                welcome,
                cards
        );
>>>>>>> 0f3fa947df1da6b62e4ca02d8012b336292e5988
    }

    // DASHBOARD CARDS
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

    // MY PROFILE PAGE
    private void showProfile() {

        content.getChildren().clear();

        Label heading = new Label("My Profile");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

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

            Button chooseResumeButton = new Button("Choose PDF");
            Button viewResumeButton = new Button("View Resume");
            Button downloadResumeButton = new Button("Download Resume");

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

            VBox form = new VBox(12);

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
                    chooseResumeButton,
                    viewResumeButton,
                    downloadResumeButton
            );

            Button saveButton = new Button("Save Changes");
            saveButton.setStyle(
                    "-fx-background-color: #2563eb;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;"
            );

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
                    messageLabel.setStyle("-fx-text-fill: green;");

                    showProfile();

                } catch (StudentService.ValidationException ex) {

                    messageLabel.setText(ex.getMessage());

                } catch (Exception ex) {

                    messageLabel.setText(
                            "Error updating profile: " + ex.getMessage()
                    );
                }
            });

            VBox profileCard = new VBox(15);
            profileCard.setPadding(new Insets(25));
            profileCard.setMaxWidth(550);
            profileCard.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: #e5e7eb;" +
                            "-fx-border-radius: 12;"
            );

            profileCard.getChildren().addAll(form, saveButton, messageLabel);

            content.getChildren().addAll(heading, profileCard);

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

        Label heading = new Label("My Skills");
        heading.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        Label description = new Label(
                "Manage your technical skills and proficiency levels."
        );

        description.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );

        // AVAILABLE SKILLS
        Label availableLabel = new Label("Select a Skill");

        ComboBox<Skill> availableSkills = new ComboBox<>();
        availableSkills.setPromptText("Choose a skill");
        availableSkills.setPrefWidth(250);

        // PROFICIENCY LEVEL
        Label proficiencyLabel = new Label("Proficiency Level");

        ComboBox<Integer> proficiencyBox = new ComboBox<>(
                FXCollections.observableArrayList(1, 2, 3, 4, 5)
        );

        proficiencyBox.setValue(3);
        proficiencyBox.setPrefWidth(150);

        // STUDENT'S SAVED SKILLS
        Label savedLabel = new Label("My Saved Skills");

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
            error.setStyle("-fx-text-fill: red;");

            content.getChildren().addAll(heading, description, error);

            return;
        }

        // ADD SKILL BUTTON
        Button addButton = new Button("Add Skill");

        addButton.setStyle(
                "-fx-background-color: #4F46E5;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-background-radius: 8px;"
        );

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

        removeButton.setStyle(
                "-fx-background-color: #DC2626;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-background-radius: 8px;"
        );

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
        VBox addSection = new VBox(12);

        addSection.setPadding(new Insets(20));

        addSection.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 12px;"
        );

        HBox selectionRow = new HBox(15);
        selectionRow.setAlignment(Pos.CENTER_LEFT);

        VBox skillBox = new VBox(8, availableLabel, availableSkills);
        VBox levelBox = new VBox(8, proficiencyLabel, proficiencyBox);

        selectionRow.getChildren().addAll(skillBox, levelBox);

        addSection.getChildren().addAll(
                new Label("Add a New Skill"),
                selectionRow,
                addButton
        );

        // SAVED SKILLS SECTION
        VBox savedSection = new VBox(12);

        savedSection.setPadding(new Insets(20));

        savedSection.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 12px;"
        );

        savedSection.getChildren().addAll(
                savedLabel,
                savedSkillsList,
                removeButton
        );

        // DISPLAY EVERYTHING
        content.getChildren().addAll(
                heading,
                description,
                addSection,
                savedSection
        );
    }

    // PLACEHOLDER PAGES
    private void showPage(String title, String description) {

        content.getChildren().clear();

        Label heading = new Label(title);

        heading.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        Label message = new Label(description);

        message.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #64748B;"
        );

        content.getChildren().addAll(heading, message);
    }

    private Label createProfileLabel(String field) {
        Label label = new Label(field);
        label.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #374151;"
        );
        return label;
    }

    // INTERNSHIPS PAGE
    private void showInternships() {

        content.getChildren().clear();

        Label heading = new Label("Available Internships");
        heading.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        VBox internshipList = new VBox(15);

        try {

            // Service already returns only Approved / Published internships
            List<Internship> internships = service.getAvailableInternships();

            for (Internship internship : internships) {

                Label details = new Label(
                        "Company: " + internship.getCompanyName() +
                                "\nRole: " + internship.getRole() +
                                "\nDescription: " + (internship.getDescription() == null ? "No description" : internship.getDescription()) +
                                "\nMinimum CGPA: " + (internship.getMinCgpa() == null ? "Not specified" : internship.getMinCgpa()) +
                                "\nPositions: " + internship.getNumberOfPositions() +
                                "\nDeadline: " + (internship.getApplicationDeadline() == null ? "Not specified" : internship.getApplicationDeadline())
                );

                details.setStyle(
                        "-fx-padding: 15px;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-color: white;" +
                                "-fx-border-color: #cccccc;"
                );

                int internId = internship.getInternId();

                Button applyButton = new Button("Apply Now");

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

                VBox internshipCard = new VBox(10, details, applyButton);
                internshipCard.setStyle("-fx-padding: 10px;");

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

        content.getChildren().addAll(heading, internshipList);
    }

    // MY APPLICATIONS PAGE
    private void showMyApplications() {

        content.getChildren().clear();

        Label heading = new Label("My Applications");
        heading.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        VBox applicationList = new VBox(15);

        try {

            List<Application> applications =
                    service.getStudentApplications(studentId);

            for (Application application : applications) {

                Label details = new Label(
                        "Company: " + application.getCompanyName() +
                                "\nRole: " + application.getRole() +
                                "\nApplication Date: " + application.getApplicationDate() +
                                "\nStatus: " + application.getStatus()
                );

                details.setStyle(
                        "-fx-padding: 15px;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-color: white;" +
                                "-fx-border-color: #cccccc;"
                );

                applicationList.getChildren().add(details);
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

        content.getChildren().addAll(heading, applicationList);
    }

    // ELIGIBILITY PAGE
    private void showEligibility() {

        content.getChildren().clear();

        Label heading = new Label("My Internship Eligibility");
        heading.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        VBox eligibilityList = new VBox(15);

        try {

            List<Eligibility> results =
                    service.evaluateAndGetEligibility(studentId);

            for (Eligibility result : results) {

                Label details = new Label(
                        "Company: " + result.getCompanyName() +
                                "\nRole: " + result.getRole() +
                                "\nSkill Match: " + result.getMatchPercentage() + "%" +
                                "\nEligibility: " + result.getEligibilityStatus()
                );

                details.setStyle(
                        "-fx-padding: 15px;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-color: white;" +
                                "-fx-border-color: #cccccc;"
                );

                eligibilityList.getChildren().add(details);
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

        content.getChildren().addAll(heading, eligibilityList);
    }

    // MY OFFERS PAGE
    private void showMyOffers() {

        content.getChildren().clear();

        Label heading = createHeading("My Offers");

        VBox offerList = new VBox(15);

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

                    Label companyLabel = new Label("Company: " + offer.getCompanyName());
                    Label roleLabel = new Label("Role: " + offer.getRole());
                    Label joiningLabel = new Label("Joining Date: " + (offer.getJoiningDate() == null ? "Not specified" : offer.getJoiningDate()));
                    Label statusLabel = new Label("Offer Status: " + status);

                    Button acceptButton = new Button("Accept Offer");
                    Button rejectButton = new Button("Reject Offer");

                    if (status == OfferStatus.PENDING) {

                        acceptButton.setOnAction(e -> respondToOffer(offerId, OfferStatus.ACCEPTED));
                        rejectButton.setOnAction(e -> respondToOffer(offerId, OfferStatus.REJECTED));

                    } else {

                        acceptButton.setDisable(true);
                        rejectButton.setDisable(true);
                    }

                    HBox buttons = new HBox(10, acceptButton, rejectButton);

                    VBox card = new VBox(
                            10,
                            companyLabel,
                            roleLabel,
                            joiningLabel,
                            statusLabel,
                            buttons
                    );

                    card.setPadding(new Insets(18));

                    card.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 10px;" +
                                    "-fx-border-color: #E2E8F0;"
                    );

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
        heading.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2c3e50;"
        );
        return heading;
    }
}