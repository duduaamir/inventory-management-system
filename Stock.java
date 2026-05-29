package models;

import java.time.Instant;

public class Stock {
    private String symbol;
    private String name;
    private double currentPrice;
    private long lastUpdated;

    public Stock(String symbol, String name, double currentPrice) {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.lastUpdated = Instant.now().toEpochMilli();
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double p) { this.currentPrice = p; this.lastUpdated = Instant.now().toEpochMilli(); }
    public long getLastUpdated() { return lastUpdated; }

    public String toCSV() {
        return String.join(",", symbol, name, String.valueOf(currentPrice), String.valueOf(lastUpdated));
    }

    public static Stock fromCSV(String line) {
        String[] p = line.split(",");
        if (p.length < 4) return null;
        try {
            Stock s = new Stock(p[0], p[1], Double.parseDouble(p[2]));
            s.lastUpdated = Long.parseLong(p[3]);
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return symbol + " - " + name + " : $" + String.format("%.2f", currentPrice);
    }
}
