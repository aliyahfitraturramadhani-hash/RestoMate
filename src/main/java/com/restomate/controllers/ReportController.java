package com.restomate.controllers;

import com.restomate.dao.TransactionDAO;
import com.restomate.views.ReportView;
import javafx.scene.control.Alert;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportController {
    private ReportView view;
    private TransactionDAO transactionDAO;
    
    // Variabel buat nyimpen angka di layar (biar bisa di-reset manual pas Tutup Kasir tanpa ngehapus DB)
    private double currentDisplayIncome = 0;
    private boolean isShiftClosed = false;

    public ReportController(ReportView view) {
        this.view = view;
        this.transactionDAO = new TransactionDAO();
        
        loadData();
        setupActions();
    }

    public void refresh() {
        loadData();
    }

    // Fungsi buat narik duit hasil kerja keras hari ini dari database
    private void loadData() {
        if (!isShiftClosed) {
            currentDisplayIncome = transactionDAO.getTodayTotalIncome();
        }
        updateUI();
    }

    private void updateUI() {
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
        view.getLblIncome().setText("Rp " + formatter.format(currentDisplayIncome));
    }

    private void setupActions() {
        
        // Logika cetak struk laporan ke bentuk file teks .txt
        view.getBtnCetak().setOnAction(e -> {
            if (isShiftClosed && currentDisplayIncome == 0) {
                showAlert(Alert.AlertType.WARNING, "Kasir Udah Ditutup", "Udah gak ada yang bisa dicetak bro, shift ini udah ditutup.");
                return;
            }
            
            try {
                String fileName = "Laporan_RestoMate_" + System.currentTimeMillis() + ".txt";
                FileWriter writer = new FileWriter(fileName);
                writer.write("=========================================\n");
                writer.write("        LAPORAN PENDAPATAN HARIAN        \n");
                writer.write("               RESTOMATE                 \n");
                writer.write("=========================================\n");
                writer.write("Tanggal Cetak  : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
                writer.write("Total Omset    : Rp " + currentDisplayIncome + "\n");
                writer.write("Status Shift   : " + (isShiftClosed ? "DITUTUP" : "AKTIF") + "\n");
                writer.write("=========================================\n");
                writer.write("Terima kasih atas kerja keras hari ini!\n");
                writer.close();
                
                showAlert(Alert.AlertType.INFORMATION, "Sukses Cetak", "File laporan berhasil dibikin: " + fileName);
                System.out.println("Laporan dicetak ke file: " + fileName);
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Gagal Cetak", "Waduh, ada error pas nulis file teks: " + ex.getMessage());
            }
        });

        // Ingat pesannya! Tombol ini DILARANG menghapus database (DELETE).
        // Cuma buat menandai pergantian shift, alias nge-reset angka tampilan di layar ini aja.
        view.getBtnReset().setOnAction(e -> {
            if (isShiftClosed) {
                showAlert(Alert.AlertType.INFORMATION, "Aman Bro", "Kasir udah ditutup kok sebelumnya.");
                return;
            }
            
            // Kita reset state memori/tampilannya aja. Data DB AMAN.
            currentDisplayIncome = 0;
            isShiftClosed = true;
            updateUI();
            
            System.out.println("Pukul " + LocalDateTime.now() + " Kasir ditutup. Layar direset.");
            
            showAlert(Alert.AlertType.INFORMATION, "Shift Berakhir", "Kasir berhasil ditutup! Angka pendapatan direset ke nol untuk shift selanjutnya. Data histori transaksi di database tetap AMAN terkendali.");
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
