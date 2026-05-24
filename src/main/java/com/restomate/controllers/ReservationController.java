package com.restomate.controllers;

import com.restomate.dao.ReservationDAO;
import com.restomate.models.Reservation;
import com.restomate.views.ReservationView;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReservationController {
    private ReservationView view;
    private ReservationDAO dao;
    private boolean isPolling = true; // Buat tanda kalau thread masih jalan
    private Thread pollingThread;
    
    // Anggap aja resto kita punya 12 meja
    private final int TOTAL_MEJA = 12;

    public ReservationController(ReservationView view) {
        this.view = view;
        this.dao = new ReservationDAO();
        
        setupActions();
        renderInitialTables();
        startPolling();
    }
    
    private void setupActions() {
        // Logika pas nyimpen data booking
        view.getBtnSimpan().setOnAction(e -> {
            try {
                String nama = view.getTxtNama().getText();
                int noMeja = Integer.parseInt(view.getTxtMeja().getText());
                LocalDate tgl = view.getDpTanggal().getValue();
                String jam = view.getTxtJam().getText();
                
                if (nama.isEmpty() || tgl == null || jam.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Isi yang bener bro", "Semua kolom wajib diisi ya!");
                    return;
                }
                
                // Gabungin tanggal sama jam jadi satu kesatuan (LocalDateTime)
                LocalTime time = LocalTime.parse(jam, DateTimeFormatter.ofPattern("HH:mm"));
                LocalDateTime datetime = LocalDateTime.of(tgl, time);
                
                Reservation r = new Reservation(0, nama, noMeja, datetime, "AKTIF");
                if (dao.addReservation(r)) {
                    showAlert(Alert.AlertType.INFORMATION, "Mantap", "Meja berhasil di-booking!");
                    // Bersihin form
                    view.getTxtNama().clear();
                    view.getTxtMeja().clear();
                    view.getDpTanggal().setValue(null);
                    view.getTxtJam().clear();
                    
                    // Gak usah manual manggil update visual, biar si background thread aja yang kerja 
                    // narik data terbaru dalam waktu kurang dari 5 detik.
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.WARNING, "Cek lagi", "Nomor meja harus angka dong.");
            } catch (DateTimeParseException ex) {
                showAlert(Alert.AlertType.WARNING, "Format Jam Salah", "Jam harus pake format HH:mm, misal 14:30.");
            }
        });
    }

    // Nggambar kotak-kotak meja di awal sebelum ada data aktual
    private void renderInitialTables() {
        view.getTableGrid().getChildren().clear();
        int col = 0, row = 0;
        
        for (int i = 1; i <= TOTAL_MEJA; i++) {
            VBox box = createTableBox(i, "KOSONG", "");
            view.getTableGrid().add(box, col, row);
            
            col++;
            if (col == 4) { // 4 meja sebaris
                col = 0;
                row++;
            }
        }
    }
    
    // Helper buat bikin bentuk mejanya
    private VBox createTableBox(int nomor, String status, String namaPelanggan) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(100, 100);
        
        // Warna dinamis. Ijo kalau kosong, merah kalau dipesan.
        Color bgColor = status.equals("AKTIF") ? Color.web("#FFCDD2") : Color.web("#C8E6C9");
        box.setBackground(new Background(new BackgroundFill(bgColor, new CornerRadii(10), Insets.EMPTY)));
        
        Label lblNo = new Label("Meja " + nomor);
        lblNo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        Label lblStatus = new Label(status.equals("AKTIF") ? "Dipesan" : "Tersedia");
        Label lblNama = new Label(status.equals("AKTIF") ? namaPelanggan : "");
        lblNama.setFont(Font.font("Segoe UI", 10));
        
        box.getChildren().addAll(lblNo, lblStatus, lblNama);
        return box;
    }

    // Ini nih ilmu hitamnya: Multithreading! (Background Task)
    private void startPolling() {
        pollingThread = new Thread(() -> {
            while (isPolling) {
                try {
                    // Cek database ngambil semua bookingan yang statusnya AKTIF
                    List<Reservation> allRes = dao.getAllReservations();
                    
                    // Kita oper update UI-nya ke JavaFX UI Thread biar gak crash (NotOnFXThreadException)
                    Platform.runLater(() -> {
                        updateTableVisuals(allRes);
                    });
                    
                    // Istirahat 5 detik sebelum ngecek lagi
                    Thread.sleep(5000);
                    
                } catch (InterruptedException e) {
                    System.out.println("Polling thread dihentikan paksa.");
                    break;
                }
            }
        });
        
        pollingThread.setDaemon(true); // Biar otomatis mati kalau aplikasinya ditutup
        pollingThread.start();
    }
    
    // Ngerender ulang warna dan isi meja berdasarkan data dari database
    private void updateTableVisuals(List<Reservation> reservations) {
        view.getTableGrid().getChildren().clear();
        int col = 0, row = 0;
        
        for (int i = 1; i <= TOTAL_MEJA; i++) {
            String status = "KOSONG";
            String pelanggan = "";
            
            // Cek apakah meja ini ada yang pesen di hari ini
            for (Reservation r : reservations) {
                // Sederhananya, kita nganggep meja ditahan kalau ada reservasi AKTIF
                if (r.getNomorMeja() == i && "AKTIF".equals(r.getStatus())) {
                    status = "AKTIF";
                    pelanggan = r.getNamaPelanggan();
                    break;
                }
            }
            
            VBox box = createTableBox(i, status, pelanggan);
            view.getTableGrid().add(box, col, row);
            
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }
    
    // Kalau view-nya diganti, usahain thread ini dimatiin biar gak bocor memori.
    // (Penting buat lifecycle JavaFX).
    public void stopPolling() {
        isPolling = false;
        if (pollingThread != null) pollingThread.interrupt();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
