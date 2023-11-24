import java.util.Scanner;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import javax.swing.SwingUtilities;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.Date;

public class TraderInterface {
    public static void main2(OracleConnection connection, int tax_id) {

        Scanner scanner = new Scanner(System.in);

        // check if account exists
        while (doesAccountExist(connection, tax_id) == false) {
            System.out.println(
                    "Account does not exist. Please create an account by typing Deposit and a balance greater than $1000");
            System.out.println("Ex: To Deposit $1000, type: Deposit 1000");

            String userInput = scanner.nextLine();

            // Split the user input into words
            String[] inputWords = userInput.split(" ");

            if (inputWords.length == 2 && inputWords[0].equalsIgnoreCase("Deposit")) {
                try {
                    double amount = Double.parseDouble(inputWords[1]);
                    if (amount >= 1000) {

                        // Deposit logic here
                        // create account
                        // create market account with amount

                        int acc_id = createAccount_Has(tax_id, connection);
                        createMarketAccount(amount, acc_id, tax_id, connection);

                        System.out.println("Successfully deposited $" + amount + ".");
                        // You can add code to perform the deposit operation
                        break; // Exit the loop after successful deposit
                    } else {
                        System.out.println("Deposit amount must be $1000 or more. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid numeric amount.");
                }
            } else {
                System.out.println("Invalid input format. Please enter a valid deposit command.");
            }
        }

        // get account id
        int accountId = getAccountId(tax_id, connection);

        while (true) {
            System.out.println("Trader Interface Options:");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Buy");
            System.out.println("4. Sell");
            System.out.println("5. Cancel");
            System.out.println("6. Show Balance");
            System.out.println("7. Show Transaction History");
            System.out.println("8. List Current Stock Price");
            System.out.println("9. Movie Information");
            System.out.println("10. List all Reviews for Movie");
            System.out.println("0. Exit");

            System.out.print("Enter your choice (0-10): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1:
                    deposit(scanner, connection, accountId, tax_id);
                    break;
                case 2:
                    withdraw(scanner, connection, accountId, tax_id);
                    break;
                case 3:
                    buy(connection, scanner, accountId, tax_id);
                    break;
                case 4:
                    sell(connection, scanner, accountId, tax_id);
                    break;
                case 5:
                    cancel();
                    break;
                case 6:
                    showBalance(connection, accountId);
                    break;
                case 7:
                    showTransactionHistory(connection, tax_id);
                    break;
                case 8:
                    listStockPrice(connection, scanner);
                    break;
                case 9:
                    movieInformation(connection, scanner);
                    break;
                case 10:
                    listAllReviews(connection, scanner);
                    break;
                case 0:
                    System.out.println("Exiting Trader Interface.");
                    //scanner.close();
                    StartupOptions.main2(connection);
                    //System.exit(0);
                default:
                    System.out.println("Invalid option. Please choose a valid option (0-10).");
            }
        }
    }

    public static int getAccountId(int taxId, Connection connection) {
        int accountId = -1;
        String selectQuery = "SELECT acc_id FROM Account_Has WHERE tax_id = " + Integer.toString(taxId);
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if (resultSet.next()) {
                accountId = resultSet.getInt("acc_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountId;
    }

    private static int createAccount_Has(int tax_id, OracleConnection connection) {
        int acc_id = 0;
        while(true){
            Random random = new Random();
            acc_id = random.nextInt(Integer.MAX_VALUE);
            String selectQuery = "SELECT * FROM Account_Has WHERE acc_id = " + Integer.toString(acc_id);
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(selectQuery);
                if (resultSet.next()) {
                    continue;
                }
                else{
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Generated Primary Key: " + acc_id);
        String insertQuery = "INSERT INTO Account_Has (acc_id, tax_id) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, acc_id);
            preparedStatement.setInt(2, tax_id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account created successfully!");
            } else {
                System.out.println("Account creation failed.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Account creation failed.");
            e.printStackTrace();
        }

        return (acc_id);
    }

    private static void createMarketAccount(double amount, int acc_id, int tax_id, OracleConnection connection) {
        // create account
        // Random random = new Random();
        // int acc_id = random.nextInt(Integer.MAX_VALUE);
        // System.out.println("Generated Primary Key: " + acc_id);
        String insertQuery = "INSERT INTO Market_Account (Balance, acc_id, tax_id) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, acc_id);
            preparedStatement.setInt(3, tax_id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Market Account created successfully!");
            } else {
                System.out.println("Market Account creation failed.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Market Account creation failed.");
            e.printStackTrace();
        }
    }

    private static void deposit(Scanner scanner, OracleConnection connection, int accountId, int tax_id) {
        System.out.println("Deposit option selected.");
        while (true) {
            System.out.println("Please enter the amount you would like to deposit as a decimal number: ");
            if (scanner.hasNextDouble()) {
                double amount = scanner.nextDouble();
                // Process the valid double input (amount) here
                System.out.println("You entered: " + amount);
                depositSQL(amount, connection, accountId);
                createDepositTransaction(connection, amount, tax_id);
                break; // Exit the loop since valid input was provided
            } else {
                System.out.println("Invalid input. Please enter a valid decimal number.");
                scanner.next(); // Consume the invalid input to avoid an infinite loop
            }
        }
        // Continue with your deposit logic here using the valid amount.
    }

    private static void depositSQL(double amount, OracleConnection connection, int accountId) {

        String insertQuery = "UPDATE Market_Account SET Balance = Balance + ? WHERE acc_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, accountId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Deposit successful!");
            } else {
                System.out.println("Deposit failed.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Deposit failed.");
            e.printStackTrace();
        }

    }

    private static void withdraw(Scanner scanner, OracleConnection connection, int accountId, int tax_id) {
        System.out.println("Withdraw option selected.");
        while (true) {
            System.out.println("Please enter the amount you would like to withdraw as a decimal number: ");
            if (scanner.hasNextDouble()) {
                double amount = scanner.nextDouble();
                // Process the valid double input (amount) here
                if (amount <= 0) {
                    System.out.println("Invalid input. Withdrawal amount must be greater than zero.");
                } else {
                    double currentBalance = getBalance(connection, accountId);
                    if (amount > currentBalance) {
                        System.out.println("Insufficient balance. Your current balance is: " + currentBalance);
                    } else {
                        withdrawSQL(amount, connection, accountId);
                        System.out.println("Withdrawal of $" + amount + " successful!");
                        createWithdrawTransaction(connection, amount, tax_id);
                        break; // Exit the loop after successful withdrawal
                    }
                }
            } else {
                System.out.println("Invalid input. Please enter a valid decimal number.");
                scanner.next(); // Consume the invalid input to avoid an infinite loop
            }
        }
        // Continue with your withdrawal logic here using the valid amount.
    }

    private static void withdrawSQL(double amount, OracleConnection connection, int accountId) {
        String updateQuery = "UPDATE Market_Account SET Balance = Balance - ? WHERE acc_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, accountId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Withdrawal successful!");
            } else {
                System.out.println("Withdrawal failed.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Withdrawal failed.");
            e.printStackTrace();
        }
    }

    private static void createWithdrawTransaction(OracleConnection connection, double amount, int tax_id){
        int transaction_id = createTransaction(connection, tax_id);
        String insertQuery = "INSERT INTO Withdraw (tid, amount) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, transaction_id);
            preparedStatement.setDouble(2, amount);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Withdraw Transaction created successfully!");
            } else {
                System.out.println("Withdraw Transaction creation failed.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Withdraw Transaction creation failed.");
            e.printStackTrace();
        }
    }

