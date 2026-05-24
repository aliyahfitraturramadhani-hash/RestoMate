package com.restomate.controllers;

import com.restomate.dao.MenuDAO;
import com.restomate.dao.TransactionDAO;
import com.restomate.models.Makanan;
import com.restomate.models.MenuRestoran;
import com.restomate.models.Minuman;
import com.restomate.models.Transaction;
import com.restomate.models.TransactionItem;
import com.restomate.views.CashierView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.effect.DropShadow;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CashierController {
    private CashierView view;
    private MenuDAO menuDAO;
    private TransactionDAO transactionDAO;
    private ObservableList<CartItem> cartItems; 

    public CashierController(CashierView view) {
        this.view = view;
        this.menuDAO = new MenuDAO();
        this.transactionDAO = new TransactionDAO();
        this.cartItems = FXCollections.observableArrayList();
        
        setupCartTable();
        // Setup listener buat fitur baru (Search & Filter)
        setupSearchAndFilter();
        
        loadMenus();
        setupActions();
    }

    private void setupCartTable() {
        TableView<CartItem> table = view.getCartTable();
        
        TableColumn<CartItem, String> colName = new TableColumn<>("Item");
        colName.setCellValueFactory(data -> data.getValue().namaProperty());
        colName.setPrefWidth(150);
        
        TableColumn<CartItem, Number> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(data -> data.getValue().qtyProperty());
        colQty.setPrefWidth(50);
        
        TableColumn<CartItem, String> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(data -> {
            java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
            return new SimpleStringProperty("Rp " + formatter.format(data.getValue().getSubtotal()));
        });
        colSubtotal.setPrefWidth(100);
        
        table.getColumns().addAll(colName, colQty, colSubtotal);
        table.setItems(cartItems);
    }
    
    // Fitur pencarian otomatis nge-refresh kartu tiap kali kita ngetik
    private void setupSearchAndFilter() {
        // Setiap kali ada ketikan baru di search bar, langsung render ulang!
        view.getTxtSearch().textProperty().addListener((obs, oldVal, newVal) -> {
            loadMenus();
        });
        
        // Setiap kali ComboBox filter diganti (Makanan/Minuman), render ulang juga!
        view.getCmbFilter().setOnAction(e -> {
            loadMenus();
        });
    }

    public void refresh() {
        loadMenus();
    }

    // Fungsi canggih buat ngambil data, ngefilter, dan ngerender jadi kartu
    public void loadMenus() {
        List<MenuRestoran> menus = menuDAO.getAllMenus();
        view.getMenuGrid().getChildren().clear();
        
        if (menus == null || menus.isEmpty()) {
            Label emptyLbl = new Label("Yah, belum ada menu nih di database. Tambahin lewat layar Kelola Menu dulu ya!");
            emptyLbl.setFont(Font.font("Segoe UI", 16));
            emptyLbl.setTextFill(Color.GRAY);
            view.getMenuGrid().add(emptyLbl, 0, 0);
            return;
        }
        
        // Ambil nilai filter saat ini
        String searchQuery = view.getTxtSearch().getText().toLowerCase();
        String filterCat = view.getCmbFilter().getValue();

        int col = 0;
        int row = 0;
        int matchCount = 0;
        
        for (MenuRestoran menu : menus) {
            // Syarat 1: Stok masih ada
            if (menu.getStok() <= 0) continue;
            
            // Syarat 2: Filter Kategori (kalau bukan "SEMUA", ya harus cocok)
            if (!"SEMUA".equals(filterCat) && !menu.getKategori().equalsIgnoreCase(filterCat)) {
                continue;
            }
            
            // Syarat 3: Filter Pencarian Nama
            if (!searchQuery.isEmpty() && !menu.getNama().toLowerCase().contains(searchQuery)) {
                continue;
            }
            
            VBox card = createMenuCard(menu);
            view.getMenuGrid().add(card, col, row);
            matchCount++;
            
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
        
        // Kalau abis difilter ternyata gak ada yang cocok
        if (matchCount == 0) {
            Label emptyLbl = new Label("Gak nemu menu yang dicari nih. Coba kata kunci atau kategori lain.");
            emptyLbl.setFont(Font.font("Segoe UI", 14));
            emptyLbl.setTextFill(Color.GRAY);
            view.getMenuGrid().add(emptyLbl, 0, 0);
        }
    }

    private VBox createMenuCard(MenuRestoran menu) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefSize(180, 240);
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.1));
        shadow.setRadius(5);
        shadow.setOffsetY(3);
        card.setEffect(shadow);
        
        Label lblName = new Label(menu.getNama());
        lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblName.setWrapText(true);
        lblName.setAlignment(Pos.CENTER);
        
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
        Label lblPrice = new Label("Rp " + formatter.format(menu.getHarga()));
        lblPrice.setTextFill(Color.web("#2196F3"));
        lblPrice.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        
        String extraInfo = "";
        if (menu instanceof Makanan) {
            Makanan m = (Makanan) menu;
            extraInfo = "Lv. Pedas: " + (m.getTingkatPedas() != null ? m.getTingkatPedas() : "-");
        } else if (menu instanceof Minuman) {
            Minuman m = (Minuman) menu;
            extraInfo = m.isDingin() ? "❄️ Dingin" : "🔥 Panas";
        }
        Label lblExtra = new Label(extraInfo);
        lblExtra.setTextFill(Color.GRAY);
        
        Label lblStok = new Label("Stok: " + menu.getStok());
        lblStok.setFont(Font.font("Segoe UI", 12));
        
        Button btnAdd = new Button("+ Keranjang");
        btnAdd.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        
        btnAdd.setOnMouseEntered(e -> {
            btnAdd.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
            shadow.setRadius(10); 
        });
        btnAdd.setOnMouseExited(e -> {
            btnAdd.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
            shadow.setRadius(5);
        });
        
        btnAdd.setOnAction(e -> addToCart(menu));
        
        String gambar = menu.getGambar();
        if (gambar != null && !gambar.isEmpty()) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image("file:src/main/resources/images/" + gambar);
                javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
                imgView.setFitWidth(120);
                imgView.setFitHeight(100);
                imgView.setPreserveRatio(true);
                card.getChildren().add(imgView);
            } catch (Exception ex) {
                System.err.println("Gagal load gambar: " + ex.getMessage());
            }
        }
        
        card.getChildren().addAll(lblName, lblPrice, lblExtra, lblStok, btnAdd);
        return card;
    }

    private void addToCart(MenuRestoran menu) {
        for (CartItem item : cartItems) {
            if (item.getMenuId() == menu.getId()) {
                if (item.getQty() < menu.getStok()) {
                    item.setQty(item.getQty() + 1);
                    item.setSubtotal(item.getQty() * menu.getHarga());
                    updateTotal();
                    view.getCartTable().refresh(); 
                } else {
                    showAlert(Alert.AlertType.WARNING, "Stok Habis", "Udah gak bisa nambah lagi, stok tinggal " + menu.getStok());
                }
                return;
            }
        }
        
        cartItems.add(new CartItem(menu.getId(), menu.getNama(), 1, menu.getHarga()));
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
        view.getLblTotal().setText("Rp " + formatter.format(total));
    }

    private void setupActions() {
        // Logika Hapus Item
        view.getBtnRemove().setOnAction(e -> {
            CartItem selected = view.getCartTable().getSelectionModel().getSelectedItem();
            if (selected != null) {
                cartItems.remove(selected);
                updateTotal();
            } else {
                showAlert(Alert.AlertType.WARNING, "Pilih Item", "Klik dulu item di keranjang yang mau dihapus!");
            }
        });
        
        // Logika Bayar
        view.getBtnPay().setOnAction(e -> {
            if (cartItems.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Keranjang Kosong", "Pilih menu dulu dong sebelum bayar.");
                return;
            }
            
            double total = 0;
            List<TransactionItem> dbItems = new ArrayList<>();
            for (CartItem ci : cartItems) {
                total += ci.getSubtotal();
                TransactionItem ti = new TransactionItem(0, 0, ci.getMenuId(), ci.getQty(), ci.getSubtotal());
                dbItems.add(ti);
            }
            
            Transaction tx = new Transaction(0, total, view.getCmbPayment().getValue(), view.getTxtCatatan().getText(), LocalDateTime.now());
            
            boolean success = transactionDAO.saveTransaction(tx, dbItems);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses!", "Pembayaran berhasil disimpan. Terima kasih!");
                cartItems.clear();
                updateTotal();
                // Sengaja kosongin search dan filter ke default lagi pas abis bayar biar lega
                view.getTxtSearch().clear();
                view.getCmbFilter().setValue("SEMUA");
                view.getTxtCatatan().clear();
                loadMenus(); 
            } else {
                showAlert(Alert.AlertType.ERROR, "Waduh Error", "Gagal menyimpan transaksi ke database.");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Kelas dummy untuk di-bind ke JavaFX TableView
    public static class CartItem {
        private final SimpleIntegerProperty menuId;
        private final SimpleStringProperty nama;
        private final SimpleIntegerProperty qty;
        private final SimpleDoubleProperty subtotal;

        public CartItem(int menuId, String nama, int qty, double subtotal) {
            this.menuId = new SimpleIntegerProperty(menuId);
            this.nama = new SimpleStringProperty(nama);
            this.qty = new SimpleIntegerProperty(qty);
            this.subtotal = new SimpleDoubleProperty(subtotal);
        }

        public int getMenuId() { return menuId.get(); }
        public int getQty() { return qty.get(); }
        public void setQty(int value) { qty.set(value); }
        public double getSubtotal() { return subtotal.get(); }
        public void setSubtotal(double value) { subtotal.set(value); }
        
        public SimpleStringProperty namaProperty() { return nama; }
        public SimpleIntegerProperty qtyProperty() { return qty; }
        public SimpleDoubleProperty subtotalProperty() { return subtotal; }
    }
}
