package models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Portfolio {
    private String userId;
    private Map<String, Holding> holdings = new HashMap<>();

    public Portfolio(String userId) { this.userId = userId; }

    public Map<String, Holding> getHoldings() { return Collections.unmodifiableMap(holdings); }

    public void setHoldings(Map<String, Holding> holdings) { this.holdings = holdings; }

    public void addHolding(String symbol, int qty, double price) {
        Holding h = holdings.get(symbol);
        if (h == null) {
            h = new Holding(symbol, qty, price);
            holdings.put(symbol, h);
        } else {
            h.addQuantity(qty, price);
        }
    }

    public void reduceHolding(String symbol, int qty) {
        Holding h = holdings.get(symbol);
        if (h == null) return;
        h.reduceQuantity(qty);
        if (h.getQuantity() <= 0) holdings.remove(symbol);
    }

    public double totalValue(StockMarketProxy priceProvider) {
        double total = 0.0;
        for (Holding h : holdings.values()) {
            Stock s = priceProvider.getStock(h.getSymbol());
            if (s != null) total += h.getQuantity() * s.getCurrentPrice();
        }
        return total;
    }

    // A tiny interface so Portfolio can ask for prices without importing services package in models
    public interface StockMarketProxy {
        Stock getStock(String symbol);
    }
}
