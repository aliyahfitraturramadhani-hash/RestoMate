package com.restomate.views;

import com.restomate.controllers.CashierController;
import com.restomate.models.MenuRestoran;
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
    private TableView<MenuRestoran> menuTable;
    private TableView<CashierController.CartItem> cartTable;
    private Label lblTotal;
    private ComboBox<String> cmbPayment;
    private Button btnPay;
    
    // Fitur baru
    private TextField txtSearch;
    private ComboBox<String> cmbFilter;
    private Button btnClearCart;
    private TextField txtCatatan;
    
    // Professional POS features
    private ComboBox<String> cmbDiscount;
    private TextField txtAmountPaid;
    private Label lblChange;
    private Label lblSubtotal;
    private Label lblDiscountAmount;
    private Label lblTaxAmount;

    public CashierView() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // --- BAGIAN TENGAH (Top Bar + Grid Menu) ---
        VBox centerArea = new VBox(10);
        
        // 1. Top Bar buat Search dan Filter
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Cari nama menu (F3)...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");
        
        cmbFilter = new ComboBox<>();
        cmbFilter.getItems().addAll("SEMUA", "MAKANAN", "MINUMAN");
        cmbFilter.setValue("SEMUA");
        cmbFilter.setStyle("-fx-font-size: 14px; -fx-pref-height: 36px; -fx-background-radius: 5;");
        
        topBar.getChildren().addAll(new Label("Cari:"), txtSearch, new Label("Kategori:"), cmbFilter);
        
        // 2. Setup Tabel Utama buat nyimpen list menu
        menuTable = new TableView<>();
        menuTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        menuTable.setPlaceholder(new Label("Gak nemu menu yang dicari nih. Coba kata kunci atau kategori lain."));
        menuTable.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
        VBox.setVgrow(menuTable, Priority.ALWAYS); // Biar menuhin sisa layar ke bawah
        
        centerArea.getChildren().addAll(topBar, menuTable);
        root.setCenter(centerArea);
        
        // --- BAGIAN KANAN (Keranjang Belanja) ---
        VBox rightSidebar = buildCartSidebar();
        root.setRight(rightSidebar);
        
        // Panggil controllernya buat ngatur logika di baliknya
        controller = new CashierController(this);
    }
    
    private VBox buildCartSidebar() {
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(15));
        sidebar.setPrefWidth(370);
        sidebar.setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF"), new CornerRadii(10), Insets.EMPTY)));
        sidebar.setBorder(new Border(new BorderStroke(Color.web("#E0E0E0"), BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        BorderPane.setMargin(sidebar, new Insets(0, 0, 0, 15));
        
        Label lblTitle = new Label("Keranjang Belanja");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        cartTable = new TableView<>();
        cartTable.setPlaceholder(new Label("Keranjang kosong."));
        cartTable.setPrefHeight(220); 
        VBox.setVgrow(cartTable, Priority.ALWAYS); 
        
        // Tombol Bersihkan Keranjang
        btnClearCart = new Button("🧹 Bersihkan Keranjang (ESC)");
        btnClearCart.setMaxWidth(Double.MAX_VALUE);
        btnClearCart.setStyle("-fx-background-color: #FFCDD2; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8;");
        btnClearCart.setOnMouseEntered(e -> btnClearCart.setStyle("-fx-background-color: #EF5350; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8; -fx-cursor: hand;"));
        btnClearCart.setOnMouseExited(e -> btnClearCart.setStyle("-fx-background-color: #FFCDD2; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8;"));
        
        // Panel Detail Diskon & Catatan
        GridPane detailsGrid = new GridPane();
        detailsGrid.setVgap(8);
        detailsGrid.setHgap(10);
        
        Label lblDiscTitle = new Label("Diskon:");
        lblDiscTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        cmbDiscount = new ComboBox<>();
        cmbDiscount.getItems().addAll("0%", "5%", "10%", "15%", "20%");
        cmbDiscount.setValue("0%");
        cmbDiscount.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(cmbDiscount, Priority.ALWAYS);

        txtCatatan = new TextField();
        txtCatatan.setPromptText("📝 Catatan pesanan...");
        txtCatatan.setStyle("-fx-font-size: 13px; -fx-padding: 6px; -fx-background-radius: 5;");
        
        detailsGrid.add(lblDiscTitle, 0, 0);
        detailsGrid.add(cmbDiscount, 1, 0);
        detailsGrid.add(new Label("Catatan:"), 0, 1);
        detailsGrid.add(txtCatatan, 1, 1);

        // Panel Pembayaran (Metode & Uang Dibayar)
        GridPane paymentGrid = new GridPane();
        paymentGrid.setVgap(8);
        paymentGrid.setHgap(10);

        Label lblPayMethod = new Label("Metode:");
        lblPayMethod.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        cmbPayment = new ComboBox<>();
        cmbPayment.getItems().addAll("CASH", "QRIS");
        cmbPayment.setValue("CASH");
        cmbPayment.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(cmbPayment, Priority.ALWAYS);

        Label lblAmountPaid = new Label("Bayar (Rp):");
        lblAmountPaid.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        txtAmountPaid = new TextField();
        txtAmountPaid.setPromptText("Uang diterima...");
        txtAmountPaid.setStyle("-fx-font-size: 13px; -fx-padding: 6px; -fx-background-radius: 5;");
        GridPane.setHgrow(txtAmountPaid, Priority.ALWAYS);

        paymentGrid.add(lblPayMethod, 0, 0);
        paymentGrid.add(cmbPayment, 1, 0);
        paymentGrid.add(lblAmountPaid, 0, 1);
        paymentGrid.add(txtAmountPaid, 1, 1);

        // Rincian Biaya
        GridPane costGrid = new GridPane();
        costGrid.setVgap(6);
        costGrid.setHgap(15);
        costGrid.setPadding(new Insets(10, 0, 10, 0));
        costGrid.setBorder(new Border(new BorderStroke(Color.web("#EEEEEE"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 0, 1, 0))));

        lblSubtotal = new Label("Rp 0");
        lblDiscountAmount = new Label("Rp 0");
        lblTaxAmount = new Label("Rp 0");
        lblTotal = new Label("Rp 0");
        lblTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblTotal.setTextFill(Color.web("#D32F2F"));
        lblChange = new Label("Rp 0");
        lblChange.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblChange.setTextFill(Color.GRAY);

        costGrid.add(new Label("Subtotal:"), 0, 0);
        costGrid.add(lblSubtotal, 1, 0);
        costGrid.add(new Label("Diskon:"), 0, 1);
        costGrid.add(lblDiscountAmount, 1, 1);
        costGrid.add(new Label("Pajak PB1 (10%):"), 0, 2);
        costGrid.add(lblTaxAmount, 1, 2);
        
        Label lblTotalTitle = new Label("Total:");
        lblTotalTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        costGrid.add(lblTotalTitle, 0, 3);
        costGrid.add(lblTotal, 1, 3);
        
        Label lblChangeTitle = new Label("Kembalian:");
        lblChangeTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        costGrid.add(lblChangeTitle, 0, 4);
        costGrid.add(lblChange, 1, 4);

        // Make right column of costGrid grow to push content to the right
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setHalignment(Pos.CENTER_RIGHT.getHpos());
        costGrid.getColumnConstraints().addAll(col1, col2);

        btnPay = new Button("Proses Pembayaran (F5)");
        btnPay.setMaxWidth(Double.MAX_VALUE);
        btnPay.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 40px; -fx-font-size: 14px; -fx-background-radius: 5;");
        btnPay.setOnMouseEntered(e -> btnPay.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 40px; -fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnPay.setOnMouseExited(e -> btnPay.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 40px; -fx-font-size: 14px; -fx-background-radius: 5;"));
        
        sidebar.getChildren().addAll(
            lblTitle, 
            cartTable, 
            btnClearCart, 
            new Separator(),
            detailsGrid, 
            new Separator(),
            paymentGrid, 
            costGrid, 
            btnPay
        );
        return sidebar;
    }

    // Biar Controller bisa ngakses UI buat diedit-edit
    public BorderPane getView() { return root; }
    public TableView<MenuRestoran> getMenuTable() { return menuTable; }
    public TableView<CashierController.CartItem> getCartTable() { return cartTable; }
    public Label getLblTotal() { return lblTotal; }
    public ComboBox<String> getCmbPayment() { return cmbPayment; }
    public Button getBtnPay() { return btnPay; }
    public TextField getTxtSearch() { return txtSearch; }
    public ComboBox<String> getCmbFilter() { return cmbFilter; }
    public Button getBtnClearCart() { return btnClearCart; }
    public TextField getTxtCatatan() { return txtCatatan; }
    
    public ComboBox<String> getCmbDiscount() { return cmbDiscount; }
    public TextField getTxtAmountPaid() { return txtAmountPaid; }
    public Label getLblChange() { return lblChange; }
    public Label getLblSubtotal() { return lblSubtotal; }
    public Label getLblDiscountAmount() { return lblDiscountAmount; }
    public Label getLblTaxAmount() { return lblTaxAmount; }
    public CashierController getController() { return controller; }
}
