
package com.skillsynch.ui;

import com.skillsynch.service.RegistrationService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RegisterView {

    private final RegistrationService registrationService =
            new RegistrationService();

    public Parent getView(Runnable openLogin) {

        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(35));
        container.setMaxWidth(460);

        Label logo = new Label("SkillSynch");
        logo.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #4F46E5;"
        );

        Label heading = new Label("Create your account");
        heading.getStyleClass().add("heading");

        Label subtitle = new Label(
                "Join the SkillSynch placement platform"
        );
        subtitle.getStyleClass().add("subheading");

        // Account type
        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("Student", "Company");
        role.setValue("Student");
        role.setMaxWidth(Double.MAX_VALUE);

        // Common fields
        TextField name = new TextField();
        name.setPromptText("Full name / Company name");

        TextField email = new TextField();
        email.setPromptText("Email address");

        PasswordField password = new PasswordField();
        password.setPromptText("Create password");

        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm password");

        // Student-specific fields
        TextField branch = new TextField();
        branch.setPromptText("Branch (e.g. Computer Science)");

        TextField year = new TextField();
        year.setPromptText("Current academic year (e.g. 2)");

        TextField cgpa = new TextField();
        cgpa.setPromptText("CGPA (e.g. 8.5)");

        // Company-specific fields
        TextField industry = new TextField();
        industry.setPromptText("Industry (e.g. Software)");

        TextArea description = new TextArea();
        description.setPromptText("Brief company description");
        description.setPrefRowCount(3);
        description.setWrapText(true);

        // Dynamic fields container
        VBox dynamicFields = new VBox(12);

        Runnable updateFields = () -> {

            dynamicFields.getChildren().clear();

            if (role.getValue().equals("Student")) {

                dynamicFields.getChildren().addAll(
                        field("Branch", branch),
                        field("Academic Year", year),
                        field("CGPA", cgpa)
                );

                name.setPromptText("Full name");

            } else {

                dynamicFields.getChildren().addAll(
                        field("Industry", industry),
                        field("Description", description)
                );

                name.setPromptText("Company name");
            }
        };

        role.setOnAction(e -> updateFields.run());
        updateFields.run();

        Button registerButton = new Button("Create Account");
        registerButton.getStyleClass().add("primary-button");
        registerButton.setMaxWidth(Double.MAX_VALUE);

        registerButton.setOnAction(e -> {

            String enteredName = name.getText().trim();
            String enteredEmail = email.getText().trim();
            String enteredPassword = password.getText();
            String confirmedPassword = confirmPassword.getText();

            if (enteredName.isBlank()
                    || enteredEmail.isBlank()
                    || enteredPassword.isBlank()
                    || confirmedPassword.isBlank()) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please complete all required fields."
                );
                return;
            }

            if (!enteredEmail.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please enter a valid email address."
                );
                return;
            }

            if (enteredPassword.length() < 8) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Password must contain at least 8 characters."
                );
                return;
            }

            if (!enteredPassword.equals(confirmedPassword)) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Passwords do not match."
                );
                return;
            }

            try {

                if (role.getValue().equals("Student")) {

                    if (branch.getText().isBlank()
                            || year.getText().isBlank()
                            || cgpa.getText().isBlank()) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Please enter all academic details."
                        );
                        return;
                    }

                    int academicYear;
                    double studentCgpa;

                    try {
                        academicYear = Integer.parseInt(year.getText().trim());
                        studentCgpa = Double.parseDouble(cgpa.getText().trim());
                    } catch (NumberFormatException ex) {
                        showAlert(
                                Alert.AlertType.WARNING,
                                "Enter a valid academic year and CGPA."
                        );
                        return;
                    }

                    if (academicYear < 1 || academicYear > 6) {
                        showAlert(
                                Alert.AlertType.WARNING,
                                "Academic year must be between 1 and 6."
                        );
                        return;
                    }

                    if (studentCgpa < 0 || studentCgpa > 10) {
                        showAlert(
                                Alert.AlertType.WARNING,
                                "CGPA must be between 0 and 10."
                        );
                        return;
                    }

                    registrationService.registerStudent(
                            enteredName,
                            enteredEmail,
                            enteredPassword,
                            branch.getText().trim(),
                            academicYear,
                            studentCgpa
                    );

                } else {

                    registrationService.registerCompany(
                            enteredName,
                            enteredEmail,
                            enteredPassword,
                            industry.getText().trim(),
                            description.getText().trim()
                    );
                }

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Your account has been created successfully!"
                );

                openLogin.run();

            } catch (Exception ex) {

                ex.printStackTrace();

                String message = ex.getMessage() == null
                        ? "Registration failed."
                        : ex.getMessage();

                if (message.toLowerCase().contains("duplicate")
                        || message.contains("1062")) {
                    message = "An account with this email already exists.";
                } else {
                    message = "Registration failed. Please check your details.";
                }

                showAlert(Alert.AlertType.ERROR, message);
            }
        });

        Hyperlink loginLink = new Hyperlink("Sign in");
        loginLink.setOnAction(e -> openLogin.run());

        HBox footer = new HBox(
                new Label("Already have an account?"),
                loginLink
        );
        footer.setAlignment(Pos.CENTER);

        VBox form = new VBox(12,
                field("Account Type", role),
                field("Name", name),
                field("Email Address", email),
                field("Password", password),
                field("Confirm Password", confirmPassword),
                dynamicFields,
                registerButton
        );

        VBox card = new VBox(22,
                logo,
                new VBox(7, heading, subtitle),
                form,
                footer
        );

        card.setPadding(new Insets(35));
        card.setMaxWidth(500);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 16px;"
        );

        card.setAlignment(Pos.CENTER_LEFT);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #F8FAFC;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #F8FAFC;");

        return scrollPane;
    }

    private VBox field(String labelText, Control control) {

        Label label = new Label(labelText);

        control.setMaxWidth(Double.MAX_VALUE);

        return new VBox(6, label, control);
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }
}
