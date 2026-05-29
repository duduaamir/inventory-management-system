package tests.yahoo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TestYahoo {
    public static void main(String[] args) {
        try {
            URL url = new URL("https://query1.finance.yahoo.com/v8/finance/chart/AAPL?interval=1d&range=1d");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == 200) {
                Scanner scanner = new Scanner(conn.getInputStream());
                while (scanner.hasNextLine()) {
                    System.out.println(scanner.nextLine());
                }
                scanner.close();
            } else {
                System.out.println("Failed to fetch data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
