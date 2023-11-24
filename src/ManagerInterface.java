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
            System.out.println("=======================================================================================================================");
            System.out.println("Manager Interface Options:");
            System.out.println("1. Add Interest");
            System.out.println("2. Generate Montly Statement");
            System.out.println("3. List Active Customers");
            System.out.println("4. Generate Government Drug & Tax Evasion Report (DTER)");
            System.out.println("5. Customer Report");
            System.out.println("6. Delete Transactions");
            System.out.println("7. Change Monthly Interest Rate");
            System.out.println("0. Exit");
            System.out.println("=======================================================================================================================");

            System.out.print("Enter your choice (0-7): ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.println("=======================================================================================================================");
            switch (choice) {
                case 1:
                    addInterest(connection,tax_id);
                    break;
                case 2:
                    generateMonthlyStatement(connection, scanner);
                    break;
                case 3:
                    listActiveCustomers(connection);
                    break;
                case 4:
                    generateDTER();
                    break;
                case 5:
                    customerReport(connection, scanner);
                    break;
                case 6:
                    deleteTransactions(connection);
                    break;
                case 7:
                    changeMonthlyInterest(connection, scanner);
                    break;
                case 0:
                    System.out.println("Exiting Manager Interface.");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please choose a valid option (0-7).");
            }
        }
    }
    public static void addInterest(OracleConnection connection, int tax_id) {
        String curr_date = Demo.getDate(connection,1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate curr_date2 = LocalDate.parse(curr_date, formatter);
        
        // System.out.println(curr_date2.monthrange());
        if(curr_date2.getDayOfMonth() == curr_date2.lengthOfMonth()) {
            String selectQuery1 = ("SELECT monthlyInterest " + 
                                "FROM Manager " +  
                                "WHERE tax_id = " + tax_id);
            String selectQuery2 = "SELECT tax_id, balance " +
                                    "FROM Market_Account";
            String insertQuery1 = "INSERT INTO Interests (tid, monthly_interest) " +
                                    "VALUES (?, ?)";
            
            try(Statement statement = connection.createStatement()) {
                ResultSet resultSet1 = statement.executeQuery(selectQuery1);
                resultSet1.next();
                float monthlyInterest = resultSet1.getFloat("monthlyInterest");
                String updateQuery1 = ("UPDATE Market_Account " + 
                                    "SET balance = balance + balance * " + monthlyInterest);
                statement.executeQuery(updateQuery1);
                ResultSet resultSet2 = statement.executeQuery(selectQuery2);
                while(resultSet2.next()) {
                    int acc_tax_id = resultSet2.getInt("tax_id");
                    float balance = resultSet2.getFloat("balance");
                    int tid = TraderInterface.createTransaction(connection, acc_tax_id);
                    try(PreparedStatement preparedStatement = connection.prepareStatement(insertQuery1)){
                        preparedStatement.setInt(1, tid);
                        preparedStatement.setFloat(2,balance * monthlyInterest);
                        preparedStatement.executeUpdate();
                    }
                }
                System.out.println("Added monthly interest rate of " + monthlyInterest + " to all customer market accounts!");
                //add to commits and transaction and Intersts table
            }  catch (SQLException e) {e.printStackTrace();}
        } else {
            System.out.println("Sorry you are only able to add appropriate monthly interest on the last business day of the month. Please try again later!");
        }
    }    
    public static void generateMonthlyStatement(OracleConnection connection, Scanner scanner) {
        System.out.print("Please enter the tax id associated with the customer that you would like to receive a general monthly statement for: ");
        while(true) {
            if(scanner.hasNextInt()) {
                int correct_id = scanner.nextInt();
                System.out.println("=======================================================================================================================");
                generateListOfTransaction(connection, 0, correct_id);
                break;
            } else {
                String wrong_id = scanner.next();
                if("q".equalsIgnoreCase(wrong_id)) break;
                System.out.println("Sorry, we could not find a customer associated with that tax id.");
                System.out.print("Please enter a new tax id or type 'q' to return to Manager Interface: ");
            }
        }

    }    
    public static void listActiveCustomers(OracleConnection connection) {
        String curr_date = Demo.getDate(connection,1);
        String selectQuery = ("SELECT C.* " + 
                                "FROM Customer C, (SELECT T.tax_id, SUM(T.sumShares) AS totalShares " + 
                                    "FROM (SELECT C.tax_id, SUM(B.shares) AS sumShares " +
                                        "FROM Buy B, Transactions T, Commits C " +
                                        "WHERE B.tid = T.tid AND C.tid = T.tid AND EXTRACT(MONTH FROM T.date_executed) = " + "EXTRACT(MONTH FROM TO_DATE('" + curr_date + "', 'yyyy-mm-dd'))" +
                                        "GROUP BY C.tax_id " +
                                        "UNION " +
                                        "SELECT C.tax_id, SUM(S.shares) AS sumShares " +
                                        "FROM Sell S, Transactions T, Commits C " +
                                        "WHERE S.tid = T.tid AND C.tid = T.tid AND EXTRACT(MONTH FROM T.date_executed) = " +  "EXTRACT(MONTH FROM TO_DATE('" + curr_date + "', 'yyyy-mm-dd'))" +
                                        "GROUP BY C.tax_id) T " + 
                                    "GROUP BY T.tax_id) T2 " + 
                                "WHERE C.tax_id = T2.tax_id AND T2.totalShares >= 1000");
        try(Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            System.out.println("Here is a list of all customers who have bought or sold at least 1000 shares this month...");
            while(resultSet.next()) {
                String state_id = resultSet.getString("state_id").trim();
                int tax_id = resultSet.getInt("tax_id");
                String cname = resultSet.getString("cname").trim();
                String phone = resultSet.getString("phone").trim();
                String email = resultSet.getString("email").trim();
                String username = resultSet.getString("username").trim();
                String password = resultSet.getString("password").trim();
                System.out.println("State id: " + state_id + ", Tax id: " + tax_id + ", Customer Name: " + cname + ", Phone Number: " + phone + 
                ", Email: " + email + ", Username: " + username + ", Password: " + password);
            }
        } catch (SQLException e) {e.printStackTrace();}
    }    
    public static void generateDTER() {
        //
    }    

    public static void customerReport(OracleConnection connection, Scanner scanner) {
        System.out.print("Please enter the tax id associated with the customer that you would like to receive a customer report for: ");
        System.out.println("=======================================================================================================================");
        while(true) {
            if(scanner.hasNextInt()) {
                int correct_id = scanner.nextInt();
                String selectQuery1 = "SELECT * FROM Customer WHERE tax_id = " + correct_id;
                String selectQuery2 = "SELECT * FROM Market_Account WHERE tax_id = " + correct_id;
                String selectQuery3 = "SELECT * FROM Stock_Account WHERE tax_id = " + correct_id;
                try(Statement statement = connection.createStatement()) {
                    ResultSet resultSet1 = statement.executeQuery(selectQuery1);
                    resultSet1.next();
                    String state_id = resultSet1.getString("state_id").trim();
                    int tax_id = resultSet1.getInt("tax_id");
                    String cname = resultSet1.getString("cname").trim();
                    String phone = resultSet1.getString("phone").trim();
                    String email = resultSet1.getString("email").trim();
                    String username = resultSet1.getString("username").trim();
                    String password = resultSet1.getString("password").trim();
                    ResultSet resultSet2 = statement.executeQuery(selectQuery2);
                    resultSet2.next();
                    float balance = resultSet2.getFloat("balance");
                    int acc_id = resultSet2.getInt("acc_id");
                    ResultSet resultSet3 = statement.executeQuery(selectQuery3);
                    System.out.println("Here is the customer you were searching for!");
                    System.out.println("=======================================================================================================================");
                    System.out.println("State id: " + state_id );
                    System.out.println("Tax id: " + tax_id);
                    System.out.println("Customer Name: " + cname);
                    System.out.println("Phone Number: " + phone);
                    System.out.println("Email: " + email);
                    System.out.println("Username: " + username);
                    System.out.println("Password: " + password);
                    System.out.println("=======================================================================================================================");
                    System.out.println("Market Account: " + acc_id);
                    System.out.println("Balance: " + balance);
                    System.out.println("=======================================================================================================================");
                    System.out.println("Here are the stocks that they own!");
                    while(resultSet3.next()) {
                        System.out.println("=======================================================================================================================");
                        String symbol = resultSet3.getString("symbol").trim();
                        int num_share = resultSet3.getInt("num_share");
                        float balance_share = resultSet3.getFloat("balance_share");
                        System.out.println("Symbol: " + symbol + ", Number of Shares: " + num_share + ", Market Value: "  + balance_share);
                    }
                } catch (SQLException e) {e.printStackTrace();}
                break;
            } else {
                String wrong_id = scanner.next();
                if("q".equalsIgnoreCase(wrong_id)) break;
                System.out.println("Sorry, we could not find a customer associated with that tax id.");
                System.out.print("Please enter a new tax id or type 'q' to return to Manager Interface: ");
            }
        }
        
    }    
    public static void deleteTransactions(OracleConnection connection) {
        String deleteQuery = "DELETE FROM Transactions";
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate(deleteQuery);
            System.out.println("Successfully deleted the list of transactions for all customers!");
        } catch (SQLException e) {e.printStackTrace();}
    }
    public static void changeMonthlyInterest(OracleConnection connection, Scanner scanner) {
        // fix interest, have to take
        System.out.print("Enter the desired monthly interest rate to be set for all customers. (Ex: 0.01): ");
        while(true) {
            if(scanner.hasNextFloat()) {
                float temp = scanner.nextFloat();
                if(temp < 0) {
                    System.out.println("Please enter a valid decimal > 0 or type 'q' to return to Manager Interface.");
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
                if("q".equalsIgnoreCase(input)) {
                    break;
                } else {
                    System.out.println("Please enter a valid decimal > 0 or type 'q' to return to Manager Interface.");
                }
            }
        }
    }    
    public static void generateListOfTransaction(OracleConnection connection, int flag, int tax_id) {
        String curr_date = Demo.getDate(connection,1);
        String[] types_trans = {"Buy", "Sell", "Withdraw", "Deposit", "Cancels", "Interests"};
        for(int i = 0; i < types_trans.length; i++) {
            String curr_type = types_trans[i];
            // System.out.print(curr_type[0]);
            String selectQuery = "SELECT A.acc_id, T.date_executed, " + curr_type.charAt(0) +  ".* " + 
                                    "FROM Account_has A, Commits O, Transactions T, " + curr_type + " " + curr_type.charAt(0) + 
                                    " WHERE A.tax_id = O.tax_id AND A.acc_id = O.acc_id AND O.tid = T.tid AND " + curr_type.charAt(0) + ".tid = T.tid";
            try(Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(selectQuery);
                while(resultSet.next()) {
                    int acc_id = resultSet.getInt("acc_id");
                    String date_executed = resultSet.getString("date_executed").trim();
                    int tid = resultSet.getInt("tid");
                    System.out.print("Account id: " + acc_id + ", Date executed: " + date_executed + ", Transaction id: " + tid);
                    if(i == 0) {
                        String symbol = resultSet.getString("symbol").trim();
                        int shares = resultSet.getInt("shares");
                        float buy_price = resultSet.getFloat("buy_price");
                        System.out.print(", Stock symbol: " + acc_id + ", Shares: " + shares + ", Buy price: " + buy_price + "\n");
                    } else if(i == 1) {
                        String symbol = resultSet.getString("symbol").trim();
                        int shares = resultSet.getInt("shares");
                        float sell_price = resultSet.getFloat("sell_price");
                        System.out.print(", Stock symbol: " + acc_id + ", Shares: " + shares + ", Sell price: " + sell_price + "\n");
                    } else if(i == 2) {
                        float amount = resultSet.getFloat("amount");
                        System.out.print(", Withdrawed amount: " + amount + "\n");
                    } else if(i == 3) {
                        float amount = resultSet.getFloat("amount");
                        System.out.print(", Deposited amount: " + amount + "\n");
                    } else if(i == 4) {
                        int ptid = resultSet.getInt("ptid");
                        System.out.print(", Cancelled transaction id: " + ptid + "\n");
                    }
                    else {
                        float monthly_interest = resultSet.getFloat("monthly_interest");
                        System.out.print(", Monthly interest earned: $" + monthly_interest + "\n");
                    }
                }
            } catch (SQLException e) {e.printStackTrace();}

        }



        
}

}
