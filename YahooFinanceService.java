package services.yahoo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YahooFinanceService {

    public double getPrice(String symbol) {
        try {
            // Using the chart endpoint as it seems more reliable/accessible for this
            // purpose
            URL url = new URL("https://query1.finance.yahoo.com/v8/finance/chart/" + symbol + "?interval=1d&range=1d");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                return parsePriceFromJSON(content.toString());
            } else {
                System.err.println("Failed to fetch price for " + symbol + ". Response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error fetching price for " + symbol + ": " + e.getMessage());
        }
        return -1.0; // Return -1 to indicate failure
    }

    private double parsePriceFromJSON(String json) {
        // Simple string parsing to avoid external dependencies
        // Looking for "regularMarketPrice":123.45
        String searchKey = "\"regularMarketPrice\":";
        int index = json.indexOf(searchKey);
        if (index != -1) {
            int start = index + searchKey.length();
            int end = json.indexOf(",", start);
            if (end == -1)
                end = json.indexOf("}", start); // In case it's the last field

            if (end != -1) {
                String priceStr = json.substring(start, end);
                try {
                    return Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing price: " + priceStr);
                }
            }
        }
        return -1.0;
    }
}
