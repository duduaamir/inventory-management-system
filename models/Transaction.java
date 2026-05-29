package models;

import java.time.Instant;

public class Transaction {
    private String transactionId;
    private String userId;
    private String type; // BUY or SELL
    private String symbol;
    private int quantity;
    private double pricePerShare;
    private double totalAmount;
    private long timestamp;

    public Transaction(String transactionId, String userId, String type, String symbol, int quantity, double pricePerShare) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.totalAmount = quantity * pricePerShare;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public String getTransactionId() { return transactionId; }
    public String getUserId() { return userId; }
    public String getType() { return type; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPricePerShare() { return pricePerShare; }
    public double getTotalAmount() { return totalAmount; }
    public long getTimestamp() { return timestamp; }

    public String toCSV() {
        return String.join(",", transactionId, userId, type, symbol, String.valueOf(quantity), String.valueOf(pricePerShare), String.valueOf(totalAmount), String.valueOf(timestamp));
    }

    public static Transaction fromCSV(String line) {
        String[] p = line.split(",");
        if (p.length < 8) return null;
        try {
            Transaction t = new Transaction(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]), Double.parseDouble(p[5]));
            t.totalAmount = Double.parseDouble(p[6]);
            t.timestamp = Long.parseLong(p[7]);
            return t;
        } catch (Exception e) { return null; }
    }

    @Override
    public String toString() {
        return String.format("%s %s %d @ $%.2f = $%.2f on %d", type, symbol, quantity, pricePerShare, totalAmount, timestamp);
    }
}
