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
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class ManagerInterface {
    public static void parentFunction(OracleConnection connection, int tax_id) {
        Scanner scanner = new Scanner(System.in);
        int accountId = TraderInterface.getAccountId(tax_id, connection);

        while (true) {
            System.out.println("Manager Interface Options:");
            System.out.println("1. Add Interest");
            System.out.println("2. Generate Montly Statement");
            System.out.println("3. List Active Customers");
            System.out.println("4. Generate Government Drug & Tax Evasion Report (DTER)");
            System.out.println("5. Customer Report");
            System.out.println("6. Delete Transactions");
            System.out.println("0. Exit");

            System.out.print("Enter your choice (0-6): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline


            switch (choice) {
                case 1:
                    addInterest(connection);
                    break;
                case 2:
                    generateMonthlyStatement();
                    break;
                case 3:
                    listActiveCustomers();
                    break;
                case 4:
                    generateDTER();
                    break;
                case 5:
                    customerReport();
                    break;
                case 6:
                    deleteTransactions();
                    break;
                case 0:
                    System.out.println("Exiting Manager Interface.");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please choose a valid option (0-6).");
            }
        }
    }
    public static void addInterest(OracleConnection connection) {
        String selectQuery = "SELECT * FROM Current_Time";
        String curr_date = "";
        try(Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if(resultSet.next()) {
                curr_date = resultSet.getString("curr_date");
            }
        } catch (SQLException e) {e.printStackTrace();}
        LocalDate date = LocalDate.parse(curr_date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if(date.getDayOfMonth() == date.withDayOfMonth(date.lengthOfMonth()).getDayOfMonth()) {
            //will finish once monthly statement is done. IDK what monthly interest rate is
        } else {
            System.out.println("\nSorry you are only able to add appropriate monthly interest on the last business day of the month. Please try again later!\n");
        }
    }    
    public static void generateMonthlyStatement() {
        //
    }    
    public static void listActiveCustomers() {
        //
    }    
    public static void generateDTER() {
        //
    }    
    public static void customerReport(OracleConnection connection) {
        String selectQuery
    }    
    public static void deleteTransactions() {
        //
    }    

}
