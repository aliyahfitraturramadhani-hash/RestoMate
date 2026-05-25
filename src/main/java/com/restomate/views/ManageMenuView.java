package com.restomate.views;

import com.restomate.controllers.ManageMenuController;
import com.restomate.models.MenuRestoran;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;

public class ManageMenuView {
    private BorderPane root;
    private ManageMenuController controller;
    
    // Fitur List & Filter
    private TableView<MenuRestoran> tableMenu;
    private TextField txtSearch;
    private ComboBox<String> cmbFilter;
    private Button btnDelete;
    
    // Komponen-komponen form input
    private TextField txtId, txtNama, txtHarga, txtStok, txtTingkatPedas;
    private ComboBox<String> cmbKategori;
    private CheckBox chkDingin;
    private Button btnSave, btnClear;
    
    // Stats labels
    private Label lblTotalMenu;
    private Label lblTotalMakanan;
    private Label lblTotalMinuman;
    private Label lblTotalKritis;
    
    // CSV buttons
    private Button btnExportCsv;
    private Button btnImportCsv;
    
    // Duplication button
    private Button btnDuplicate;
    
    private Label lblPedas;

    public ManageMenuView() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // --- Sisi Kiri/Tengah: Daftar Menu & Filter ---
        VBox centerArea = buildCenterArea();
        root.setCenter(centerArea);
        
        // --- Sisi Kanan: Form Input ---
        VBox rightForm = buildFormArea();
        root.setRight(rightForm);
        
