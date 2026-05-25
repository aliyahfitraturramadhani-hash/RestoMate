package com.restomate.models;

public abstract class MenuRestoran {
    private int id;
    private String nama;
    private double harga;
    private int stok;
    private String kategori;

    public MenuRestoran(int id, String nama, double harga, int stok, String kategori) {
        this.id = id;
        this.nama = nama;
        setHarga(harga);
        setStok(stok);
        this.kategori = kategori;
    }
    
    public MenuRestoran() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) {
        if (harga < 0) throw new IllegalArgumentException("Harga tidak boleh negatif");
        this.harga = harga;
    }

    public int getStok() { return stok; }
    public void setStok(int stok) {
        if (stok < 0) throw new IllegalArgumentException("Stok tidak boleh negatif");
        this.stok = stok;
    }
    
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public abstract void tampilkanDetail();
}
