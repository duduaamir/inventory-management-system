package services;

import java.util.List;
import services.yahoo.YahooFinanceService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Service responsible for fetching real-time stock prices and updating the
 * market.
 * Runs a background timer to periodically fetch data from external APIs.
 */
public class MarketUpdateService {
    private final StockMarket market = StockMarket.getInstance();
    private final YahooFinanceService yahooService = new YahooFinanceService();
    private Timer timer;

    public void simulateMarketUpdate() {
        synchronized (market) {
            List<models.Stock> stocks = market.getAllStocks();
            // System.out.println("Updating prices from Yahoo Finance...");
            for (models.Stock s : stocks) {
                double newPrice = yahooService.getPrice(s.getSymbol());
                if (newPrice > 0) {
                    s.setCurrentPrice(newPrice);
                    // System.out.println("Updated " + s.getSymbol() + ": $" + newPrice);
                } else {
                    // System.out.println("Failed to update " + s.getSymbol());
                }
                // Add a small delay to be nice to the API
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }
            market.persist();
        }
    }

    public void startAutoUpdate(long intervalMillis) {
        if (timer != null)
            return;
        // Enforce minimum 30s interval for auto updates to avoid rate limiting
        if (intervalMillis < 30000)
            intervalMillis = 30000;

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                simulateMarketUpdate();
            }
        }, intervalMillis, intervalMillis);
    }

    public void stopAutoUpdate() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
