package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Holding;
import models.Stock;
import models.Transaction;
import models.User;
import utils.CSVHandler;

/**
 * Centralized CSV-backed persistence helper used instead of DAOs.
 */
public final class DataStore {
    private static final String USERS_PATH = "users.csv";
    private static final String HOLDINGS_PATH = "holdings.csv";
    private static final String TRANSACTIONS_PATH = "transactions.csv";
    private static final String STOCKS_PATH = "stocks.csv";

    private DataStore() {}

    // region Users

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try {
            CSVHandler.ensureFile(USERS_PATH);
            for (String line : CSVHandler.readAll(USERS_PATH)) {
                User user = User.fromCSV(line);
                if (user != null) users.add(user);
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public static User findUserByUsername(String username) {
        for (User user : loadUsers()) {
            if (user.getUsername().equalsIgnoreCase(username)) return user;
        }
        return null;
    }

    public static void addUser(User user) {
        try {
            CSVHandler.appendLine(USERS_PATH, user.toCSV());
        } catch (IOException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    public static void updateUser(User user) {
        List<User> users = loadUsers();
        boolean updated = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                updated = true;
                break;
            }
        }
        if (!updated) users.add(user);
        saveUsers(users);
    }

    private static void saveUsers(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User user : users) lines.add(user.toCSV());
        try {
            CSVHandler.writeAll(USERS_PATH, lines);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public static String nextUserId() {
        int max = 0;
        for (User user : loadUsers()) {
            try {
                String digits = user.getUserId().replaceAll("[^0-9]", "");
                max = Math.max(max, Integer.parseInt(digits));
            } catch (NumberFormatException ignored) {}
        }
        return String.format("U%03d", max + 1);
    }

    // endregion

    // region Holdings

    private static Map<String, Map<String, Holding>> loadAllHoldings() {
        Map<String, Map<String, Holding>> all = new HashMap<>();
        try {
            CSVHandler.ensureFile(HOLDINGS_PATH);
            for (String line : CSVHandler.readAll(HOLDINGS_PATH)) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                String userId = parts[0];
                Holding holding = Holding.fromCSV(parts);
                if (holding == null) continue;
                all.computeIfAbsent(userId, k -> new HashMap<>()).put(holding.getSymbol(), holding);
            }
        } catch (IOException e) {
            System.err.println("Error loading holdings: " + e.getMessage());
        }
        return all;
    }

    private static void saveAllHoldings(Map<String, Map<String, Holding>> all) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Map<String, Holding>> entry : all.entrySet()) {
            String userId = entry.getKey();
            for (Holding holding : entry.getValue().values()) {
                lines.add(holding.toCSV(userId));
            }
        }
        try {
            CSVHandler.writeAll(HOLDINGS_PATH, lines);
        } catch (IOException e) {
            System.err.println("Error saving holdings: " + e.getMessage());
        }
    }

    public static Map<String, Holding> loadHoldingsForUser(String userId) {
        Map<String, Map<String, Holding>> all = loadAllHoldings();
        Map<String, Holding> holdings = all.getOrDefault(userId, Collections.emptyMap());
        return new HashMap<>(holdings);
    }

    public static void persistHoldings(String userId, Map<String, Holding> holdings) {
        Map<String, Map<String, Holding>> all = loadAllHoldings();
        all.put(userId, new HashMap<>(holdings));
        saveAllHoldings(all);
    }

    // endregion

    // region Transactions

    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            CSVHandler.ensureFile(TRANSACTIONS_PATH);
            for (String line : CSVHandler.readAll(TRANSACTIONS_PATH)) {
                Transaction transaction = Transaction.fromCSV(line);
                if (transaction != null) transactions.add(transaction);
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
        transactions.sort(Comparator.comparingLong(Transaction::getTimestamp).reversed());
        return transactions;
    }

    public static void appendTransaction(Transaction transaction) {
        try {
            CSVHandler.appendLine(TRANSACTIONS_PATH, transaction.toCSV());
        } catch (IOException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
        }
    }

    public static String nextTransactionId() {
        int max = 0;
        for (Transaction t : loadTransactions()) {
            try {
                String digits = t.getTransactionId().replaceAll("[^0-9]", "");
                max = Math.max(max, Integer.parseInt(digits));
            } catch (NumberFormatException ignored) {}
        }
        return String.format("T%04d", max + 1);
    }

    // endregion

    // region Stocks

    public static List<Stock> loadStocks() {
        List<Stock> stocks = new ArrayList<>();
        try {
            CSVHandler.ensureFile(STOCKS_PATH);
            for (String line : CSVHandler.readAll(STOCKS_PATH)) {
                Stock stock = Stock.fromCSV(line);
                if (stock != null) stocks.add(stock);
            }
        } catch (IOException e) {
            System.err.println("Error loading stocks: " + e.getMessage());
        }
        return stocks;
    }

    public static void saveStocks(List<Stock> stocks) {
        List<String> lines = new ArrayList<>();
        for (Stock stock : stocks) {
            lines.add(stock.toCSV());
        }
        try {
            CSVHandler.writeAll(STOCKS_PATH, lines);
        } catch (IOException e) {
            System.err.println("Error saving stocks: " + e.getMessage());
        }
    }

    // endregion
}

