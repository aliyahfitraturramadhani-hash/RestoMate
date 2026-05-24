package com.restomate.views;

import com.restomate.controllers.ReportController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;

public class ReportView {
    private BorderPane root;
    private ReportController controller;
    
    private Label lblIncome;
    private Button btnCetak, btnReset;

    public ReportView() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        
        VBox centerBox = buildCenterBox();
        root.setCenter(centerBox);
        
        controller = new ReportController(this);
    }

    private VBox buildCenterBox() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.setMaxWidth(500);
        box.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(15), Insets.EMPTY)));
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.1));
        shadow.setRadius(20);
        box.setEffect(shadow);
        
        Label lblTitle = new Label("Ringkasan Hari Ini");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTitle.setTextFill(Color.GRAY);
        
        // Angka omset gede-gede biar seneng liatnya
        lblIncome = new Label("Rp 0");
        lblIncome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 48));
        lblIncome.setTextFill(Color.web("#4CAF50")); // Hijau lambang cuan
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        btnCetak = new Button("🖨️ Cetak Laporan");
        btnCetak.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 40px; -fx-pref-width: 150px;");
        
        btnReset = new Button("🔒 Tutup Kasir");
        btnReset.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 40px; -fx-pref-width: 150px;");
        
        buttonBox.getChildren().addAll(btnCetak, btnReset);
        
        box.getChildren().addAll(lblTitle, lblIncome, buttonBox);
        return box;
    }

    public BorderPane getView() { return root; }
    public Label getLblIncome() { return lblIncome; }
    public Button getBtnCetak() { return btnCetak; }
    public Button getBtnReset() { return btnReset; }
    public ReportController getController() { return controller; }
}
