package com.restomate.views;

import com.restomate.controllers.CashierController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CashierView {
    private BorderPane root;
    private CashierController controller;
    
    // UI Komponen yang butuh diakses sama Controller
    private GridPane menuGrid;
    private TableView<CashierController.CartItem> cartTable;
    private Label lblTotal;
    private ComboBox<String> cmbPayment;
    private Button btnPay;
    
    // Fitur baru
    private TextField txtSearch;
    private ComboBox<String> cmbFilter;
    private Button btnRemove;
    private TextField txtCatatan;

    public CashierView() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // --- BAGIAN TENGAH (Top Bar + Grid Menu) ---
        VBox centerArea = new VBox(10);
        
        // 1. Top Bar buat Search dan Filter
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Cari nama menu...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");
        
        cmbFilter = new ComboBox<>();
        cmbFilter.getItems().addAll("SEMUA", "MAKANAN", "MINUMAN");
        cmbFilter.setValue("SEMUA");
        cmbFilter.setStyle("-fx-font-size: 14px; -fx-pref-height: 36px; -fx-background-radius: 5;");
        
        topBar.getChildren().addAll(new Label("Cari:"), txtSearch, new Label("Kategori:"), cmbFilter);
        
        // 2. Setup Grid utama buat nyimpen kartu-kartu menu
        menuGrid = new GridPane();
        menuGrid.setHgap(20); 
        menuGrid.setVgap(20); 
        menuGrid.setPadding(new Insets(10));
        menuGrid.setAlignment(Pos.TOP_LEFT);
        
        ScrollPane scrollPane = new ScrollPane(menuGrid);
        scrollPane.setFitToWidth(true); 
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #F5F5F5;");
        scrollPane.setBorder(Border.EMPTY);
        VBox.setVgrow(scrollPane, Priority.ALWAYS); // Biar menuhin sisa layar ke bawah
        
        centerArea.getChildren().addAll(topBar, scrollPane);
        root.setCenter(centerArea);
        
        // --- BAGIAN KANAN (Keranjang Belanja) ---
        VBox rightSidebar = buildCartSidebar();
        root.setRight(rightSidebar);
        
        // Panggil controllernya buat ngatur logika di baliknya
        controller = new CashierController(this);
    }
    
    private VBox buildCartSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(350);
        sidebar.setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF"), new CornerRadii(10), Insets.EMPTY)));
        sidebar.setBorder(new Border(new BorderStroke(Color.web("#E0E0E0"), BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        BorderPane.setMargin(sidebar, new Insets(0, 0, 0, 20));
        
        Label lblTitle = new Label("Keranjang Belanja");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        cartTable = new TableView<>();
        cartTable.setPlaceholder(new Label("Keranjang kosong."));
        VBox.setVgrow(cartTable, Priority.ALWAYS); 
        
        // Tombol Hapus Item
        btnRemove = new Button("🗑️ Hapus Item Terpilih");
        btnRemove.setMaxWidth(Double.MAX_VALUE);
        btnRemove.setStyle("-fx-background-color: #FFCDD2; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnRemove.setOnMouseEntered(e -> btnRemove.setStyle("-fx-background-color: #EF5350; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnRemove.setOnMouseExited(e -> btnRemove.setStyle("-fx-background-color: #FFCDD2; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-background-radius: 5;"));
        
        HBox totalBox = new HBox(10);
        totalBox.setAlignment(Pos.CENTER_LEFT);
        Label lblTotalTitle = new Label("Total:");
        lblTotalTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblTotal = new Label("Rp 0");
        lblTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        lblTotal.setTextFill(Color.web("#D32F2F")); 
        totalBox.getChildren().addAll(lblTotalTitle, lblTotal);
        
        cmbPayment = new ComboBox<>();
        cmbPayment.getItems().addAll("CASH", "QRIS");
        cmbPayment.setValue("CASH");
        cmbPayment.setMaxWidth(Double.MAX_VALUE);
        cmbPayment.setStyle("-fx-font-size: 14px; -fx-pref-height: 40px;");
        
        btnPay = new Button("Proses Pembayaran");
        btnPay.setMaxWidth(Double.MAX_VALUE);
        btnPay.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 45px; -fx-font-size: 14px; -fx-background-radius: 5;");
        btnPay.setOnMouseEntered(e -> btnPay.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 45px; -fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnPay.setOnMouseExited(e -> btnPay.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 45px; -fx-font-size: 14px; -fx-background-radius: 5;"));
        
        txtCatatan = new TextField();
        txtCatatan.setPromptText("📝 Catatan pesanan (opsional)...");
        txtCatatan.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");
        
        sidebar.getChildren().addAll(lblTitle, cartTable, btnRemove, totalBox, txtCatatan, cmbPayment, btnPay);
        return sidebar;
    }

    // Biar Controller bisa ngakses UI buat diedit-edit
    public BorderPane getView() { return root; }
    public GridPane getMenuGrid() { return menuGrid; }
    public TableView<CashierController.CartItem> getCartTable() { return cartTable; }
    public Label getLblTotal() { return lblTotal; }
    public ComboBox<String> getCmbPayment() { return cmbPayment; }
    public Button getBtnPay() { return btnPay; }
    public TextField getTxtSearch() { return txtSearch; }
    public ComboBox<String> getCmbFilter() { return cmbFilter; }
    public Button getBtnRemove() { return btnRemove; }
    public TextField getTxtCatatan() { return txtCatatan; }
    public CashierController getController() { return controller; }
}
