package com.restomate.models;

public class Makanan extends MenuRestoran {
    private String tingkatPedas;

    public Makanan(int id, String nama, double harga, int stok, String tingkatPedas) {
        super(id, nama, harga, stok, "MAKANAN");
        this.tingkatPedas = tingkatPedas;
    }
    
    public Makanan() {
        super();
        setKategori("MAKANAN");
    }

    public String getTingkatPedas() { return tingkatPedas; }
    public void setTingkatPedas(String tingkatPedas) { this.tingkatPedas = tingkatPedas; }

    @Override
    public void tampilkanDetail() {
        System.out.println("Makanan: " + getNama() + " | Harga: " + getHarga() + " | Stok: " + getStok() + " | Level Pedas: " + tingkatPedas);
    }
}
