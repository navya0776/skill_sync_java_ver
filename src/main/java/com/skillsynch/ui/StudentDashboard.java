package com.skillsynch.ui;
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

        heading.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        content.getChildren().add(heading);

        try (Connection connection =
                     DatabaseConfig.getConnection()) {

            StudentDAO studentDAO = new StudentDAO();

            Map<String, Object> student =
                    studentDAO.getStudentById(
                            connection,
                            studentId
                    );

            if (student == null) {

                Label message = new Label(
                        "Student profile not found."
                );

                content.getChildren().add(message);
                return;
            }

            // STUDENT DETAILS
            String name = String.valueOf(
                    student.get("name")
            );

            String email = String.valueOf(
                    student.get("email")
            );

            String branch = String.valueOf(
                    student.get("branch")
            );

            String year = String.valueOf(
                    student.get("year")
            );

            String cgpa = String.valueOf(
                    student.get("cgpa")
            );

            // PROFILE CARD
            VBox profileCard = new VBox(18);

            profileCard.setPadding(new Insets(30));
            profileCard.setMaxWidth(600);

            profileCard.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 12px;" +
                            "-fx-border-color: #E2E8F0;" +
                            "-fx-border-radius: 12px;"
            );

            Label nameLabel = createProfileLabel(
                    "Name", name
            );

            Label emailLabel = createProfileLabel(
                    "Email", email
            );

            Label branchLabel = createProfileLabel(
                    "Branch", branch
            );

            Label yearLabel = createProfileLabel(
                    "Year", year
            );

            Label cgpaLabel = createProfileLabel(
                    "CGPA", cgpa
            );

            profileCard.getChildren().addAll(
                    nameLabel,
                    new Separator(),
                    emailLabel,
                    new Separator(),
                    branchLabel,
                    new Separator(),
                    yearLabel,
                    new Separator(),
                    cgpaLabel
            );

            content.getChildren().add(profileCard);

        } catch (Exception ex) {

            ex.printStackTrace();

            Label error = new Label(
                    "Unable to load student profile."
            );

            error.setStyle(
                    "-fx-text-fill: red;" +
                            "-fx-font-size: 15px;"
            );

            content.getChildren().add(error);
        }
    }

    // PROFILE LABEL DESIGN
    private Label createProfileLabel(
            String field,
            String value
    ) {

        Label label = new Label(
                field + " : " + value
        );

        label.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #334155;"
        );

        return label;
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
}