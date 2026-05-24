package com.restomate;

import com.restomate.utils.Database;
import com.restomate.views.LoginView;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    // Kita simpan scene utamanya di sini biar gampang kalau mau ganti-ganti halaman
    private static Scene mainScene;
    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        
        // Pemanasan dulu, inisialisasi database pas aplikasi baru nyala.
        // Biar tabel-tabelnya ke-create dan akun admin default siap dipake.
        Database.initializeDatabase();

        // Mulai dari halaman Login dulu dong pastinya
        LoginView loginView = new LoginView();
        
        mainScene = new Scene(loginView.getView(), 1024, 768);
        
        // Sedikit styling dasar biar nggak kaku banget
        mainScene.getRoot().setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #F5F5F5;");

        primaryStage.setTitle("RestoMate - Restaurant Management System");
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    // Fungsi sakti buat pindah halaman dengan gampang
    // Tinggal panggil Main.setRoot(viewBaru) dari mana aja
    public static void setRoot(Parent root) {
        mainScene.setRoot(root);
        // Pastiin style bawaan tetep nempel tiap ganti root
        root.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #F5F5F5;");
    }
    
    // Buat jaga-jaga kalau butuh nutup paksa dari kode
    public static void closeApp() {
        mainStage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
