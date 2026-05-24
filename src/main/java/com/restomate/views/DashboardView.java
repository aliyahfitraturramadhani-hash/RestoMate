package com.restomate.views;

import com.restomate.Main;
import com.restomate.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.effect.DropShadow;

public class DashboardView {

    private BorderPane root;
    private StackPane centerContainer;
    private User currentUser;
    
    // Simpan instance view biar gak bolak-balik dibikin ulang pas diklik (menghemat memori)
    private CashierView cashierView;
    private ManageMenuView manageMenuView;
    private ReservationView reservationView;
    private ReportView reportView;

    public DashboardView(User currentUser) {
        this.currentUser = currentUser;
        
        // Inisialisasi semua layar di awal
        cashierView = new CashierView();
        manageMenuView = new ManageMenuView();
        reservationView = new ReservationView();
        reportView = new ReportView();
        
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();
        
        // Siapin kontainer tengah dulu sebelum sidebar (karena sidebar butuh akses centerContainer)
        centerContainer = new StackPane();
        centerContainer.setPadding(new Insets(20));
        centerContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        BorderPane.setMargin(centerContainer, new Insets(20, 20, 20, 0)); 
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.05));
        shadow.setRadius(10);
        centerContainer.setEffect(shadow);
        
        root.setCenter(centerContainer);
        
        // Siapin sidebar di sebelah kiri
        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);
        
        // Tampilin layar selamat datang di awal
        showWelcomeScreen();
    }
    
    private VBox buildSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        Label logo = new Label("RestoMate");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        logo.setTextFill(Color.web("#2196F3"));
        VBox.setMargin(logo, new Insets(0, 0, 30, 0)); 
        
        Label userLabel = new Label("Halo, " + currentUser.getUsername() + "!");
        userLabel.setTextFill(Color.GRAY);
        VBox.setMargin(userLabel, new Insets(0, 0, 20, 0));
        
        Button btnKasir = createSidebarButton("Kasir (POS)");
        Button btnMenu = createSidebarButton("Kelola Menu");
        Button btnReservasi = createSidebarButton("Reservasi Meja");
        Button btnLaporan = createSidebarButton("Laporan");
        Button btnLogout = createSidebarButton("Logout");
        
        // --- LOGIKA NAVIGASI ---
        btnKasir.setOnAction(e -> {
            cashierView.getController().refresh();
            setCenterContent(cashierView.getView());
        });
        btnMenu.setOnAction(e -> {
            manageMenuView.getController().refresh();
            setCenterContent(manageMenuView.getView());
        });
        btnReservasi.setOnAction(e -> setCenterContent(reservationView.getView()));
        btnLaporan.setOnAction(e -> {
            reportView.getController().refresh();
            setCenterContent(reportView.getView());
        });
        // -----------------------
        
        btnLogout.setOnMouseEntered(e -> btnLogout.setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #D32F2F; -fx-alignment: center-left; -fx-padding: 10 20; -fx-cursor: hand;"));
        btnLogout.setOnMouseExited(e -> btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333; -fx-alignment: center-left; -fx-padding: 10 20;"));
        
        btnLogout.setOnAction(e -> {
            System.out.println("User " + currentUser.getUsername() + " pamit logout.");
            Main.setRoot(new LoginView().getView());
        });
        
        sidebar.getChildren().addAll(logo, userLabel, btnKasir, btnMenu, btnReservasi, btnLaporan, btnLogout);
        return sidebar;
    }
    
    // Fungsi bantuan buat gonta-ganti isi kontainer tengah
    private void setCenterContent(Node node) {
        centerContainer.getChildren().clear();
        centerContainer.getChildren().add(node);
    }
    
    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE); 
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333; -fx-alignment: center-left; -fx-padding: 10 20;");
        btn.setFont(Font.font("Segoe UI", 14));
        
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #E3F2FD; -fx-text-fill: #2196F3; -fx-alignment: center-left; -fx-padding: 10 20; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333; -fx-alignment: center-left; -fx-padding: 10 20;"));
        
        return btn;
    }
    
    private void showWelcomeScreen() {
        centerContainer.getChildren().clear();
        
        VBox welcomeBox = new VBox(15);
        welcomeBox.setAlignment(Pos.CENTER);
        
        Label lblWelcome = new Label("Selamat Datang di RestoMate");
        lblWelcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        lblWelcome.setTextFill(Color.web("#333333"));
        
        Label lblSub = new Label("Pilih menu di sidebar sebelah kiri untuk mulai kerja.");
        lblSub.setFont(Font.font("Segoe UI", 16));
        lblSub.setTextFill(Color.GRAY);
        
        welcomeBox.getChildren().addAll(lblWelcome, lblSub);
        centerContainer.getChildren().add(welcomeBox);
    }
    
    public BorderPane getView() {
        return root;
    }
}
