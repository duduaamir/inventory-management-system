package models;

import java.util.Objects;

public class User {
    private String userId;
    private String username;
    private String password;
    private double cashBalance;

    public User(String userId, String username, String password, double cashBalance) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.cashBalance = cashBalance;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public double getCashBalance() { return cashBalance; }
    public void setCashBalance(double cashBalance) { this.cashBalance = cashBalance; }

    public String toCSV() {
        return String.join(",", userId, username, password, String.valueOf(cashBalance));
    }

    public static User fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) return null;
        try {
            return new User(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return username + " (" + userId + ") - $" + String.format("%.2f", cashBalance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() { return Objects.hash(userId); }
}
