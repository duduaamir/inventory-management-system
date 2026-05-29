package tests;

import services.MarketUpdateService;
import services.StockMarket;
import models.Stock;
import java.util.List;

public class VerifyAutoUpdates {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Auto-Update Verification...");

        StockMarket market = StockMarket.getInstance();
        MarketUpdateService simulator = new MarketUpdateService();

        System.out.println("\n--- Initial Prices ---");
        printPrices(market);

        System.out.println("\nStarting auto-updates (30s interval)...");
        simulator.startAutoUpdate(30000);

        System.out.println("Waiting 40 seconds for an update to occur...");
        Thread.sleep(40000);

        System.out.println("\n--- Prices After Wait ---");
        printPrices(market);

        simulator.stopAutoUpdate();
        System.out.println("\nVerification Complete.");
        System.exit(0);
    }

    private static void printPrices(StockMarket market) {
        List<Stock> stocks = market.getAllStocks();
        // Print first 3 stocks to keep output clean
        for (int i = 0; i < Math.min(stocks.size(), 3); i++) {
            Stock s = stocks.get(i);
            System.out.printf("%s: $%.2f (Last Updated: %d)\n", s.getSymbol(), s.getCurrentPrice(), s.getLastUpdated());
        }
    }
}
