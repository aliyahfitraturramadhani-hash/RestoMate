package com.restomate.views;

import com.restomate.controllers.ReservationController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ReservationView {
    private BorderPane root;
    private ReservationController controller;
    
    // UI Form
    private TextField txtNama, txtMeja;
    private DatePicker dpTanggal;
    private TextField txtJam; // Format HH:mm
    private Button btnSimpan;
    
    // UI Visualisasi Meja
    private GridPane tableGrid;

    public ReservationView() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // Form input di sebelah kiri
        VBox leftForm = buildForm();
        root.setLeft(leftForm);
        
        // Visualisasi meja di sebelah kanan/tengah
        VBox rightVisual = buildVisualArea();
        root.setCenter(rightVisual);
        
        // Panggil controller
        controller = new ReservationController(this);
    }
    
    private VBox buildForm() {
        VBox formCard = new VBox(15);
        formCard.setPadding(new Insets(20));
        formCard.setPrefWidth(300);
        formCard.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        BorderPane.setMargin(formCard, new Insets(0, 20, 0, 0));
        
        Label lblTitle = new Label("Catat Reservasi");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        txtNama = new TextField();
        txtNama.setPromptText("Nama Pelanggan");
        
        txtMeja = new TextField();
        txtMeja.setPromptText("Nomor Meja (Misal: 1)");
        
        dpTanggal = new DatePicker();
        dpTanggal.setPromptText("Pilih Tanggal");
        dpTanggal.setMaxWidth(Double.MAX_VALUE);
        
        txtJam = new TextField();
        txtJam.setPromptText("Jam (Format HH:mm)");
        
        btnSimpan = new Button("Booking Meja!");
        btnSimpan.setMaxWidth(Double.MAX_VALUE);
        btnSimpan.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        
        formCard.getChildren().addAll(lblTitle, new Label("Nama:"), txtNama, 
                                      new Label("Meja:"), txtMeja, 
                                      new Label("Tanggal:"), dpTanggal, 
                                      new Label("Waktu/Jam:"), txtJam, 
                                      btnSimpan);
        return formCard;
    }
    
    private VBox buildVisualArea() {
        VBox visualBox = new VBox(15);
        visualBox.setPadding(new Insets(20));
        visualBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        
        Label lblTitle = new Label("Ketersediaan Meja (Live 🔴)");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        tableGrid = new GridPane();
        tableGrid.setHgap(15);
        tableGrid.setVgap(15);
        tableGrid.setAlignment(Pos.CENTER);
        
        visualBox.getChildren().addAll(lblTitle, tableGrid);
        return visualBox;
    }

    public BorderPane getView() { return root; }
    public TextField getTxtNama() { return txtNama; }
    public TextField getTxtMeja() { return txtMeja; }
    public DatePicker getDpTanggal() { return dpTanggal; }
    public TextField getTxtJam() { return txtJam; }
    public Button getBtnSimpan() { return btnSimpan; }
    public GridPane getTableGrid() { return tableGrid; }
}
