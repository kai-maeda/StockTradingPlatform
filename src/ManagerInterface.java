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
import java.sql.ResultSetMetaData;


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
            System.out.println("7. Change Monthly Interest Rate");
            System.out.println("0. Exit");

            System.out.print("Enter your choice (0-7): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline
            System.out.println("=======================================================================================================================");


            switch (choice) {
                case 1:
                    addInterest(connection);
                    break;
                case 2:
                    generateMonthlyStatement();
                    break;
                case 3:
                    listActiveCustomers(connection);
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
                case 7:
                    changeMonthlyInterest(connection, scanner);
                    break;
                case 0:
                    System.out.println("Exiting Manager Interface.");
                    System.out.println("=======================================================================================================================");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please choose a valid option (0-7).");
                    System.out.println("=======================================================================================================================");
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
    public static void listActiveCustomers(OracleConnection connection) {
        //Need to implement that first query is labed AS. only takes when sumshares > 1000. fix so that it also takes into account sell
        String selectQuery = ("SELECT C.tax_id, SUM(B.shares) AS sumShares " + 
                              "FROM Buy B, Transactions T, Commits C " + 
                              "WHERE B.tid = T.tid AND C.tid = T.tid AND EXTRACT(MONTH FROM T.date_executed) = 11 " + 
                              "GROUP BY C.tax_id");
        try(Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            System.out.println("Here is a list of all customers who have bought or sold at least 1000 shares this month...");
            
            while(resultSet.next()) {
                int tax_id = resultSet.getInt("tax_id");
                selectQuery = ("SELECT * " +
                               "FROM Customer C " +
                              "WHERE C.tax_id = " + "'" + tax_id + "'" + " ");
                try(Statement statement2 = connection.createStatement()) {
                    ResultSet resultSet2 = statement2.executeQuery(selectQuery);
                    
                    if(resultSet2.next()) {
                        String state_id = resultSet2.getString("state_id").trim();
                        tax_id = resultSet2.getInt("tax_id");
                        String cname = resultSet2.getString("cname").trim();
                        String phone = resultSet2.getString("phone").trim();
                        String email = resultSet2.getString("email").trim();
                        String username = resultSet2.getString("username").trim();
                        String password = resultSet2.getString("password").trim();
                        System.out.println("State_id: " + state_id + ", Tax_id: " + tax_id + ", Customer Name: " + cname + ", Phone Number: " + phone + 
                        ", Email: " + email + ", Username: " + username + ", Password: " + password);
                    }
                }
            }
            System.out.println("=======================================================================================================================");
        } catch (SQLException e) {e.printStackTrace();}
    }    
    public static void generateDTER() {
        //
    }    

    public static void customerReport() {
        //
    }    
    public static void deleteTransactions() {
        //
    }
    public static void changeMonthlyInterest(OracleConnection connection, Scanner scanner) {
        System.out.println("Enter the desired monthly interest rate to be set for all customers. Ex: 0.01");
        while(true) {
            if(scanner.hasNextFloat()) {
                float temp = scanner.nextFloat();
                if(temp < 0) {
                    System.out.println("Please enter a valid decimal > 0 or type 'Escape' to return to Manager Interface.");
                } else {
                    String updateQuery = "UPDATE Manager SET monthlyInterest = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                        preparedStatement.setFloat(1, temp);
                        preparedStatement.executeUpdate();
                    } catch(SQLException e) {e.printStackTrace();}
                    break;
                }
            }else {
                String input = scanner.next();
                if("Escape".equalsIgnoreCase(input)) {
                    break;
                } else {
                    System.out.println("Please enter a valid decimal > 0 or type 'Escape' to return to Manager Interface.");
                }
            }
        }
    }    

}
