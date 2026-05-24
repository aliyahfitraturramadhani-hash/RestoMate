package com.restomate.dao;

import com.restomate.models.User;
import com.restomate.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    
    // Fungsi buat ngecek login pegawai. Kalau cocok dapet object User-nya, kalau nggak ya dapet null.
    public User login(String username, String password) {
        // Enkripsi dulu nih password yang diinput, biar cocok sama yang di DB
        String hashedPassword = Database.hashPassword(password);
        
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        // Biasakan pakai try-with-resources ya, biar koneksinya otomatis ketutup dan gak bocor!
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Yeay, ketemu! Bikin object User dari datanya.
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Waduh, gagal query login nih: " + e.getMessage());
        }
        
        // Yah, gagal login. Username atau password salah bro.
        return null;
    }
}
