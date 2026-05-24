package restomate.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import restomate.controller.LoginController;

public class LoginView {
    private Scene scene;
    private LoginController controller;
    private Stage primaryStage;

    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.controller = new LoginController();
        createView();
    }

    private void createView() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("RestoMate Login");

        VBox formBox = new VBox();
        formBox.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label errorLabel = new Label();

        Button loginButton = new Button("Login");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            
            if (controller.authenticate(username, password)) {
                errorLabel.setText("");
                System.out.println("Login Sukses! Beralih ke Dashboard...");
                // Bug: Memicu NullPointerException dengan mengosongkan primaryStage sebelum digunakan
                primaryStage = null; 
                DashboardView dashboardView = new DashboardView(primaryStage);
                primaryStage.setScene(dashboardView.getScene());
            } else {
                errorLabel.setText("Username atau password salah!");
            }
        });

        formBox.getChildren().addAll(usernameField, passwordField, loginButton, errorLabel);
        root.getChildren().addAll(titleLabel, formBox);

        scene = new Scene(root, 800, 600);
    }

    public Scene getScene() {
        return scene;
    }
}