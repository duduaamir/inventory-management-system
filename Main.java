import java.util.List;
import java.util.Map;
import java.util.Scanner;
import models.Holding;
import models.User;
import services.DataStore;
import services.MarketUpdateService;
import services.ReportGenerator;
import services.StockMarket;
import services.TradingService;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StockMarket market = StockMarket.getInstance();
    private static final TradingService tradingService = new TradingService();
    private static final MarketUpdateService simulator = new MarketUpdateService();
    private static final ReportGenerator reports = new ReportGenerator();

    public static void main(String[] args) {
        ensureDefaults();
        System.out.println("Welcome to the Console Stock Portfolio Manager");
        User user = null;
        while (user == null) {
            System.out.println("[1] Login  [2] Register  [0] Exit");
            String c = scanner.nextLine().trim();
            if (c.equals("1"))
                user = login();
            else if (c.equals("2"))
                user = register();
            else if (c.equals("0")) {
                System.out.println("Goodbye");
                System.exit(0);
            }
        }

        boolean running = true;
        while (running) {
            System.out.println(
                    "\nMain Menu:\n[1] View Portfolio\n[2] Buy Stocks\n[3] Sell Stocks\n[4] View Market Prices\n[5] Transaction History\n[6] Generate Reports\n[9] Logout\n[0] Exit");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewPortfolio(user);
                    break;
                case "2":
                    buyFlow(user);
                    break;
                case "3":
                    sellFlow(user);
                    break;
                case "4":
                    viewMarket();
                    break;
                case "5":
                    reports.generateTransactionHistory(user);
                    break;
                case "6":
                    runReports(user);
                    break;
                case "9":
                    user = null;
                    user = login();
                    break;
                case "0":
                    running = false;
                    simulator.stopAutoUpdate();
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
        System.out.println("Exiting. Bye.");
    }

    private static void ensureDefaults() {
        // Ensure stock market initialized
        market.getAllStocks();
        // Start auto-updates every 30 seconds
        simulator.startAutoUpdate(30000);
        System.out.println("Market price updates started (every 30s).");
    }

    private static User login() {
        System.out.print("Username: ");
        String u = scanner.nextLine().trim();
        User user = DataStore.findUserByUsername(u);
        if (user == null) {
            System.out.println("User not found.");
            return null;
        }
        System.out.print("Password: ");
        String p = scanner.nextLine().trim();
        if (!user.getPassword().equals(p)) {
            System.out.println("Invalid password.");
            return null;
        }
        System.out.println("Login successful. Welcome back, " + user.getUsername());
        return user;
    }

    private static User register() {
        System.out.print("Choose username: ");
        String u = scanner.nextLine().trim();
        if (DataStore.findUserByUsername(u) != null) {
            System.out.println("Username already exists");
            return null;
        }
        System.out.print("Choose password: ");
        String p = scanner.nextLine().trim();
        String id = DataStore.nextUserId();
        User user = new User(id, u, p, 100000.0);
        DataStore.addUser(user);
        System.out.println("Registered. Your user id: " + id);
        return user;
    }

    private static void viewPortfolio(User user) {
        reports.generatePortfolioSummary(user);
    }

    private static void viewMarket() {
        List<models.Stock> stocks = market.getAllStocks();
        System.out.println("Symbol | Name | Price");
        for (models.Stock s : stocks)
            System.out.printf("%s | %s | $%.2f\n", s.getSymbol(), s.getName(), s.getCurrentPrice());
    }

    private static void buyFlow(User user) {
        System.out.printf("Available cash: $%.2f\n", user.getCashBalance());
        System.out.print("Enter symbol or 'list': ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        if (symbol.equalsIgnoreCase("list")) {
            viewMarket();
            return;
        }
        if (!market.stockExists(symbol)) {
            System.out.println("Invalid symbol");
            return;
        }
        System.out.print("Enter quantity: ");
        String q = scanner.nextLine().trim();
        int qty;
        try {
            qty = Integer.parseInt(q);
            if (qty <= 0) {
                System.out.println("Quantity must be positive");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid quantity");
            return;
        }
        double price = market.getStock(symbol).getCurrentPrice();
        double total = price * qty;
        System.out.printf("Total cost: $%.2f - Confirm buy? (y/n): ", total);
        String c = scanner.nextLine().trim();
        if (c.equalsIgnoreCase("y")) {
            boolean ok = tradingService.executeBuy(user, symbol, qty);
            if (ok)
                System.out.println("Buy executed.");
            else
                System.out.println("Buy failed (insufficient funds or error)");
        } else
            System.out.println("Buy cancelled");
    }

    private static void sellFlow(User user) {
        Map<String, Holding> holdings = DataStore.loadHoldingsForUser(user.getUserId());
        if (holdings.isEmpty()) {
            System.out.println("You have no holdings.");
            return;
        }
        System.out.println("Your holdings:");
        for (Holding h : holdings.values())
            System.out.printf("%s : %d @ $%.2f\n", h.getSymbol(), h.getQuantity(), h.getAveragePurchasePrice());
        System.out.print("Enter symbol to sell: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        Holding h = holdings.get(symbol);
        if (h == null) {
            System.out.println("You don't own that symbol");
            return;
        }
        System.out.print("Enter quantity to sell: ");
        String q = scanner.nextLine().trim();
        int qty;
        try {
            qty = Integer.parseInt(q);
            if (qty <= 0 || qty > h.getQuantity()) {
                System.out.println("Invalid quantity");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid quantity");
            return;
        }
        double price = market.getStock(symbol).getCurrentPrice();
        double proceeds = price * qty;
        System.out.printf("Proceeds: $%.2f - Confirm sell? (y/n): ", proceeds);
        String c = scanner.nextLine().trim();
        if (c.equalsIgnoreCase("y")) {
            boolean ok = tradingService.executeSell(user, symbol, qty);
            if (ok)
                System.out.println("Sell executed.");
            else
                System.out.println("Sell failed (insufficient shares or error)");
        } else
            System.out.println("Sell cancelled");
    }

    private static void runReports(User user) {
        System.out.println("[1] Portfolio Summary  [2] P&L Report  [3] Transaction History");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1":
                reports.generatePortfolioSummary(user);
                break;
            case "2":
                reports.generateProfitLossReport(user);
                break;
            case "3":
                reports.generateTransactionHistory(user);
                break;
            default:
                System.out.println("Invalid");
        }
    }
}
