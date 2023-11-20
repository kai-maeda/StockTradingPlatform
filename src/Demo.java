import java.sql.Statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

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
import java.sql.Date;

public class Demo {

    public static void initalizeInterface(OracleConnection connection, Scanner scanner) {
        while (true) {
            System.out.println("Welcome to the Demo Interface:");
            System.out.println("The current date is: " + getDate(connection));
            System.out.println("1. Open Market");
            System.out.println("2. Close Market");
            System.out.println("3. Go to Next Day");
            System.out.println("4. Change Stock Price");

            System.out.print("Enter your choice (1-3): ");

            int choice = 0;

            while (true) {
                try {
                    System.out.print("Enter your choice (1-3): ");
                    choice = scanner.nextInt();

                    if (choice >= 1 && choice <= 4) {
                        break; // Valid input, exit the loop
                    } else {
                        System.out.println("Invalid input. Please enter a number between 1 and 3.");
                    }
                } catch (java.util.InputMismatchException e) {
                    // Handle non-integer input
                    System.out.println("Invalid input. Please enter a number between 1 and 3.");
                    scanner.nextLine(); // Consume the invalid input
                }
            }

            scanner.nextLine();

            switch (choice) {
                case 1:
                    openMarket(connection);
                    break;
                case 2:
                    closeMarket(connection);
                    break;
                case 3:
                    goToNextDay(connection);
                    break;
                case 4: 
                    promptChangeStockPrice(connection, scanner);
                    break;
                default:
                    System.out.println("Invalid input. Please enter a number between 1 and 3.");
            }

        }
    }

    public static String getDate(OracleConnection connection) {
        String selectQuery = "SELECT * FROM Current_Time";
        String curr_date = "";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if (resultSet.next()) {
                curr_date = resultSet.getString("curr_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Current Date: " + curr_date);
        return (curr_date);
    }

    public static boolean isOpen(OracleConnection connection) {
        String selectQuery = "SELECT * FROM Current_Time";
        String curr_date = "";
        boolean isOpen;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if (resultSet.next()) {
                curr_date = resultSet.getString("curr_date");
                if (resultSet.getInt("is_open") == 1) {
                    return (true);
                } else if (resultSet.getInt("is_open") == 0) {
                    return (false);
                } else {
                    System.out.println("CRITICAL ERROR: Invalid value for is_open.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }





        return (false);
    }

    public static void openMarket(OracleConnection connection) {
        if (isOpen(connection) == true) {
            System.out.println("Market is already open.");
            return;
        }
        String updateQuery = "UPDATE Current_Time SET is_open = 1";
        try (Statement statement = connection.createStatement()) {
            int rowsAffected = statement.executeUpdate(updateQuery);
            if (rowsAffected > 0) {
                System.out.println("Market opened successfully!");
            } else {
                System.out.println("Market opening failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeMarket(OracleConnection connection) {
        if (isOpen(connection) == false) {
            System.out.println("Market is already closed.");
            return;
        }
        String updateQuery = "UPDATE Current_Time SET is_open = 0";
        try (Statement statement = connection.createStatement()) {
            int rowsAffected = statement.executeUpdate(updateQuery);
            if (rowsAffected > 0) {
                System.out.println("Market closed successfully!");
            } else {
                System.out.println("Market closing failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void goToNextDay(OracleConnection connection) {
        String selectQuery = "SELECT * FROM Current_Time";
        String curr_date = "";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if (resultSet.next()) {
                curr_date = resultSet.getString("curr_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date customDate = sdf.parse(curr_date);
            Calendar c = Calendar.getInstance();
            c.setTime(customDate);
            c.add(Calendar.DATE, 1);
            customDate = c.getTime();
            Date sqlDate = new Date(customDate.getTime());
            String updateQuery = "UPDATE Current_Time SET curr_date = TO_DATE(" + "'" + sqlDate + "', 'YYYY-MM-DD')";
            try (Statement statement = connection.createStatement()) {
                int rowsAffected = statement.executeUpdate(updateQuery);
                if (rowsAffected > 0) {
                    System.out.println("Date updated successfully!");
                } else {
                    System.out.println("Date update failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void promptChangeStockPrice(OracleConnection connection, Scanner scanner){
        System.out.println("Enter the symbol of the stock you want to change the price of: ");
        String symbol = scanner.nextLine();
        if(TraderInterface.getStockPrice(connection, symbol) == -1){
            System.out.println("Stock does not exist.");
            return;
        }
        double price = 0;
        while (true) {
                try {
                    System.out.println("Enter the new price of the stock: ");
                    double choice = scanner.nextInt();

                    if (choice > 0) {
                        break; // Valid input, exit the loop
                    } else {
                        System.out.println("Invalid input. Please enter a number greater than zero.");
                    }
                } catch (java.util.InputMismatchException e) {
                    // Handle non-integer input
                    System.out.println("Invalid input. Please enter a number greater than zero.");
                    scanner.nextLine(); // Consume the invalid input
                }
            }
        scanner.nextLine();
        changeStockPrice(connection, scanner, price, symbol);
    }

    private static void changeStockPrice(OracleConnection connection, Scanner scanner, double price, String symbol) {


        String updateQuery = "UPDATE Stock_Actor SET current_price = " + Double.toString(price) + " WHERE symbol = '" + symbol + "'";

        try (Statement statement = connection.createStatement()) {

            int rowsAffected = statement.executeUpdate(updateQuery);
            if (rowsAffected > 0) {
                System.out.println("Change Stock Price Successful");
            } else {
                System.out.println("Change Stock Price Failed.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Change Stock Price Failed.");
            e.printStackTrace();
        }
    }

}