        controller = new ManageMenuController(this);
    }
    
    private VBox buildCenterArea() {
        VBox centerBox = new VBox(15);
        
        // 0. Stats Bar
        HBox statsBar = buildStatsBar();
        
        // 1. Top Bar: Pencarian & Filter
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Cari nama menu...");
        txtSearch.setPrefWidth(300);
        txtSearch.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");
        
        cmbFilter = new ComboBox<>();
        cmbFilter.getItems().addAll("SEMUA", "MAKANAN", "MINUMAN");
        cmbFilter.setValue("SEMUA");
        cmbFilter.setStyle("-fx-font-size: 14px; -fx-pref-height: 36px; -fx-background-radius: 5;");
        
        topBar.getChildren().addAll(new Label("Cari:"), txtSearch, new Label("Kategori:"), cmbFilter);
        
        // 2. TableView
        tableMenu = new TableView<>();
        tableMenu.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tableMenu, Priority.ALWAYS);
        
        // 3. Bottom Bar: Tombol Hapus & Impor/Ekspor
        HBox bottomBar = new HBox(15);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        
        btnImportCsv = new Button("📥 Impor CSV");
        btnImportCsv.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnImportCsv.setOnMouseEntered(e -> btnImportCsv.setStyle("-fx-background-color: #BDBDBD; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnImportCsv.setOnMouseExited(e -> btnImportCsv.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;"));

        btnExportCsv = new Button("📤 Ekspor CSV");
        btnExportCsv.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnExportCsv.setOnMouseEntered(e -> btnExportCsv.setStyle("-fx-background-color: #BDBDBD; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnExportCsv.setOnMouseExited(e -> btnExportCsv.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;"));

        btnDelete = new Button("🗑️ Hapus Menu Terpilih");
        btnDelete.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        bottomBar.getChildren().addAll(btnImportCsv, btnExportCsv, spacer, btnDelete);
        
        centerBox.getChildren().addAll(statsBar, topBar, tableMenu, bottomBar);
        return centerBox;
    }

    private HBox buildStatsBar() {
        HBox bar = new HBox(15);
        bar.setPadding(new Insets(5, 0, 5, 0));
        bar.setAlignment(Pos.CENTER);
        
        VBox cardTotal = createStatsCard("TOTAL MENU", lblTotalMenu = new Label("0"), "#E3F2FD", "#1565C0");
        VBox cardMakanan = createStatsCard("MAKANAN", lblTotalMakanan = new Label("0"), "#E8F5E9", "#2E7D32");
        VBox cardMinuman = createStatsCard("MINUMAN", lblTotalMinuman = new Label("0"), "#FFF3E0", "#E65100");
        VBox cardKritis = createStatsCard("STOK KRITIS (<10)", lblTotalKritis = new Label("0"), "#FFEBEE", "#C62828");
        
        HBox.setHgrow(cardTotal, Priority.ALWAYS);
        HBox.setHgrow(cardMakanan, Priority.ALWAYS);
        HBox.setHgrow(cardMinuman, Priority.ALWAYS);
        HBox.setHgrow(cardKritis, Priority.ALWAYS);
        
        bar.getChildren().addAll(cardTotal, cardMakanan, cardMinuman, cardKritis);
        return bar;
    }
    
    private VBox createStatsCard(String title, Label lblValue, String bgHex, String textHex) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER);
        card.setBackground(new Background(new BackgroundFill(Color.web(bgHex), new CornerRadii(8), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(Color.web(textHex, 0.2), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));
        
        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        lblTitle.setTextFill(Color.web(textHex));
        
        lblValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        lblValue.setTextFill(Color.web(textHex));
        
        card.getChildren().addAll(lblTitle, lblValue);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.05));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        card.setEffect(shadow);
        
        return card;
    }
    
    private VBox buildFormArea() {
        VBox formCard = new VBox(20);
        formCard.setPadding(new Insets(25));
        formCard.setPrefWidth(380);
        formCard.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        BorderPane.setMargin(formCard, new Insets(0, 0, 0, 25));
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.05));
        shadow.setRadius(10);
        formCard.setEffect(shadow);
        
        Label lblTitle = new Label("📋 Detail Menu");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        lblTitle.setTextFill(Color.web("#333333"));
        
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(15);
        
        // Buat ngerapihin lebar kolom Label vs Textfield biar lurus sempurna
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(90); // Kolom untuk label
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS); // Kolom untuk isian form (melar maksimal)
        grid.getColumnConstraints().addAll(col1, col2);
        
        txtId = new TextField();
        txtId.setDisable(true); 
        txtId.setPromptText("Otomatis");
        txtId.setStyle("-fx-font-size: 14px; -fx-background-color: #F5F5F5;");
        txtId.setMaxWidth(Double.MAX_VALUE);
        
        txtNama = new TextField();
        txtNama.setStyle("-fx-font-size: 14px;");
        txtNama.setMaxWidth(Double.MAX_VALUE);
        
        txtHarga = new TextField();
        txtHarga.setPromptText("Contoh: 15000");
        txtHarga.setStyle("-fx-font-size: 14px;");
        txtHarga.setMaxWidth(Double.MAX_VALUE);
        
        txtStok = new TextField();
        txtStok.setPromptText("Contoh: 50");
        txtStok.setStyle("-fx-font-size: 14px;");
        txtStok.setMaxWidth(Double.MAX_VALUE);
        
        cmbKategori = new ComboBox<>();
        cmbKategori.getItems().addAll("MAKANAN", "MINUMAN");
        cmbKategori.setValue("MAKANAN");
        cmbKategori.setMaxWidth(Double.MAX_VALUE);
        cmbKategori.setStyle("-fx-font-size: 14px;");
        
        txtTingkatPedas = new TextField();
        txtTingkatPedas.setPromptText("Contoh: Level 3");
        txtTingkatPedas.setStyle("-fx-font-size: 14px;");
        txtTingkatPedas.setMaxWidth(Double.MAX_VALUE);
        
        chkDingin = new CheckBox("Disajikan dengan Es (Dingin)");
        chkDingin.setFont(Font.font("Segoe UI", 14));
        
        lblPedas = new Label("Lv. Pedas:");
        lblPedas.setFont(Font.font("Segoe UI", 14));
        
        Label l1 = new Label("ID Menu:"); l1.setFont(Font.font("Segoe UI", 14));
        Label l2 = new Label("Nama:"); l2.setFont(Font.font("Segoe UI", 14));
        Label l3 = new Label("Kategori:"); l3.setFont(Font.font("Segoe UI", 14));
        Label l4 = new Label("Harga:"); l4.setFont(Font.font("Segoe UI", 14));
        Label l5 = new Label("Stok:"); l5.setFont(Font.font("Segoe UI", 14));
        
        // Kita masukkan SEKARANG field spesifik (Level Pedas & Dingin) ke dalam GridPane
        // biar posisinya benar-benar sejajar dengan isian yang lain
        grid.add(l1, 0, 0); grid.add(txtId, 1, 0);
        grid.add(l2, 0, 1); grid.add(txtNama, 1, 1);
        grid.add(l3, 0, 2); grid.add(cmbKategori, 1, 2);
        grid.add(l4, 0, 3); grid.add(txtHarga, 1, 3);
        grid.add(l5, 0, 4); grid.add(txtStok, 1, 4);
        grid.add(lblPedas, 0, 5); grid.add(txtTingkatPedas, 1, 5);
        grid.add(chkDingin, 1, 6); 
        
        // Default awal: Dingin disembunyikan karena kategori awal adalah MAKANAN
        chkDingin.setVisible(false);
        chkDingin.setManaged(false);
        
        cmbKategori.setOnAction(e -> {
            if ("MAKANAN".equals(cmbKategori.getValue())) {
                lblPedas.setVisible(true); lblPedas.setManaged(true);
                txtTingkatPedas.setVisible(true); txtTingkatPedas.setManaged(true);
                
                chkDingin.setVisible(false); chkDingin.setManaged(false);
            } else {
                lblPedas.setVisible(false); lblPedas.setManaged(false);
                txtTingkatPedas.setVisible(false); txtTingkatPedas.setManaged(false);
                
                chkDingin.setVisible(true); chkDingin.setManaged(true);
            }
        });
        
        // Area Tombol Form
        VBox actionBox = new VBox(10);
        actionBox.setPadding(new Insets(20, 0, 0, 0));
        
        btnSave = new Button("💾 Simpan Data");
        btnSave.setMaxWidth(Double.MAX_VALUE);
        btnSave.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-font-size: 14px; -fx-background-radius: 5;");
        btnSave.setOnMouseEntered(e -> btnSave.setStyle("-fx-background-color: #45A049; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnSave.setOnMouseExited(e -> btnSave.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-font-size: 14px; -fx-background-radius: 5;"));
        
        btnDuplicate = new Button("👥 Duplikat Menu Terpilih");
        btnDuplicate.setMaxWidth(Double.MAX_VALUE);
        btnDuplicate.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-font-size: 14px; -fx-background-radius: 5;");
        btnDuplicate.setOnMouseEntered(e -> btnDuplicate.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnDuplicate.setOnMouseExited(e -> btnDuplicate.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-font-size: 14px; -fx-background-radius: 5;"));

        btnClear = new Button("✨ Bersihkan Form");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setStyle("-fx-background-color: #F5F5F5; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-padding: 12; -fx-font-size: 14px; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5;");
        btnClear.setOnMouseEntered(e -> btnClear.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-padding: 12; -fx-font-size: 14px; -fx-border-color: #BDBDBD; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnClear.setOnMouseExited(e -> btnClear.setStyle("-fx-background-color: #F5F5F5; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-padding: 12; -fx-font-size: 14px; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5;"));
        
        actionBox.getChildren().addAll(btnSave, btnDuplicate, btnClear);
        
        formCard.getChildren().addAll(lblTitle, grid, actionBox);
        return formCard;
    }

    // Getter buat Controller
    public BorderPane getView() { return root; }
    public TableView<MenuRestoran> getTableMenu() { return tableMenu; }
    public TextField getTxtSearch() { return txtSearch; }
    public ComboBox<String> getCmbFilter() { return cmbFilter; }
    public Button getBtnDelete() { return btnDelete; }
    
    public TextField getTxtId() { return txtId; }
    public TextField getTxtNama() { return txtNama; }
    public TextField getTxtHarga() { return txtHarga; }
    public TextField getTxtStok() { return txtStok; }
    public TextField getTxtTingkatPedas() { return txtTingkatPedas; }
    public ComboBox<String> getCmbKategori() { return cmbKategori; }
    public CheckBox getChkDingin() { return chkDingin; }
    public Button getBtnSave() { return btnSave; }
    public Button getBtnClear() { return btnClear; }
    
    public Label getLblTotalMenu() { return lblTotalMenu; }
    public Label getLblTotalMakanan() { return lblTotalMakanan; }
    public Label getLblTotalMinuman() { return lblTotalMinuman; }
    public Label getLblTotalKritis() { return lblTotalKritis; }
    
    public Button getBtnExportCsv() { return btnExportCsv; }
    public Button getBtnImportCsv() { return btnImportCsv; }
    public Button getBtnDuplicate() { return btnDuplicate; }

    public ManageMenuController getController() { return controller; }
}
