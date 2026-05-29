package models;

public class Holding {
    private String symbol;
    private int quantity;
    private double averagePurchasePrice;

    public Holding(String symbol, int quantity, double averagePurchasePrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averagePurchasePrice = averagePurchasePrice;
    }

    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getAveragePurchasePrice() { return averagePurchasePrice; }

    public void addQuantity(int qty, double pricePerShare) {
        if (qty <= 0) return;
        double totalCost = this.averagePurchasePrice * this.quantity + pricePerShare * qty;
        this.quantity += qty;
        this.averagePurchasePrice = totalCost / this.quantity;
    }

    public void reduceQuantity(int qty) {
        if (qty <= 0) return;
        this.quantity -= qty;
        if (this.quantity < 0) this.quantity = 0;
    }

    public String toCSV(String userId) {
        return String.join(",", userId, symbol, String.valueOf(quantity), String.valueOf(averagePurchasePrice));
    }

    public static Holding fromCSV(String[] parts) {
        if (parts.length < 4) return null;
        try {
            return new Holding(parts[1], Integer.parseInt(parts[2]), Double.parseDouble(parts[3]));
        } catch (Exception e) { return null; }
    }
}
