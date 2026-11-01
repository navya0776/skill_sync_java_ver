package com.skillsynch.ui;
import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.dao.SkillDAO;
import java.sql.Connection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdminDashboard {

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
        Button studentsBtn = createButton("Manage Students");
        Button companiesBtn = createButton("Manage Companies");
        Button internshipsBtn = createButton("Approve Internships");
        Button applicationsBtn = createButton("Applications");
        Button reportsBtn = createButton("Placement Reports");
        Button logoutBtn = createButton("Logout");
        Button skillsBtn = createButton("Manage Skills");
        sidebar.getChildren().addAll(
                logo,
                new Separator(),
                dashboardBtn,
                studentsBtn,
                companiesBtn,
                skillsBtn,
                internshipsBtn,
                applicationsBtn,
                reportsBtn,
                logoutBtn
        );

        // Main content
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #F8FAFC;");

        root.setLeft(sidebar);
        root.setCenter(content);

        // Navigation
        dashboardBtn.setOnAction(e -> showDashboard());
        skillsBtn.setOnAction(e -> showManageSkills());
        studentsBtn.setOnAction(e ->
                showPage("Manage Students",
                        "View and manage registered students.")
        );

        companiesBtn.setOnAction(e ->
                showPage("Manage Companies",
                        "View and manage registered companies.")
        );

        internshipsBtn.setOnAction(e ->
                showInternshipApprovals()
        );

        applicationsBtn.setOnAction(e ->
                showPage("Applications",
                        "Monitor student internship applications.")
        );

        reportsBtn.setOnAction(e ->
                showPlacementReports()
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

        Label heading = createHeading("Admin Dashboard");
        Label welcome = new Label("Welcome to SkillSynch Admin Panel!");

        HBox cards = new HBox(20);

        cards.getChildren().addAll(
                createCard("Total Students", "0"),
                createCard("Total Companies", "0"),
                createCard("Internships", "0")
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

    private Label createHeading(String title) {

        Label heading = new Label(title);

        heading.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        return heading;
    }

    private void showPage(String title, String description) {

        content.getChildren().clear();

        Label heading = createHeading(title);
        Label message = new Label(description);

        message.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #64748B;"
        );

        content.getChildren().addAll(heading, message);
    }

    private void showInternshipApprovals() {

        content.getChildren().clear();

        Label heading = createHeading("Approve Internships");

        Label message = new Label(
                "Review internship requests submitted by companies."
        );

        // Sample internship for UI testing
        Label internship = new Label(
                "Java Developer Internship - Sample Company"
        );

        Button approveBtn = new Button("Approve");
        Button rejectBtn = new Button("Reject");

        approveBtn.setStyle(
                "-fx-background-color: #16A34A;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10px;"
        );

        rejectBtn.setStyle(
                "-fx-background-color: #DC2626;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10px;"
        );

        Label status = new Label("Status: Pending");

        approveBtn.setOnAction(e -> {
            status.setText("Status: Approved");
            approveBtn.setDisable(true);
            rejectBtn.setDisable(true);
        });

        rejectBtn.setOnAction(e -> {
            status.setText("Status: Rejected");
            approveBtn.setDisable(true);
            rejectBtn.setDisable(true);
        });

        HBox buttons = new HBox(15, approveBtn, rejectBtn);

        VBox internshipCard = new VBox(
                15,
                internship,
                status,
                buttons
        );

        internshipCard.setPadding(new Insets(20));
        internshipCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10px;"
        );

        content.getChildren().addAll(
                heading,
                message,
                internshipCard
        );
    }

    private void showPlacementReports() {

        content.getChildren().clear();

        Label heading = createHeading("Placement Reports");

        HBox cards = new HBox(20);

        cards.getChildren().addAll(
                createCard("Total Applications", "0"),
                createCard("Selected Students", "0"),
                createCard("Offers Extended", "0")
        );

        content.getChildren().addAll(
                heading,
                cards
        );
    } // End of showPlacementReports()


    private void showManageSkills() {

        content.getChildren().clear();

        Label heading = createHeading("Manage Skills");

        Label description = new Label(
                "Add new skills that students can select from their dashboard."
        );

        TextField skillNameField = new TextField();
        skillNameField.setPromptText("Enter Skill Name");
        skillNameField.setMaxWidth(350);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Enter Category");
        categoryField.setMaxWidth(350);

        Button addSkillBtn = new Button("Add Skill");

        Label statusLabel = new Label();

        addSkillBtn.setOnAction(e -> {

            String skillName = skillNameField.getText().trim();
            String category = categoryField.getText().trim();

            if (skillName.isEmpty() || category.isEmpty()) {
                statusLabel.setText("Please fill in both fields.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            try (Connection conn = DatabaseConfig.getConnection()) {

                SkillDAO skillDAO = new SkillDAO();
                skillDAO.addSkill(conn, skillName, category);

                statusLabel.setText("Skill added successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");

                skillNameField.clear();
                categoryField.clear();

            } catch (Exception ex) {

                ex.printStackTrace();
                statusLabel.setText("Error adding skill: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        VBox form = new VBox(
                15,
                new Label("Skill Name"),
                skillNameField,
                new Label("Category"),
                categoryField,
                addSkillBtn,
                statusLabel
        );

        form.setPadding(new Insets(25));
        form.setMaxWidth(450);

        form.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 12px;"
        );

        content.getChildren().addAll(
                heading,
                description,
                form
        );
    }

}
