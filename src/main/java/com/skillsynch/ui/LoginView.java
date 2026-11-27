
package com.skillsynch.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import com.skillsynch.service.AuthService;
import com.skillsynch.model.User;
import com.skillsynch.model.UserRole;
import java.util.function.Consumer;
public class LoginView {

    public Parent getView(Runnable openRegister,Consumer<User> onLoginSuccess) {

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(36));
        container.setMaxWidth(440);

        Label logo = new Label("SkillSynch");
        logo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");

        Label heading = new Label("Welcome back");
        heading.getStyleClass().add("heading");

        Label subtitle = new Label("Sign in to continue to your account");
        subtitle.getStyleClass().add("subheading");

        VBox titleBox = new VBox(6, heading, subtitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label roleLabel = new Label("Sign in as");
        roleLabel.getStyleClass().add("field-label");
        ComboBox<UserRole> role = new ComboBox<>();
        role.getItems().addAll(UserRole.STUDENT, UserRole.COMPANY, UserRole.ADMIN);
        role.setValue(UserRole.STUDENT);
        role.setConverter(new StringConverter<>() {
            @Override
            public String toString(UserRole userRole) {
                if (userRole == null) return "";
                return switch (userRole) {
                    case STUDENT -> "Student";
                    case COMPANY -> "Company";
                    case ADMIN -> "College Admin";
                };
            }

            @Override
            public UserRole fromString(String string) {
                return null;
            }
        });
        role.setMaxWidth(Double.MAX_VALUE);

        Label emailLabel = new Label("Email address");
        emailLabel.getStyleClass().add("field-label");
        TextField email = new TextField();
        email.setPromptText("Enter your email");

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("field-label");
        PasswordField password = new PasswordField();
        password.setPromptText("Enter your password");

        Button loginButton = new Button("Sign In");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);


        AuthService authService = new AuthService();

        loginButton.setOnAction(e -> {

            String enteredEmail = email.getText().trim();
            String enteredPassword = password.getText();
            UserRole selectedRole = role.getValue();

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

        VBox form = new VBox(10,
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
        card.setMaxWidth(460);
        card.getStyleClass().add("card");

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("content-area");

        return root;
    }
}
