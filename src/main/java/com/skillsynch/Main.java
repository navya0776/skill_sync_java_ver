package com.skillsynch;

import com.skillsynch.model.User;
import com.skillsynch.ui.StudentDashboard;
import com.skillsynch.ui.CompanyDashboard;
import com.skillsynch.ui.AdminDashboard;
import com.skillsynch.ui.LoginView;
import com.skillsynch.ui.RegisterView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {

        this.stage = stage;

        stage.setTitle("SkillSynch | Placement Management");
        stage.setMinWidth(700);
        stage.setMinHeight(600);

        showLogin();

        stage.show();
    }

    // LOGIN SCREEN
    private void showLogin() {

        LoginView loginView = new LoginView();

        Scene scene = new Scene(
                loginView.getView(
                        this::showRegister,
                        this::openDashboard
                ),
                1000,
                700
        );

        stage.setScene(scene);
    }

    // REGISTRATION SCREEN
    private void showRegister() {

        RegisterView registerView = new RegisterView();

        Scene scene = new Scene(
                registerView.getView(this::showLogin),
                1000,
                700
        );

        stage.setScene(scene);
    }

    // STUDENT DASHBOARD
    private void showStudentDashboard(int studentId) {

        StudentDashboard dashboard = new StudentDashboard();

        Scene scene = new Scene(
                dashboard.getView(this::showLogin, studentId),
                1100,
                700
        );

        stage.setScene(scene);
    }

    // COMPANY DASHBOARD
    private void showCompanyDashboard() {

        CompanyDashboard dashboard = new CompanyDashboard();

        Scene scene = new Scene(
                dashboard.getView(this::showLogin),
                1100,
                700
        );

        stage.setScene(scene);
    }

    // ADMIN DASHBOARD
    private void showAdminDashboard() {

        AdminDashboard dashboard = new AdminDashboard();

        Scene scene = new Scene(
                dashboard.getView(this::showLogin),
                1100,
                700
        );

        stage.setScene(scene);
    }

    // OPEN DASHBOARD BASED ON AUTHENTICATED USER
    private void openDashboard(User user) {

        if (user == null || user.getRole() == null) {
            showLogin();
            return;
        }

        switch (user.getRole().toLowerCase()) {

            case "student":

                if (user.getStudentId() == null) {
                    showError("No student profile is linked to this account.");
                    return;
                }

                showStudentDashboard(user.getStudentId());
                break;

            case "company":
                showCompanyDashboard();
                break;

            case "admin":
                showAdminDashboard();
                break;

            default:
                showError("Unknown user role.");
                showLogin();
        }
    }

    private void showError(String message) {

        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR,
                        message
                );

        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}