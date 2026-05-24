package restomate.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DashboardView {
    private Scene scene;
    private Stage primaryStage;
    private BorderPane root;

    public DashboardView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createView();
    }

    private ReservationView currentReservationView;

    private void switchView(VBox newView) {
        if (currentReservationView != null) {
            currentReservationView.stopPolling();
            currentReservationView = null;
        }
        root.setCenter(newView);
    }

    private void createView() {
        root = new BorderPane();

       
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);

        Label titleLabel = new Label("RestoMate");

        Button btnCashier = createSidebarButton("Menu Kasir");
        Button btnFood = createSidebarButton("Kelola Makanan");
        Button btnReservation = createSidebarButton("Catat Reservasi");
        Button btnReport = createSidebarButton("Laporan Harian");
        
        Button btnLogout = new Button("Logout");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        
        sidebar.getChildren().addAll(titleLabel, btnCashier, btnFood, btnReservation, btnReport, btnLogout);
        root.setLeft(sidebar);

      
        showWelcomeView();

    
        btnCashier.setOnAction(e -> switchView(new CashierView().getView()));
        btnFood.setOnAction(e -> switchView(new ManageFoodView().getView()));
        
        
        btnReservation.setOnAction(e -> {
            switchView(new VBox()); 
            currentReservationView = new ReservationView();
            root.setCenter(currentReservationView.getView());
        });

        btnReport.setOnAction(e -> switchView(new ReportView().getView()));
        
        btnLogout.setOnAction(e -> {
            if (currentReservationView != null) {
                currentReservationView.stopPolling();
                currentReservationView = null;
            }
            logout();
        });

        scene = new Scene(root, 1024, 768);
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void showWelcomeView() {
        VBox welcomeBox = new VBox();
        welcomeBox.setAlignment(Pos.CENTER);
        
        Label welcomeLabel = new Label("Selamat datang di Dashboard RestoMate!");
        
        Label descLabel = new Label("Pilih menu di sidebar untuk mulai menggunakan aplikasi.");
        
        welcomeBox.getChildren().addAll(welcomeLabel, descLabel);
        root.setCenter(welcomeBox);
    }

    private void logout() {
        LoginView loginView = new LoginView(primaryStage);
        primaryStage.setScene(loginView.getScene());
    }

    public Scene getScene() {
        return scene;
    }
}