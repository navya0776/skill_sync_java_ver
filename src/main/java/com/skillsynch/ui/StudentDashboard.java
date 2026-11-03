package com.skillsynch.ui;
import com.skillsynch.dao.ApplicationDAO;
import com.skillsynch.dao.InternshipDAO;
import com.skillsynch.dao.EligibilityDAO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javafx.stage.FileChooser;
import java.io.File;
import com.skillsynch.dao.SkillDAO;
import javafx.collections.FXCollections;
import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.dao.StudentDAO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.util.*;

public class StudentDashboard {

    private final BorderPane root = new BorderPane();
    private final VBox content = new VBox(20);

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
        internshipsBtn.setOnAction(e -> {
            System.out.println("INTERNSHIPS BUTTON CLICKED");
            showInternships();
        });

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

        Label welcome = new Label(
                "Welcome to SkillSynch!"
        );

        welcome.setStyle(
                "-fx-font-size: 18px;"
        );

        HBox cards = new HBox(20);

        VBox card1 = createCard(
                "Available Internships",
                "0"
        );

        VBox card2 = createCard(
                "Applications",
                "0"
        );

        VBox card3 = createCard(
                "Eligible Internships",
                "0"
        );

        cards.getChildren().addAll(
                card1,
                card2,
                card3
        );

        content.getChildren().addAll(
                heading,
                welcome,
                cards
        );
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

