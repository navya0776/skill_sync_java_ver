package com.skillsynch.ui;
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

        internshipsBtn.setOnAction(e ->
                showPage(
                        "Available Internships",
                        "Browse available internships here."
                )
        );

        eligibilityBtn.setOnAction(e ->
                showPage(
                        "Eligibility",
                        "Check your internship eligibility here."
                )
        );

        applicationsBtn.setOnAction(e ->
                showPage(
                        "My Applications",
                        "Track your internship applications here."
                )
        );

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
}