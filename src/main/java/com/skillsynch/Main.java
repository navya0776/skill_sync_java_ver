
package com.skillsynch;

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

        showLogin();

        stage.setTitle("SkillSynch | Placement Management");
        stage.setMinWidth(700);
        stage.setMinHeight(600);
        stage.show();
    }

    private void showLogin() {
        LoginView loginView = new LoginView();

        Scene scene = new Scene(
                loginView.getView(this::showRegister),
                1000,
                700
        );

        stage.setScene(scene);
    }

    private void showRegister() {
        RegisterView registerView = new RegisterView();

        Scene scene = new Scene(
                registerView.getView(this::showLogin),
                1000,
                700
        );

        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
