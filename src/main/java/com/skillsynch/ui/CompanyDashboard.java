package com.skillsynch.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CompanyDashboard {

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
        Button profileBtn = createButton("Company Profile");
        Button postBtn = createButton("Post Internship");
        Button internshipsBtn = createButton("My Internships");
        Button applicationsBtn = createButton("Applications");
        Button shortlistBtn = createButton("Shortlisted Students");
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
                logoutBtn
        );

        // Main content
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #F8FAFC;");

        root.setLeft(sidebar);
        root.setCenter(content);

        // Navigation
        dashboardBtn.setOnAction(e -> showDashboard());

        profileBtn.setOnAction(e ->
                showPage("Company Profile",
                        "Company information will appear here.")
        );

        postBtn.setOnAction(e -> showPostInternship());

        internshipsBtn.setOnAction(e ->
                showPage("My Internships",
                        "Your posted internships will appear here.")
        );

        applicationsBtn.setOnAction(e ->
                showPage("Applications",
                        "Student applications will appear here.")
        );

        shortlistBtn.setOnAction(e ->
                showPage("Shortlisted Students",
                        "Shortlisted students will appear here.")
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
        heading.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

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

                showAlert("Please fill all fields.");
                return;
            }

            try {
                double cgpa = Double.parseDouble(cgpaField.getText());
                int positions = Integer.parseInt(positionsField.getText());

                if (cgpa < 0 || cgpa > 10 || positions <= 0) {
                    showAlert("Enter a valid CGPA and number of positions.");
                    return;
                }

                if (deadlinePicker.getValue()
                        .isBefore(java.time.LocalDate.now())) {
                    showAlert("Deadline cannot be in the past.");
                    return;
                }

                showAlert(
                        "Form validated successfully!\n" +
                                "Database saving will be added later."
                );

            } catch (NumberFormatException ex) {
                showAlert("CGPA must be a number and positions must be an integer.");
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
                submitBtn
        );

        form.setMaxWidth(500);

        content.getChildren().add(form);
    }

    private void showAlert(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SkillSynch");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
