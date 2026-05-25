package com.restomate.models;

public class Minuman extends MenuRestoran {
    private boolean isDingin;

    public Minuman(int id, String nama, double harga, int stok, boolean isDingin) {
        super(id, nama, harga, stok, "MINUMAN");
        this.isDingin = isDingin;
    }
    
    public Minuman() {
        super();
        setKategori("MINUMAN");
    }

    public boolean isDingin() { return isDingin; }
    public void setDingin(boolean dingin) { isDingin = dingin; }

    @Override
    public void tampilkanDetail() {
        String suhu = isDingin ? "Dingin/Es" : "Panas";
        System.out.println("Minuman: " + getNama() + " | Harga: " + getHarga() + " | Stok: " + getStok() + " | Suhu: " + suhu);
    }
}
