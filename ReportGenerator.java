package services;

import models.Holding;
import models.Stock;
import models.Transaction;
import models.User;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ReportGenerator {
    private final StockMarket market = StockMarket.getInstance();

    public void generatePortfolioSummary(User user) {
        Map<String, Holding> holdings = DataStore.loadHoldingsForUser(user.getUserId());
        System.out.println("Portfolio for " + user.getUsername());
        double totalValue = 0.0;
        System.out.println("Symbol | Qty | Avg Price | Current Price | Value | P/L");
        for (Holding h : holdings.values()) {
            Stock s = market.getStock(h.getSymbol());
            double current = s != null ? s.getCurrentPrice() : 0.0;
            double value = current * h.getQuantity();
            double cost = h.getAveragePurchasePrice() * h.getQuantity();
            double pl = value - cost;
            totalValue += value;
            System.out.printf("%s | %d | $%.2f | $%.2f | $%.2f | $%.2f\n", h.getSymbol(), h.getQuantity(), h.getAveragePurchasePrice(), current, value, pl);
        }
        double cash = user.getCashBalance();
        System.out.printf("Cash: $%.2f\nTotal Portfolio Value: $%.2f\nOverall (cash+holdings): $%.2f\n", cash, totalValue, cash + totalValue);
    }

    public void generateTransactionHistory(User user) {
        List<Transaction> all = DataStore.loadTransactions();
        System.out.println("Transactions for " + user.getUsername());
        for (Transaction t : all) {
            if (t.getUserId().equals(user.getUserId())) {
                System.out.printf("%s | %s | %s | %d | $%.2f | $%.2f | %s\n", t.getTransactionId(), t.getType(), t.getSymbol(), t.getQuantity(), t.getPricePerShare(), t.getTotalAmount(), Instant.ofEpochMilli(t.getTimestamp()).toString());
            }
        }
    }

    public void generateProfitLossReport(User user) {
        Map<String, Holding> holdings = DataStore.loadHoldingsForUser(user.getUserId());
        double unrealized = 0.0;
        double costBasis = 0.0;
        for (Holding h : holdings.values()) {
            Stock s = market.getStock(h.getSymbol());
            double current = s != null ? s.getCurrentPrice() : 0.0;
            unrealized += (current - h.getAveragePurchasePrice()) * h.getQuantity();
            costBasis += h.getAveragePurchasePrice() * h.getQuantity();
        }
        double realized = 0.0; // For simplicity not tracked separately; would require scanning transactions
        double accountTotal = user.getCashBalance() + costBasis + unrealized;
        double roi = (accountTotal - 100000.0) / 100000.0 * 100.0;
        System.out.printf("Unrealized P/L: $%.2f\nRealized P/L: $%.2f\nAccount Total: $%.2f\nROI since initial: %.2f%%\n", unrealized, realized, accountTotal, roi);
    }
}