    public static double getStockPrice(OracleConnection connection, String symbol) {
        String selectQuery = "SELECT * FROM Stock_Actor WHERE symbol = " + "'" + symbol + "'";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if(resultSet.next()) {
                return resultSet.getDouble("current_price");
            }
        } catch (SQLException e) {
            return(-1.0);
        }
        return -1.0;
    }

    private static void buy(OracleConnection connection, Scanner scanner, int acc_id, int tax_id) {
        // Implement buy logic
        
        System.out.println("Buy option selected.");
        System.out.println("Type symbol of stock you would like to purchase.");
        String symbol = scanner.nextLine();
        double stockPrice = getStockPrice(connection, symbol);
        if(stockPrice != -1.0) {
            System.out.println("Stock exists.");
            System.out.println("Type amount of stock you would like to purchase.");
            int numShares = 0;
            while (true) {
                System.out.print("Type the amount of stock you wish to purchase: ");
                try {
                    numShares = Integer.parseInt(scanner.nextLine());
                    break;  // Exit the loop if the input is a valid integer
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer. Try again.");
                }
            }
            if(numShares <= 0) {
                System.out.println("Invalid input. Number of shares must be greater than zero.");
                return;
            }
            double totalCost = stockPrice * numShares;
            double balance = getBalance(connection, acc_id);
            if(totalCost >= balance) {
                System.out.println("Insufficient funds. Your current balance is: " + balance);
                return;
            }
            else {
                //withdraw the money 
                withdrawSQL(totalCost, connection, acc_id);
                String selectQuery = "SELECT balance_share FROM Stock_Account WHERE tax_id = " + Integer.toString(tax_id) + " AND symbol = " + "'" + symbol + "'";
                try (Statement statement = connection.createStatement()) {
                    ResultSet resultSet = statement.executeQuery(selectQuery);
                    if (!resultSet.next()) {
                        createStockAccount(connection, scanner, tax_id, acc_id, symbol);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //String updateQuery = "UPDATE Stock_Account SET num_share = num_share + ?, balance_share = balance_share + ? WHERE tax_id = ? AND symbol = ?";
                String updateQuery = "UPDATE Stock_Account SET num_share = num_share + " + Integer.toString(numShares) + ", balance_share = balance_share + " + Double.toString(totalCost) + " WHERE tax_id = " + Integer.toString(tax_id) + " AND symbol = " + "'" + symbol + "'";
                try (Statement statement = connection.createStatement()) {
                    //preparedStatement.setInt(1, numShares);
                    //preparedStatement.setDouble(2, totalCost);
                    //preparedStatement.setInt(3, tax_id);
                    //preparedStatement.setString(4, symbol);
        
                    int rowsAffected = statement.executeUpdate(updateQuery);
                    if (rowsAffected > 0) {
                        System.out.println("Added to Stock Account successful!");
                    } else {
                        System.out.println("Purchase failed.");
                    }
                } catch (SQLException e) {
                    System.out.println("ERROR: Purchase failed.");
                    e.printStackTrace();
                }
                createBuyTransaction(connection, numShares, symbol, stockPrice, tax_id);

            }
        }
        else{
            System.out.println("Stock does not exist.");
        }
}

    private static void createStockAccount(OracleConnection connection, Scanner scanner, int tax_id, int acc_id, String symbol){
        String insertQuery = "INSERT INTO Stock_Account (acc_id, symbol, num_share, balance_share, tax_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setDouble(1, acc_id);
            preparedStatement.setString(2, symbol);
            preparedStatement.setInt(3, 0);
            preparedStatement.setDouble(4, 0.0);
            preparedStatement.setInt(5, tax_id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Stock Account created successfully!");
            } else {
                System.out.println("Stock Account creation failed.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Stock Account creation failed.");
            e.printStackTrace();
        }

    }
        

    private static void sell(OracleConnection connection, Scanner scanner , int acc_id, int tax_id) {
        // Implement sell logic
        System.out.println("Sell option selected.");
        System.out.println("Enter the symbol of the stock you would like to sell: ");
        String symbol = scanner.nextLine();
        double stockPrice = getStockPrice(connection, symbol);
        if(stockPrice == -1.0) {
            System.out.println("Stock does not exist.");
            return;
        }
        System.out.println("Enter the number of shares you would like to sell: ");
        int numShares = 0;
        while (true) {
            System.out.print("Type the amount of stock you wish to sell: ");
            try {
                numShares = Integer.parseInt(scanner.nextLine());
                if(numShares <= 0) {
                    System.out.println("Invalid input. Number of shares must be greater than zero.");
                    return;
                }
                break;  // Exit the loop if the input is a valid integer
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer. Try again.");
            }
        }
        String selectQuery = "SELECT num_share FROM Stock_Account WHERE tax_id = " + Integer.toString(tax_id) + " AND symbol = " + "'" + symbol + "'";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if (!resultSet.next()) {
                System.out.println("You do not own any shares of this stock.");
                return;
            }
            else{
                int numSharesOwned = resultSet.getInt("num_share");
                if(numSharesOwned < numShares) {
                    System.out.println("You do not own enough shares of this stock.");
                    return;
                }
                else{
                    double totalCost = stockPrice * numShares;
                    //depositSQL(totalCost, connection, acc_id);
                    String updateQuery = "UPDATE Stock_Account SET num_share = num_share - " + Integer.toString(numShares) + ", balance_share = balance_share - " + Double.toString(totalCost) + " WHERE tax_id = " + Integer.toString(tax_id) + " AND symbol = " + "'" + symbol + "'";
                    try (Statement statement2 = connection.createStatement()) {
                        int rowsAffected = statement2.executeUpdate(updateQuery);
                        if (rowsAffected > 0) {
                            System.out.println("Sold Stock successful!");
                        } else {
                            System.out.println("Sale failed.");
                        }
                    } catch (SQLException e) {
                        System.out.println("ERROR: Sale failed.");
                        e.printStackTrace();
                    }
                    depositSQL(totalCost, connection, acc_id);
                    createSellTransaction(connection, numShares, symbol, stockPrice, tax_id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        

    }

    private static void createSellTransaction(OracleConnection connection, int numShares, String symbol, double price, int tax_id){
        String insertQuery = "INSERT INTO Sell (tid, shares, sell_price, symbol) VALUES (?, ?, ?, ?)";

        int transaction_id = createTransaction(connection, tax_id);

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setDouble(1, transaction_id);
            preparedStatement.setInt(2, numShares);
            preparedStatement.setDouble(3, price);
            preparedStatement.setString(4, symbol);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sell Transaction created successfully!");
            } else {
                System.out.println("Sell Transaction creation failed.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Sell Transaction creation failed.");
            e.printStackTrace();
        }
    }

    private static void cancel() {
        // Implement cancel logic
        System.out.println("Cancel option selected.");
    }

    private static void showBalance(OracleConnection connection, int acc_id) {
        // Implement show balance logic
        System.out.println("Show Balance option selected.");
        System.out.println("Your current balance is: " + getBalance(connection, acc_id));
    }

    private static double getBalance(OracleConnection connection, int acc_id) {
        double balance = 0.0;
        String selectQuery = "SELECT balance FROM Market_Account WHERE acc_id = " + Integer.toString(acc_id);
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if (resultSet.next()) {
                balance = resultSet.getInt("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    private static void createBuyTransaction(OracleConnection connection, int numShares, String symbol, double price, int tax_id){
        String insertQuery = "INSERT INTO Buy (tid, shares, buy_price, symbol) VALUES (?, ?, ?, ?)";

        int transaction_id = createTransaction(connection, tax_id);

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setDouble(1, transaction_id);
            preparedStatement.setInt(2, numShares);
            preparedStatement.setDouble(3, price);
            preparedStatement.setString(4, symbol);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Buy Transaction created successfully!");
            } else {
                System.out.println("Buy Transaction creation failed.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Buy Transaction creation failed.");
            e.printStackTrace();
        }
    }

    private static void createDepositTransaction(OracleConnection connection, double amount, int tax_id){
        int transaction_id = createTransaction(connection, tax_id);
        String insertQuery = "INSERT INTO Deposit (tid, amount) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, transaction_id);
            preparedStatement.setDouble(2, amount);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Deposit Transaction created successfully!");
            } else {
                System.out.println("Deposit Transaction creation failed.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Deposit Transaction creation failed.");
            e.printStackTrace();
        }
    }

    public static int createTransaction(OracleConnection connection, int tax_id){
        int transaction_id = 0;
        while(true){
            Random random = new Random();
            transaction_id = random.nextInt(Integer.MAX_VALUE);
            String selectQuery = "SELECT * FROM Transactions WHERE tid = " + Integer.toString(transaction_id);
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(selectQuery);
                if (resultSet.next()) {
                    continue;
                }
                else{
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Generated Transaction Key: " + transaction_id);
        String insertQuery = "INSERT INTO Transactions (tid, date_executed) VALUES (?, ?)";

        //Date date = Demo.getCurrentDate(connection);
        Date date = new Date(System.currentTimeMillis());
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, transaction_id);
            preparedStatement.setDate(2, date);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Transaction created successfully!");
            } else {
                System.out.println("Transaction creation failed.");
            }
        } catch (SQLException e) {
            System.out.println("Transaction Account creation failed.");
            e.printStackTrace();
        }
        insertIntoCommits(connection, transaction_id, tax_id);
        
        return(transaction_id);
    }

    public static void insertIntoCommits(OracleConnection connection, int transaction_id, int tax_id){
        String insertQuery = "INSERT INTO Commits (acc_id, tid, tax_id) VALUES (?, ?, ?)";
        int acc_id = getAccountId(tax_id, connection);

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, acc_id);
            preparedStatement.setInt(2, transaction_id);
            preparedStatement.setInt(3, tax_id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Commit created successfully!");
            } else {
                System.out.println("Commit creation failed.");
            }
        } catch (SQLException e) {
            System.out.println("Commit creation failed.");
            e.printStackTrace();
        }
    }

    private static void showTransactionHistory(OracleConnection connection, int tax_id) {
        // Implement show transaction history logic
        System.out.println("Show Transaction History option selected.");
        ArrayList<Integer> transactions = new ArrayList<Integer>();
        transactions = getUserTransactions(connection, tax_id);
        for(int i = 0; i < transactions.size(); i++) {
            //System.out.println(transactions.get(i));
        }
    }

    private static void listStockPrice(OracleConnection connection, Scanner scanner) {
        // Implement list stock price logic
        System.out.println("List Current Stock Price option selected.");
        System.out.println("Please enter the symbol of the stock you would like to view: ");
        String symbol = scanner.nextLine();
        double stockPrice = getStockPrice(connection, symbol);
        if(stockPrice != -1.0) {
            System.out.println("Stock exists.");
            System.out.println("Current price of " + symbol + " is: " + stockPrice);
            String selectQuery = "SELECT * FROM Stock_Actor WHERE symbol = " + "'" + symbol + "'";
            try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if(resultSet.next()) {
                System.out.println("Actor Name: " + resultSet.getString("actor_name"));
                System.out.println("Date of Birth: " + resultSet.getString("date_of_birth").substring(0, 10));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        }
        else{
            System.out.println("Stock does not exist.");
        }

    }

    private static void actorProfile() {
        // Implement actor profile logic
        System.out.println("Actor Profile option selected.");
    }

    private static void movieInformation(OracleConnection connection, Scanner scanner) {
        // Implement movie information logic
        System.out.println("Movie Information option selected.");
        System.out.println("Enter the name of the movie you would like to view: ");
        String movieName = scanner.nextLine();
        System.out.println("Enter the year of the movie you would like to view: ");
        int year = 0;
        while (true) {
            System.out.print("Type the year of the movie you wish to view: ");
            try {
                year = Integer.parseInt(scanner.nextLine());
                break;  // Exit the loop if the input is a valid integer
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer. Try again.");
            }
        }
        String selectQuery = "SELECT * FROM Movie WHERE title = " + "'" + movieName + "'" + " AND movie_year = " + Integer.toString(year);
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if(resultSet.next()) {
                System.out.println("Movie Title: " + resultSet.getString("title"));
                System.out.println("Year: " + resultSet.getString("movie_year"));
                System.out.println("Rating: " + resultSet.getString("rating"));
            }
            else{
                System.out.println("Movie does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean doesAccountExist(OracleConnection connection, int tax_id) {
        String selectQuery = "SELECT * FROM Account_Has WHERE tax_id = " + Integer.toString(tax_id);
        try (Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(
                    selectQuery);
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions that may occur during database access
        }

        return (false);
    }

    
    public static ArrayList<Integer> getUserTransactions(OracleConnection connection, int tax_id) {
        ArrayList<Integer> transactions = new ArrayList<Integer>();
        String selectQuery = "SELECT * FROM Commits WHERE tax_id = " + Integer.toString(tax_id);
        try (Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(
                    selectQuery);
            while (resultSet.next()) {
                transactions.add(resultSet.getInt("tid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions that may occur during database access
        }

        return (transactions);
    }

    private static void listAllReviews(OracleConnection connection, Scanner scanner){
        System.out.println("List all Reviews for Movie option selected.");
        System.out.println("Enter the name of the movie you would like to view: ");
        String movieName = scanner.nextLine();
        System.out.println("Enter the year of the movie you would like to view: ");
        int year = 0;
        while (true) {
            System.out.print("Type the year of the movie you wish to view: ");
            try {
                year = Integer.parseInt(scanner.nextLine());
                break;  // Exit the loop if the input is a valid integer
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer. Try again.");
            }
        }
        String selectQuery = "SELECT * FROM Review WHERE title = " + "'" + movieName + "'" + " AND year_written = " + Integer.toString(year);
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            int count = 1;
            boolean found = false;
            while(resultSet.next()) {
                found = true;
                System.out.println("Review " + count + ": " + resultSet.getString("written_review"));
                count++;
            }
            if (found == false) {
                System.out.println("Movie does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    //declare helper functions to print transactions
    

}