    // NEW FEATURE: DISPLAY STUDENT PROFILE
    private void showProfile() {

        content.getChildren().clear();

        Label heading = new Label("My Profile");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        try (Connection connection = DatabaseConfig.getConnection()) {

            StudentDAO studentDAO = new StudentDAO();

            Map<String, Object> student =
                    studentDAO.getStudentById(connection, studentId);

            if (student == null || student.isEmpty()) {
                content.getChildren().add(
                        new Label("Student profile not found.")
                );
                return;
            }

            TextField nameField = new TextField(
                    String.valueOf(student.get("name"))
            );

            TextField emailField = new TextField(
                    String.valueOf(student.get("email"))
            );

            TextField branchField = new TextField(
                    String.valueOf(student.get("branch"))
            );

            TextField yearField = new TextField(
                    String.valueOf(student.get("year"))
            );

            TextField cgpaField = new TextField(
                    String.valueOf(student.get("cgpa"))
            );

            Object resumeValue = student.get("resume_url");

            TextField resumeField = new TextField(
                    resumeValue == null ? "" : String.valueOf(resumeValue)
            );
            final File[] selectedResume = {null};
            Label messageLabel = new Label();
            Button chooseResumeButton = new Button("Choose PDF");
            Button viewResumeButton = new Button("View Resume");

            viewResumeButton.setOnAction(event -> {

                try {

                    String resumePath;

                    if (selectedResume[0] != null) {
                        resumePath = selectedResume[0].getAbsolutePath();
                    } else {
                        resumePath = resumeValue == null ? "" : resumeValue.toString();
                    }

                    if (resumePath.isEmpty()) {
                        messageLabel.setText("Please upload a resume first.");
                        return;
                    }

                    File resumeFile = new File(resumePath);

                    if (!resumeFile.exists()) {
                        messageLabel.setText("Resume file not found.");
                        return;
                    }

                    java.awt.Desktop.getDesktop().open(resumeFile);

                } catch (Exception ex) {

                    messageLabel.setText(
                            "Unable to open resume: " + ex.getMessage()
                    );
                }
            });
            Button downloadResumeButton = new Button("Download Resume");

            downloadResumeButton.setOnAction(event -> {
                try {
                    String resumePath;

                    if (selectedResume[0] != null) {
                        resumePath = selectedResume[0].getAbsolutePath();
                    } else {
                        resumePath = resumeValue == null ? "" : resumeValue.toString();
                    }

                    if (resumePath.isEmpty()) {
                        messageLabel.setText("Please upload a resume first.");
                        return;
                    }

                    File resumeFile = new File(resumePath);

                    if (!resumeFile.exists()) {
                        messageLabel.setText("Resume file not found.");
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
                        Files.copy(
                                resumeFile.toPath(),
                                destination.toPath(),
                                StandardCopyOption.REPLACE_EXISTING
                        );

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
                        new FileChooser.ExtensionFilter(
                                "PDF Files", "*.pdf"
                        )
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
                    createProfileLabel("Name", ""),
                    nameField,
                    createProfileLabel("Email", ""),
                    emailField,
                    createProfileLabel("Branch", ""),
                    branchField,
                    createProfileLabel("Year", ""),
                    yearField,
                    createProfileLabel("CGPA", ""),
                    cgpaField,
                    createProfileLabel("Resume", ""),
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

                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String branch = branchField.getText().trim();
                    String resumeUrl = resumeField.getText().trim();
                    if (selectedResume[0] != null) {

                        if (selectedResume[0] != null) {

                            Path uploadDirectory = Path.of("uploads")
                                    .toAbsolutePath();

                            Files.createDirectories(uploadDirectory);

                            String fileName = "student_" + studentId + "_resume.pdf";

                            Path destination = uploadDirectory.resolve(fileName);

                            Files.copy(
                                    selectedResume[0].toPath(),
                                    destination,
                                    StandardCopyOption.REPLACE_EXISTING
                            );

                            resumeUrl = destination.toString();

                            System.out.println("Original selected file: "
                                    + selectedResume[0].getAbsolutePath());

                            System.out.println("Copied file location: "
                                    + destination);

                            System.out.println("File successfully copied: "
                                    + Files.exists(destination));
                        }
                    }
                    if (name.isEmpty() || email.isEmpty()
                            || branch.isEmpty()) {

                        messageLabel.setText(
                                "Please fill all required fields."
                        );
                        return;
                    }

                    if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                        messageLabel.setText("Enter a valid email.");
                        return;
                    }

                    int year = Integer.parseInt(yearField.getText().trim());
                    double cgpa = Double.parseDouble(cgpaField.getText().trim());

                    if (year < 1 || year > 5) {
                        messageLabel.setText(
                                "Year must be between 1 and 5."
                        );
                        return;
                    }

                    if (cgpa < 0 || cgpa > 10) {
                        messageLabel.setText(
                                "CGPA must be between 0 and 10."
                        );
                        return;
                    }

                    try (Connection updateConnection =
                                 DatabaseConfig.getConnection()) {

                        studentDAO.updateStudent(
                                updateConnection,
                                studentId,
                                name,
                                branch,
                                year,
                                cgpa,
                                email,
                                resumeUrl
                        );
                    }

                    messageLabel.setText("Profile updated successfully!");
                    messageLabel.setStyle("-fx-text-fill: green;");

                    showProfile();

                } catch (NumberFormatException ex) {

                    messageLabel.setText(
                            "Year and CGPA must be valid numbers."
                    );

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

            profileCard.getChildren().addAll(
                    form,
                    saveButton,
                    messageLabel
            );

            content.getChildren().addAll(
                    heading,
                    profileCard
            );

        } catch (Exception e) {

            content.getChildren().add(
                    new Label("Error loading profile: " + e.getMessage())
            );
            e.printStackTrace();
        }
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

        ComboBox<String> availableSkills = new ComboBox<>();
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

        ListView<String> savedSkillsList = new ListView<>();
        savedSkillsList.setPrefHeight(220);
        savedSkillsList.setPlaceholder(
                new Label("You haven't added any skills yet.")
        );

        // MAPS TO CONNECT SKILL NAMES WITH DATABASE IDS
        Map<String, Integer> availableSkillIds = new HashMap<>();

        Map<String, Integer> savedSkillIds = new HashMap<>();

        SkillDAO skillDAO = new SkillDAO();

        // LOAD SKILLS FROM MYSQL
        try (Connection connection = DatabaseConfig.getConnection()) {

            // Get all available skills
            List<Map<String, Object>> allSkills =
                    skillDAO.getAllSkills(connection);

            // Get skills belonging to logged-in student
            List<Map<String, Object>> mySkills =
                    skillDAO.getStudentSkills(
                            connection,
                            studentId
                    );

            Set<Integer> alreadyAddedIds = new HashSet<>();

            List<String> savedSkillNames = new ArrayList<>();

            // Display student's existing skills
            for (Map<String, Object> skill : mySkills) {

                int id = (Integer) skill.get("skill_id");

                String name = (String) skill.get("skill_name");

                String category = (String) skill.get("category");

                int level = (Integer) skill.get("proficiency_level");

                alreadyAddedIds.add(id);

                String displayName =
                        name + " | " +
                                (category == null ? "Uncategorized" : category) +
                                " | Proficiency: " + level + "/5";

                savedSkillNames.add(displayName);

                savedSkillIds.put(displayName, id);
            }

            savedSkillsList.setItems(
                    FXCollections.observableArrayList(savedSkillNames)
            );

            // Show only skills not already added
            List<String> availableNames = new ArrayList<>();

            for (Map<String, Object> skill : allSkills) {

                int id = (Integer) skill.get("skill_id");

                String name = (String) skill.get("skill_name");

                if (!alreadyAddedIds.contains(id)) {

                    availableNames.add(name);

                    availableSkillIds.put(name, id);
                }
            }

            availableSkills.setItems(
                    FXCollections.observableArrayList(availableNames)
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            Label error = new Label(
                    "Unable to load skills from database."
            );

            error.setStyle("-fx-text-fill: red;");

            content.getChildren().addAll(
                    heading,
                    description,
                    error
            );

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

            String selectedSkill = availableSkills.getValue();

            Integer selectedLevel = proficiencyBox.getValue();

            if (selectedSkill == null || selectedLevel == null) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Please select a skill and proficiency level."
                ).showAndWait();

                return;
            }

            try (Connection connection = DatabaseConfig.getConnection()) {

                int skillId = availableSkillIds.get(selectedSkill);

                skillDAO.addStudentSkill(
                        connection,
                        studentId,
                        skillId,
                        selectedLevel
                );

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

            String selectedSkill = savedSkillsList.getSelectionModel()
                    .getSelectedItem();

            if (selectedSkill == null) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Please select a skill to remove."
                ).showAndWait();

                return;
            }

            try (Connection connection = DatabaseConfig.getConnection()) {

                int skillId = savedSkillIds.get(selectedSkill);

                skillDAO.removeStudentSkill(
                        connection,
                        studentId,
                        skillId
                );

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
    private void showPage(
            String title,
            String description
    ) {

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

        content.getChildren().addAll(
                heading,
                message
        );
    }

    private Label createProfileLabel(String field, String value) {
        Label label = new Label(field);
        label.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #374151;"
        );
        return label;
    }

    private void showInternships() {

        content.getChildren().clear();

        Label heading = new Label("Available Internships");
        heading.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        VBox internshipList = new VBox(15);

        try (Connection connection = DatabaseConfig.getConnection()) {

            InternshipDAO dao = new InternshipDAO();

            List<Map<String, Object>> internships =
                    dao.getAllInternships(connection);

            System.out.println("Total internships: " + internships.size());

            for (Map<String, Object> internship : internships) {
                System.out.println("Role: " + internship.get("role")
                        + " | Status: [" + internship.get("status") + "]");
            }
            for (Map<String, Object> internship : internships) {

                String status = String.valueOf(internship.get("status"));

                if (!status.equals("Approved") &&
                        !status.equals("Published")) {
                    continue;
                }


                Label details = new Label(
                        "Company: " + internship.get("company_name") +
                                "\nRole: " + internship.get("role") +
                                "\nDescription: " + internship.get("description") +
                                "\nMinimum CGPA: " + internship.get("min_cgpa") +
                                "\nPositions: " + internship.get("number_of_positions") +
                                "\nDeadline: " + internship.get("application_deadline")
                );

                details.setStyle(
                        "-fx-padding: 15px;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-color: white;" +
                                "-fx-border-color: #cccccc;"
                );

                Button applyButton = new Button("Apply Now");
                try (Connection checkConnection = DatabaseConfig.getConnection()) {

                    ApplicationDAO applicationDAO = new ApplicationDAO();

                    boolean alreadyApplied = applicationDAO.hasApplied(
                            checkConnection,
                            studentId,
                            Integer.parseInt(String.valueOf(internship.get("intern_id")))
                    );

                    if (alreadyApplied) {
                        applyButton.setText("Applied");
                        applyButton.setDisable(true);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                applyButton.setOnAction(e -> {
                    try (Connection applyConnection = DatabaseConfig.getConnection()) {

                        ApplicationDAO applicationDAO = new ApplicationDAO();

                        int internId = Integer.parseInt(
                                String.valueOf(internship.get("intern_id"))
                        );

                        boolean applied = applicationDAO.applyForInternship(
                                applyConnection, studentId, internId
                        );

                        if (applied) {
                            applyButton.setText("Applied");
                            applyButton.setDisable(true);

                            new Alert(Alert.AlertType.INFORMATION,
                                    "Application submitted successfully!").showAndWait();

                        } else {
                            applyButton.setText("Applied");
                            applyButton.setDisable(true);

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

    private void showMyApplications() {

        content.getChildren().clear();

        Label heading = new Label("My Applications");
        heading.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        VBox applicationList = new VBox(15);

        try (Connection connection = DatabaseConfig.getConnection()) {

            ApplicationDAO dao = new ApplicationDAO();

            List<Map<String, Object>> applications =
                    dao.getStudentApplications(connection, studentId);

            for (Map<String, Object> application : applications) {

                Label details = new Label(
                        "Company: " + application.get("company_name") +
                                "\nRole: " + application.get("role") +
                                "\nApplication Date: " + application.get("application_date") +
                                "\nStatus: " + application.get("status")
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

    private void showEligibility() {

        content.getChildren().clear();

        Label heading = new Label("My Internship Eligibility");
        heading.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        VBox eligibilityList = new VBox(15);

        try (Connection connection = DatabaseConfig.getConnection()) {

            InternshipDAO internshipDAO = new InternshipDAO();
            EligibilityDAO eligibilityDAO = new EligibilityDAO();

            // Calculate eligibility for available internships
            List<Map<String, Object>> internships =
                    internshipDAO.getAllInternships(connection);

            for (Map<String, Object> internship : internships) {

                String status = String.valueOf(internship.get("status"));

                if (status.equals("Approved") || status.equals("Published")) {

                    int internId = Integer.parseInt(
                            String.valueOf(internship.get("intern_id"))
                    );

                    eligibilityDAO.evaluateAndSave(
                            connection, studentId, internId
                    );
                }
            }

            // Retrieve eligibility results
            List<Map<String, Object>> results =
                    eligibilityDAO.getStudentEligibility(connection, studentId);

            for (Map<String, Object> result : results) {

                Label details = new Label(
                        "Company: " + result.get("company_name") +
                                "\nRole: " + result.get("role") +
                                "\nSkill Match: " + result.get("match_percentage") + "%" +
                                "\nEligibility: " + result.get("eligibility_status")
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

    private void showMyOffers() {

        content.getChildren().clear();

        Label heading = createHeading("My Offers");

        VBox offerList = new VBox(15);

        try (Connection connection = DatabaseConfig.getConnection()) {

            ApplicationDAO dao = new ApplicationDAO();

            List<Map<String, Object>> offers =
                    dao.getStudentOffers(connection, studentId);

            if (offers.isEmpty()) {

                offerList.getChildren().add(
                        new Label("You have not received any offers yet.")
                );

            } else {

                for (Map<String, Object> offer : offers) {

                    int offerId =
                            ((Number) offer.get("offer_id")).intValue();

                    String status = String.valueOf(offer.get("status"));

                    Label companyLabel = new Label(
                            "Company: " + offer.get("company_name")
                    );

                    Label roleLabel = new Label(
                            "Role: " + offer.get("role")
                    );

                    Label joiningLabel = new Label(
                            "Joining Date: " + offer.get("joining_date")
                    );

                    Label statusLabel = new Label(
                            "Offer Status: " + status
                    );

                    Button acceptButton = new Button("Accept Offer");
                    Button rejectButton = new Button("Reject Offer");

                    if ("Pending".equals(status)) {

                        acceptButton.setOnAction(e ->
                                respondToOffer(offerId, "Accepted")
                        );

                        rejectButton.setOnAction(e ->
                                respondToOffer(offerId, "Rejected")
                        );

                    } else {

                        acceptButton.setDisable(true);
                        rejectButton.setDisable(true);
                    }

                    HBox buttons = new HBox(
                            10,
                            acceptButton,
                            rejectButton
                    );

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

    private void respondToOffer(int offerId, String status) {

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Offer Confirmation");
        confirmation.setHeaderText("Confirm your decision");
        confirmation.setContentText(
                "Are you sure you want to " + status.toLowerCase() + " this offer?"
        );

        confirmation.showAndWait().ifPresent(response -> {

            if (response == ButtonType.OK) {

                try (Connection connection = DatabaseConfig.getConnection()) {

                    ApplicationDAO dao = new ApplicationDAO();

                    dao.updateOfferStatus(
                            connection,
                            offerId,
                            studentId,
                            status
                    );

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("Offer " + status.toLowerCase() + " successfully!");
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
