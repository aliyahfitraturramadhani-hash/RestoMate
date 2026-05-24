package com.restomate.models;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private String namaPelanggan;
    private int nomorMeja;
    private LocalDateTime waktuReservasi;
    private String status;

    public Reservation(int id, String namaPelanggan, int nomorMeja, LocalDateTime waktuReservasi, String status) {
        this.id = id;
        this.namaPelanggan = namaPelanggan;
        this.nomorMeja = nomorMeja;
        this.waktuReservasi = waktuReservasi;
        this.status = status;
    }
    
    public Reservation() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }

    public int getNomorMeja() { return nomorMeja; }
    public void setNomorMeja(int nomorMeja) { this.nomorMeja = nomorMeja; }

    public LocalDateTime getWaktuReservasi() { return waktuReservasi; }
    public void setWaktuReservasi(LocalDateTime waktuReservasi) { this.waktuReservasi = waktuReservasi; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
