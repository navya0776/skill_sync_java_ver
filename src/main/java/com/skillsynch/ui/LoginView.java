
package com.skillsynch.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.skillsynch.service.AuthService;
import com.skillsynch.model.User;
import java.util.function.Consumer;
public class LoginView {

    public Parent getView(Runnable openRegister,Consumer<User> onLoginSuccess) {

        VBox container = new VBox(22);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        container.setMaxWidth(420);

        Label logo = new Label("SkillSynch");
        logo.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #4F46E5;");

        Label heading = new Label("Welcome back");
        heading.getStyleClass().add("heading");

        Label subtitle = new Label("Sign in to continue to your account");
        subtitle.getStyleClass().add("subheading");

        VBox titleBox = new VBox(8, heading, subtitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label roleLabel = new Label("Sign in as");
        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("Student", "Company", "College Admin");
        role.setValue("Student");
        role.setMaxWidth(Double.MAX_VALUE);
        role.getStyleClass().add("input-field");

        Label emailLabel = new Label("Email address");
        TextField email = new TextField();
        email.setPromptText("Enter your email");
        email.getStyleClass().add("input-field");

        Label passwordLabel = new Label("Password");
        PasswordField password = new PasswordField();
        password.setPromptText("Enter your password");
        password.getStyleClass().add("input-field");

        Button loginButton = new Button("Sign In");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);


        AuthService authService = new AuthService();

        loginButton.setOnAction(e -> {

            String enteredEmail = email.getText().trim();
            String enteredPassword = password.getText();

            String selectedRole = role.getValue().equals("College Admin")
                    ? "admin"
                    : role.getValue().toLowerCase();

            if (enteredEmail.isBlank() || enteredPassword.isBlank()) {
                new Alert(
                        Alert.AlertType.WARNING,
                        "Please enter your email and password."
                ).showAndWait();
                return;
            }

            try {
                User user = authService.login(
                        enteredEmail,
                        enteredPassword,
                        selectedRole
                );

                if (user != null) {

                    new Alert(
                            Alert.AlertType.INFORMATION,
                            "Welcome to SkillSynch, " + user.getUsername()
                    ).showAndWait();

                    onLoginSuccess.accept(user);

                } else {
                    new Alert(
                            Alert.AlertType.ERROR,
                            "Invalid email, password or account type."
                    ).showAndWait();
                }

            } catch (Exception ex) {
                ex.printStackTrace();

                new Alert(
                        Alert.AlertType.ERROR,
                        "Unable to login. Please check your database connection."
                ).showAndWait();
            }
        });

        Hyperlink registerLink = new Hyperlink("Create an account");
        registerLink.setOnAction(e -> openRegister.run());

        HBox footer = new HBox(5,
                new Label("Don't have an account?"),
                registerLink);
        footer.setAlignment(Pos.CENTER);

        VBox form = new VBox(9,
                roleLabel, role,
                emailLabel, email,
                passwordLabel, password,
                loginButton);
        form.setMaxWidth(Double.MAX_VALUE);

        container.getChildren().addAll(
                logo, titleBox, form, footer
        );

        VBox card = new VBox(container);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(500);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 16px;"
        );

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #F8FAFC;");

        return root;
    }
}
