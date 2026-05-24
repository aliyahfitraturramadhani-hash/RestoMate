package com.restomate.controllers;

import com.restomate.dao.MenuDAO;
import com.restomate.models.Makanan;
import com.restomate.models.MenuRestoran;
import com.restomate.models.Minuman;
import com.restomate.views.ManageMenuView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class ManageMenuController {

    private ManageMenuView view;
    private MenuDAO menuDAO;
    private ObservableList<MenuRestoran> menuList;
    
    // Simpan semua data ori di sini biar gampang difilter
    private List<MenuRestoran> allMenus;
    private java.io.File selectedImageFile = null;

    public ManageMenuController(ManageMenuView view) {
        this.view = view;
        this.menuDAO = new MenuDAO();
        this.menuList = FXCollections.observableArrayList();
        
        setupTable();
        setupSearchAndFilter();
        setupActions();
        
        loadData();
    }

    private void setupTable() {
        TableView<MenuRestoran> table = view.getTableMenu();
        
        TableColumn<MenuRestoran, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        colId.setPrefWidth(50);
        
        TableColumn<MenuRestoran, String> colNama = new TableColumn<>("Nama Menu");
        colNama.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNama()));
        colNama.setPrefWidth(200);
        
        TableColumn<MenuRestoran, String> colKat = new TableColumn<>("Kategori");
        colKat.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKategori()));
        colKat.setPrefWidth(100);
        
        TableColumn<MenuRestoran, String> colHarga = new TableColumn<>("Harga");
        colHarga.setCellValueFactory(data -> {
            java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
            return new SimpleStringProperty("Rp " + formatter.format(data.getValue().getHarga()));
        });
        colHarga.setPrefWidth(120);
        
        TableColumn<MenuRestoran, String> colStok = new TableColumn<>("Stok");
        colStok.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStok())));
        colStok.setPrefWidth(70);
        
        TableColumn<MenuRestoran, String> colDetail = new TableColumn<>("Keterangan Tambahan");
        colDetail.setCellValueFactory(data -> {
            MenuRestoran menu = data.getValue();
            if (menu instanceof Makanan) {
                return new SimpleStringProperty("🌶️ Pedas: " + ((Makanan) menu).getTingkatPedas());
            } else if (menu instanceof Minuman) {
                return new SimpleStringProperty(((Minuman) menu).isDingin() ? "❄️ Es/Dingin" : "🔥 Panas");
            }
            return new SimpleStringProperty("-");
        });
        colDetail.setPrefWidth(180);
        
        table.getColumns().addAll(colId, colNama, colKat, colHarga, colStok, colDetail);
        table.setItems(menuList);
        
        // Event kalau baris tabel diklik, langsung oper datanya ke form sebelah kanan
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                fillForm(newSel);
            }
        });
    }

    private void setupSearchAndFilter() {
        // Nge-filter otomatis pas user ngetik di search bar
        view.getTxtSearch().textProperty().addListener((obs, old, newVal) -> {
            applyFilters();
        });
        
        // Nge-filter pas user ganti dropdown kategori
        view.getCmbFilter().setOnAction(e -> {
            applyFilters();
        });
    }

    public void refresh() {
        loadData();
    }

    private void loadData() {
        // Ambil data seger dari database
        allMenus = menuDAO.getAllMenus();
        applyFilters(); // Langsung terapkan filter yang lagi aktif
    }
    
    // Fungsi khusus buat nge-filter list
    private void applyFilters() {
        if (allMenus == null) return;
        
        String searchQuery = view.getTxtSearch().getText().toLowerCase();
        String filterCat = view.getCmbFilter().getValue();
        
        menuList.clear();
        for (MenuRestoran menu : allMenus) {
            // Cek Kategori
            if (!"SEMUA".equals(filterCat) && !menu.getKategori().equalsIgnoreCase(filterCat)) {
                continue;
            }
            // Cek Pencarian
            if (!searchQuery.isEmpty() && !menu.getNama().toLowerCase().contains(searchQuery)) {
                continue;
            }
            menuList.add(menu);
        }
    }

    private void fillForm(MenuRestoran menu) {
        view.getTxtId().setText(String.valueOf(menu.getId()));
        view.getTxtNama().setText(menu.getNama());
        view.getTxtHarga().setText(String.valueOf((int)menu.getHarga()));
        view.getTxtStok().setText(String.valueOf(menu.getStok()));
        view.getCmbKategori().setValue(menu.getKategori());
        
        view.getTxtTingkatPedas().setText("");
        view.getChkDingin().setSelected(false);
        
        selectedImageFile = null;
        if (menu.getGambar() != null && !menu.getGambar().isEmpty()) {
            view.getLblNamaGambar().setText(menu.getGambar());
        } else {
            view.getLblNamaGambar().setText("Belum ada gambar");
        }
        
        if (menu instanceof Makanan) {
            view.getTxtTingkatPedas().setText(((Makanan) menu).getTingkatPedas());
        } else if (menu instanceof Minuman) {
            view.getChkDingin().setSelected(((Minuman) menu).isDingin());
        }
    }

    private void clearForm() {
        view.getTxtId().clear();
        view.getTxtNama().clear();
        view.getTxtHarga().clear();
        view.getTxtStok().clear();
        view.getTxtTingkatPedas().clear();
        view.getChkDingin().setSelected(false);
        view.getCmbKategori().setValue("MAKANAN"); 
        
        selectedImageFile = null;
        view.getLblNamaGambar().setText("Belum ada gambar");
        
        view.getTableMenu().getSelectionModel().clearSelection();
    }

    private void setupActions() {
        // Pilih Gambar
        view.getBtnPilihGambar().setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Pilih Gambar Menu");
            fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            java.io.File file = fileChooser.showOpenDialog(view.getView().getScene().getWindow());
            if (file != null) {
                selectedImageFile = file;
                view.getLblNamaGambar().setText(file.getName());
            }
        });
        
        // Simpan Data (Insert atau Update)
        view.getBtnSave().setOnAction(e -> {
            try {
                String nama = view.getTxtNama().getText();
                String hargaText = view.getTxtHarga().getText();
                String stokText = view.getTxtStok().getText();
                
                if (nama.isEmpty() || hargaText.isEmpty() || stokText.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Isian Belum Lengkap", "Nama, Harga, dan Stok wajib diisi ya bos!");
                    return;
                }
                
                double harga = Double.parseDouble(hargaText);
                int stok = Integer.parseInt(stokText);
                String kategori = view.getCmbKategori().getValue();
                
                MenuRestoran existing = view.getTableMenu().getSelectionModel().getSelectedItem();
                String gambarName = (existing != null && !view.getTxtId().getText().isEmpty()) ? existing.getGambar() : null;

                if (selectedImageFile != null) {
                    try {
                        java.nio.file.Path destDir = java.nio.file.Paths.get("src/main/resources/images");
                        if (!java.nio.file.Files.exists(destDir)) {
                            java.nio.file.Files.createDirectories(destDir);
                        }
                        gambarName = System.currentTimeMillis() + "_" + selectedImageFile.getName();
                        java.nio.file.Path destPath = destDir.resolve(gambarName);
                        java.nio.file.Files.copy(selectedImageFile.toPath(), destPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception ex) {
                        System.err.println("Gagal copy gambar: " + ex.getMessage());
                    }
                }
                
                MenuRestoran menu;
                if ("MAKANAN".equals(kategori)) {
                    String pedas = view.getTxtTingkatPedas().getText();
                    menu = new Makanan(0, nama, harga, stok, gambarName, pedas);
                } else {
                    boolean dingin = view.getChkDingin().isSelected();
                    menu = new Minuman(0, nama, harga, stok, gambarName, dingin);
                }
                
                String idStr = view.getTxtId().getText();
                boolean success;
                
                if (idStr.isEmpty()) {
                    success = menuDAO.addMenu(menu);
                } else {
                    menu.setId(Integer.parseInt(idStr));
                    success = menuDAO.updateMenu(menu);
                }
                
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses!", "Data menu berhasil diamankan ke database.");
                    clearForm();
                    loadData(); // Tarik ulang biar tabelnya seger
                } else {
                    showAlert(Alert.AlertType.ERROR, "Waduh Gagal", "Ada masalah pas nyimpen ke database nih.");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.WARNING, "Salah Format", "Pastikan isian Harga dan Stok hanya mengandung angka!");
            }
        });
        
        // Hapus Data
        view.getBtnDelete().setOnAction(e -> {
            MenuRestoran selected = view.getTableMenu().getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean success = menuDAO.deleteMenu(selected.getId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Sayonara!", "Menu '" + selected.getNama() + "' resmi dihapus.");
                    clearForm();
                    loadData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal Hapus", "Sistem gagal menghapus data dari database.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Pilih Dulu Dong", "Klik salah satu menu di tabel sebelum menekan tombol hapus.");
            }
        });
        
        // Bersihkan Form
        view.getBtnClear().setOnAction(e -> clearForm());
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
