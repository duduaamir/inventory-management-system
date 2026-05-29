package services;

import models.Holding;
import models.Transaction;
import models.User;

import java.util.Map;

/**
 * Service handling stock trading operations (Buy/Sell).
 * Validates transactions, updates user balances, and records transaction
 * history.
 */
public class TradingService {
    private StockMarket market = StockMarket.getInstance();

    public boolean executeBuy(User user, String symbol, int qty) {
        if (qty <= 0)
            return false;
        if (!market.stockExists(symbol))
            return false;
        synchronized (user) {
            double price = market.getStock(symbol).getCurrentPrice();
            double total = price * qty;
            if (user.getCashBalance() < total)
                return false;
            user.setCashBalance(user.getCashBalance() - total);
            Map<String, Holding> holdings = DataStore.loadHoldingsForUser(user.getUserId());
            holdings.putIfAbsent(symbol, new Holding(symbol, 0, 0.0));
            holdings.get(symbol).addQuantity(qty, price);
            DataStore.persistHoldings(user.getUserId(), holdings);
            String txId = DataStore.nextTransactionId();
            Transaction t = new Transaction(txId, user.getUserId(), "BUY", symbol, qty, price);
            DataStore.appendTransaction(t);
            DataStore.updateUser(user);
            return true;
        }
    }

    public boolean executeSell(User user, String symbol, int qty) {
        if (qty <= 0)
            return false;
        if (!market.stockExists(symbol))
            return false;
        synchronized (user) {
            Map<String, Holding> holdings = DataStore.loadHoldingsForUser(user.getUserId());
            Holding h = holdings.get(symbol);
            if (h == null || h.getQuantity() < qty)
                return false;
            double price = market.getStock(symbol).getCurrentPrice();
            double totalProceeds = price * qty;
            h.reduceQuantity(qty);
            if (h.getQuantity() <= 0)
                holdings.remove(symbol);
            DataStore.persistHoldings(user.getUserId(), holdings);
            user.setCashBalance(user.getCashBalance() + totalProceeds);
            String txId = DataStore.nextTransactionId();
            Transaction t = new Transaction(txId, user.getUserId(), "SELL", symbol, qty, price);
            DataStore.appendTransaction(t);
            DataStore.updateUser(user);
            return true;
        }
    }
}
