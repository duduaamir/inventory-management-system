package services;

import models.Stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton class managing the list of available stocks and their current
 * prices.
 * Handles loading stocks from persistence and updating prices.
 */
public class StockMarket {
    private static StockMarket instance;
    private List<Stock> stocks = new ArrayList<>();

    private StockMarket() {
        loadOrInit();
    }

    public static synchronized StockMarket getInstance() {
        if (instance == null)
            instance = new StockMarket();
        return instance;
    }

    private void loadOrInit() {
        List<Stock> loaded = DataStore.loadStocks();
        if (loaded == null || loaded.isEmpty()) {
            // initialize default 15 stocks
            stocks.add(new Stock("AAPL", "Apple Inc.", 150));
            stocks.add(new Stock("GOOGL", "Alphabet Inc.", 2800));
            stocks.add(new Stock("MSFT", "Microsoft Corp.", 300));
            stocks.add(new Stock("AMZN", "Amazon.com Inc.", 3200));
            stocks.add(new Stock("TSLA", "Tesla Inc.", 800));
            stocks.add(new Stock("META", "Meta Platforms Inc.", 320));
            stocks.add(new Stock("NVDA", "NVIDIA Corp.", 450));
            stocks.add(new Stock("JPM", "JPMorgan Chase & Co.", 145));
            stocks.add(new Stock("V", "Visa Inc.", 220));
            stocks.add(new Stock("WMT", "Walmart Inc.", 155));
            stocks.add(new Stock("DIS", "Disney", 95));
            stocks.add(new Stock("NFLX", "Netflix", 380));
            stocks.add(new Stock("INTC", "Intel Corp.", 45));
            stocks.add(new Stock("AMD", "AMD", 110));
            stocks.add(new Stock("BA", "Boeing Co.", 210));
            DataStore.saveStocks(stocks);
        } else {
            stocks = new ArrayList<>(loaded);
        }
    }

    public synchronized Stock getStock(String symbol) {
        for (Stock s : stocks)
            if (s.getSymbol().equalsIgnoreCase(symbol))
                return s;
        return null;
    }

    public synchronized List<Stock> getAllStocks() {
        return Collections.unmodifiableList(stocks);
    }

    public synchronized boolean stockExists(String symbol) {
        return getStock(symbol) != null;
    }

    public synchronized void updateStockPrice(String symbol, double newPrice) {
        Stock s = getStock(symbol);
        if (s != null) {
            s.setCurrentPrice(newPrice);
            DataStore.saveStocks(stocks);
        }
    }

    public synchronized void persist() {
        DataStore.saveStocks(stocks);
    }
}
