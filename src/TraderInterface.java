import java.util.Scanner;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.SwingUtilities;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;

public class TraderInterface {
    public static void main2(OracleConnection connection, int tax_id) {
        Scanner scanner = new Scanner(System.in);

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
                    deposit();
                    break;
                case 2:
                    withdraw();
                    break;
                case 3:
                    buy();
                    break;
                case 4:
                    sell();
                    break;
                case 5:
                    cancel();
                    break;
                case 6:
                    showBalance();
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

    private static void deposit() {
        // Implement deposit logic
        System.out.println("Deposit option selected.");
    }

    private static void withdraw() {
        // Implement withdraw logic
        System.out.println("Withdraw option selected.");
    }

    private static void buy() {
        // Implement buy logic
        System.out.println("Buy option selected.");
    }

    private static void sell() {
        // Implement sell logic
        System.out.println("Sell option selected.");
    }

    private static void cancel() {
        // Implement cancel logic
        System.out.println("Cancel option selected.");
    }

    private static void showBalance() {
        // Implement show balance logic
        System.out.println("Show Balance option selected.");
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
}
