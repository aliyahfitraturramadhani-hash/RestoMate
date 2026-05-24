package com.restomate.controllers;

import com.restomate.Main;
import com.restomate.dao.UserDAO;
import com.restomate.models.User;
import com.restomate.views.DashboardView;
import com.restomate.views.LoginView;
import javafx.scene.control.Label;

public class LoginController {
    
    // Bawa referensi view-nya dan inisialisasi DAO buat ngobrol sama database
    private LoginView view;
    private UserDAO userDAO;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDAO();
    }

    // Fungsi ini dipanggil pas tombol login diklik di view
    public void handleLogin(String username, String password, Label errorLabel) {
        // Cek dlu kosong apa nggak, jangan biarin user males ngetik
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            errorLabel.setText("Username sama password gak boleh kosong ya!");
            errorLabel.setVisible(true);
            return;
        }

        // Langsung cek ke database, cocok nggak nih?
        User user = userDAO.login(username, password);
        
        if (user != null) {
            // Asik, login berhasil! Sembunyiin errornya terus langsung lompat ke Dashboard
            errorLabel.setVisible(false);
            System.out.println("Login sukses cuy! Selamat datang, " + user.getUsername());
            
            // Bikin view dashboard baru trus setel jadi root utama
            DashboardView dashboard = new DashboardView(user);
            Main.setRoot(dashboard.getView());
        } else {
            // Yah, gagal. Kasih tau usernya pelan-pelan.
            errorLabel.setText("Hmm, username atau passwordnya salah tuh. Coba lagi deh.");
            errorLabel.setVisible(true);
        }
    }
}
