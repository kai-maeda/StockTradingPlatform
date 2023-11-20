import java.util.Scanner;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Random;

import javax.swing.SwingUtilities;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;

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
            System.out.println("9. Actor Profile");
            System.out.println("10. Movie Information");
            System.out.println("0. Exit");

            System.out.print("Enter your choice (0-10): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1:
                    deposit(scanner, connection, accountId);
                    break;
                case 2:
                    withdraw(scanner, connection, accountId);
                    break;
                case 3:
                    buy(connection, scanner, accountId);
                    break;
                case 4:
                    sell();
                    break;
                case 5:
                    cancel();
                    break;
                case 6:
                    showBalance(connection, accountId);
                    break;
                case 7:
                    showTransactionHistory();
                    break;
                case 8:
                    listStockPrice();
                    break;
                case 9:
                    actorProfile();
                    break;
                case 10:
                    movieInformation();
                    break;
                case 0:
                    System.out.println("Exiting Trader Interface.");
                    scanner.close();
                    System.exit(0);
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
        Random random = new Random();
        int acc_id = random.nextInt(Integer.MAX_VALUE);
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
                System.out.println("Account created successfully!");
            } else {
                System.out.println("Account creation failed.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Account creation failed.");
            e.printStackTrace();
        }
    }

    private static void deposit(Scanner scanner, OracleConnection connection, int accountId) {
        System.out.println("Deposit option selected.");
        while (true) {
            System.out.println("Please enter the amount you would like to deposit as a decimal number: ");
            if (scanner.hasNextDouble()) {
                double amount = scanner.nextDouble();
                // Process the valid double input (amount) here
                System.out.println("You entered: " + amount);
                depositSQL(amount, connection, accountId);
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

    private static void withdraw(Scanner scanner, OracleConnection connection, int accountId) {
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

    private static double getStockPrice(OracleConnection connection, String symbol) {
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

    private static void buy(OracleConnection connection, Scanner scanner, int acc_id) {
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
                //add the stock to a stock account
            }
        }
        else{
            System.out.println("Stock does not exist.");
        }
} 
        

    private static void sell() {
        // Implement sell logic
        System.out.println("Sell option selected.");
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

    private static void showTransactionHistory() {
        // Implement show transaction history logic
        System.out.println("Show Transaction History option selected.");
    }

    private static void listStockPrice() {
        // Implement list stock price logic
        System.out.println("List Current Stock Price option selected.");
    }

    private static void actorProfile() {
        // Implement actor profile logic
        System.out.println("Actor Profile option selected.");
    }

    private static void movieInformation() {
        // Implement movie information logic
        System.out.println("Movie Information option selected.");
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

}
