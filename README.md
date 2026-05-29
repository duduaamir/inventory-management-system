# Console Stock Portfolio Manager

A Java-based console application for managing a stock portfolio with real-time price updates from Yahoo Finance.

## Features

- **Real-time Market Data**: Fetches live stock prices from Yahoo Finance API.
- **Portfolio Management**: Buy and sell stocks, view holdings, and track performance.
- **User System**: Secure login and registration system.
- **Reporting**: Generate portfolio summaries, profit/loss reports, and transaction history.
- **Auto-Updates**: Market prices update automatically in the background every 30 seconds.

## Project Structure

- `models`: Core data entities (User, Stock, Holding, Transaction).
- `services`: Business logic (Trading, Market Updates, Reporting).
- `services.yahoo`: Integration with Yahoo Finance API.
- `tests`: Verification scripts and test classes.
- `utils`: Helper utilities (CSV handling, Input validation).
- `Main.java`: Application entry point and UI loop.

## How to Run

1.  **Compile the project**:
    ```bash
    javac -d bin models/*.java services/*.java services/yahoo/*.java utils/*.java Main.java
    ```

2.  **Run the application**:
    ```bash
    java -cp bin Main
    ```

## Usage

1.  **Register** a new account or **Login** with existing credentials.
2.  Use the **Main Menu** to navigate:
    - **View Portfolio**: See your current holdings and total value.
    - **Buy/Sell Stocks**: Execute trades.
    - **View Market Prices**: Check current live prices.
    - **Reports**: View detailed performance reports.

## Dependencies

- Standard Java libraries 

