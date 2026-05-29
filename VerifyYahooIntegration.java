package tests.yahoo;

import services.MarketUpdateService;
import services.StockMarket;
import models.Stock;
import java.util.List;

public class VerifyYahooIntegration {
    public static void main(String[] args) {
        System.out.println("Starting Verification...");

        StockMarket market = StockMarket.getInstance();
        MarketUpdateService simulator = new MarketUpdateService();

        System.out.println("\n--- Initial Prices ---");
        printPrices(market);

        System.out.println("\n--- Triggering Update from Yahoo Finance ---");
        simulator.simulateMarketUpdate();

        System.out.println("\n--- Updated Prices ---");
        printPrices(market);

        System.out.println("\nVerification Complete.");
    }

    private static void printPrices(StockMarket market) {
        List<Stock> stocks = market.getAllStocks();
        for (Stock s : stocks) {
            System.out.printf("%s: $%.2f\n", s.getSymbol(), s.getCurrentPrice());
        }
    }
}
