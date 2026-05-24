package com.restomate.models;

public class TransactionItem {
    private int id;
    private int transactionId;
    private int menuId;
    private int qty;
    private double subtotal;

    public TransactionItem(int id, int transactionId, int menuId, int qty, double subtotal) {
        this.id = id;
        this.transactionId = transactionId;
        this.menuId = menuId;
        this.qty = qty;
        this.subtotal = subtotal;
    }
    
    public TransactionItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
