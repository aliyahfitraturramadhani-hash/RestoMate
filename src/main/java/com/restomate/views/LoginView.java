package com.restomate.views;

import com.restomate.controllers.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.effect.DropShadow;

public class LoginView {
    
    private VBox root;
    private LoginController controller;

    public LoginView() {
        controller = new LoginController(this);
        buildUI();
    }

    private void buildUI() {
        // Kontainer utamanya kita bikin di tengah-tengah layar
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        
        // Kita bikin kotak login-nya (Card style) biar keliatan modern dan elegan
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(400);
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        
        // Kasih efek bayangan dikit biar kartunya keliatan pop-up alias 3D
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.1));
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        card.setEffect(shadow);

        // Judul aplikasinya
        Label titleLabel = new Label("RestoMate");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#2196F3")); // Biru muda yang seger

        Label subtitleLabel = new Label("Silakan login untuk memulai shift Anda");
        subtitleLabel.setTextFill(Color.GRAY);
        subtitleLabel.setPadding(new Insets(0, 0, 20, 0));

        // Form inputannya nih
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Username");
        txtUsername.setStyle("-fx-pref-height: 40px; -fx-font-size: 14px; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #E0E0E0; -fx-background-color: #FAFAFA;");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");
        txtPassword.setStyle("-fx-pref-height: 40px; -fx-font-size: 14px; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #E0E0E0; -fx-background-color: #FAFAFA;");

        // Label buat nampilin pesan error kalau user salah masukin data
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        // Tombol saktinya
        Button btnLogin = new Button("Masuk");
        btnLogin.setMaxWidth(Double.MAX_VALUE); // Biar tombolnya selebar kartu
        btnLogin.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 40px; -fx-background-radius: 5;");
        
        // Efek hover simpel pas kursor lewat
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 40px; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnLogin.setOnMouseExited(e -> btnLogin.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 40px; -fx-background-radius: 5;"));

        // Aksi pas tombol dipencet, kita oper ke controller buat diurusin
        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText();
            String password = txtPassword.getText();
            controller.handleLogin(username, password, errorLabel);
        });

        // Susun semuanya ke dalam kartu
        card.getChildren().addAll(titleLabel, subtitleLabel, txtUsername, txtPassword, errorLabel, btnLogin);
        
        // Masukin kartunya ke root
        root.getChildren().add(card);
    }

    public VBox getView() {
        return root;
    }
}
