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
import java.text.DecimalFormat;


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
                    generateDTER(connection);
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
                    System.out.println("Exiting Manager Interface...");
                    StartupOptions.main2(connection);
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
        System.out.print("Please enter the tax ID associated with the customer that you would like to receive a general monthly statement for: ");
        while(true) {
            if(scanner.hasNextInt()) {
                int correct_id = scanner.nextInt();
                System.out.println("=======================================================================================================================");
                if(checkIfMarketAccountExists(connection, correct_id)) {
                    System.out.println("Sorry this person has not created a market account yet so there is no monthly statement to print.");
                    break;
                }
                float dud = generateListOfTransaction(connection, 0, correct_id);
                break;
            } else {
                String wrong_id = scanner.next();
                if("q".equalsIgnoreCase(wrong_id)) break;
                System.out.println("Sorry, we could not find a customer associated with that tax ID.");
                System.out.print("Please enter a new tax ID or type 'q' to return to Manager Interface: ");
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
                System.out.println("State ID: " + state_id + ", Tax ID: " + tax_id + ", Customer Name: " + cname + ", Phone Number: " + phone + 
                ", Email: " + email + ", Username: " + username + ", Password: " + password);
            }
        } catch (SQLException e) {e.printStackTrace();}
    }    
    public static void generateDTER(OracleConnection connection) {
        String selectQuery = "SELECT * FROM Customer";
        try(Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            System.out.println("Generating list of customers who have made more than $10,000 in the past month...");
            while(resultSet.next()) {
                int tax_id = resultSet.getInt("tax_id");
                float total_earnings = generateListOfTransaction(connection, 1, tax_id);
                if(total_earnings >= 10000) {
                    String state_id = resultSet.getString("state_id").trim();
                    String cname = resultSet.getString("cname").trim();
                    String phone = resultSet.getString("phone").trim();
                    String email = resultSet.getString("email").trim();
                    String username = resultSet.getString("username").trim();
                    String password = resultSet.getString("password").trim();
                    DecimalFormat df = new DecimalFormat("#.##");
                    String total_format = df.format(total_earnings);
                    System.out.println("State ID = " + state_id + ", Tax ID = " + tax_id + ", Name = " + cname + ", Phone number = " + phone + ", Email = " + email + ", Username = "+ username + ", Password = "+ password +", Total Earnings = $" + total_format);
                }
            }
        } catch (SQLException e) {e.printStackTrace();}
        //
    }    

    public static void customerReport(OracleConnection connection, Scanner scanner) {
        System.out.print("Please enter the tax ID associated with the customer that you would like to receive a customer report for: ");
        while(true) {
            if(scanner.hasNextInt()) {
                int correct_id = scanner.nextInt();
                System.out.println("=======================================================================================================================");
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
                    DecimalFormat df = new DecimalFormat("#.##");
                    String balance_format = df.format(balance);
                    int acc_id = resultSet2.getInt("acc_id");
                    ResultSet resultSet3 = statement.executeQuery(selectQuery3);
                    System.out.println("Here is the customer you were searching for!");
                    System.out.println("State ID: " + state_id );
                    System.out.println("Tax ID: " + tax_id);
                    System.out.println("Customer Name: " + cname);
                    System.out.println("Phone Number: " + phone);
                    System.out.println("Email: " + email);
                    System.out.println("Username: " + username);
                    System.out.println("Password: " + password);
                    System.out.println("=======================================================================================================================");
                    System.out.println("Market Account: " + acc_id);
                    System.out.println("Balance: $" + balance_format);
                    System.out.println("=======================================================================================================================");
                    System.out.println("Here are the stocks that they own!");
                    while(resultSet3.next()) {
                        String symbol = resultSet3.getString("symbol").trim();
                        int num_share = resultSet3.getInt("num_share");
                        float balance_share = resultSet3.getFloat("balance_share");
                        balance_format = df.format(balance_share);
                        System.out.println("Symbol: " + symbol + ", Number of Shares: " + num_share + ", Market Value: $"  + balance_format);
                    }
                } catch (SQLException e) {e.printStackTrace();}
                break;
            } else {
                String wrong_id = scanner.next();
                if("q".equalsIgnoreCase(wrong_id)) break;
                System.out.println("Sorry, we could not find a customer associated with that tax ID.");
                System.out.print("Please enter a new tax ID or type 'q' to return to Manager Interface: ");
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
                    System.out.println("Successfully changed the monthly interest rate of all customers to be " + temp + "!");
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
    public static float generateListOfTransaction(OracleConnection connection, int flag, int tax_id) {
        String curr_date = Demo.getDate(connection,1);
        String[] types_trans = {"Buy", "Sell", "Withdraw", "Deposit", "Cancels", "Interests"};
        float total_earnings = 0;
        float commissions = 0;
        DecimalFormat df = new DecimalFormat("#.##");
        //flag = 0 means for monthlyStatement, flag = 1 for DTER
        for(int i = 0; i < types_trans.length; i++) {
            String curr_type = types_trans[i];
            String selectQuery = "SELECT A.acc_id, T.date_executed, " + curr_type.charAt(0) +  ".* " + 
                                    "FROM Account_has A, Commits O, Transactions T, " + curr_type + " " + curr_type.charAt(0) + 
                                    " WHERE A.tax_id = O.tax_id AND A.acc_id = O.acc_id AND O.tid = T.tid AND " + curr_type.charAt(0) + ".tid = T.tid" +
                                    " AND A.tax_id = " + tax_id + " AND EXTRACT(MONTH FROM T.date_executed) = EXTRACT(MONTH FROM TO_DATE('" + curr_date + "', 'yyyy-mm-dd'))";
            try(Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(selectQuery);
                while(resultSet.next()) {
                    int acc_id = resultSet.getInt("acc_id");
                    String date_executed = resultSet.getString("date_executed").trim();
                    int tid = resultSet.getInt("tid");
                    if(flag == 0) System.out.print("Account ID: " + acc_id + ", Date executed: " + date_executed + ", Transaction ID: " + tid);
                    if(i == 0) {
                        commissions += 20;
                        String symbol = resultSet.getString("symbol").trim();
                        int shares = resultSet.getInt("shares");
                        float buy_price = resultSet.getFloat("buy_price");
                        if(flag == 0) System.out.print(", Stock symbol: " + symbol + ", Shares: " + shares + ", Buy price: " + buy_price + "\n");
                    } else if(i == 1) {
                        commissions += 20;
                        String symbol = resultSet.getString("symbol").trim();
                        float sell_price = resultSet.getFloat("sell_price");
                        int shares = resultSet.getInt("shares");
                        if(flag == 0) System.out.print(", Stock symbol: " + symbol + ", Shares: " + shares + ", Sell price: " + sell_price + "\n");
                        total_earnings += getSellLegs(connection,tid,sell_price, flag);
                    } else if(i == 2) {
                        float amount = resultSet.getFloat("amount");
                        if(flag == 0) System.out.print(", Withdrawed amount: " + amount + "\n");
                    } else if(i == 3) {
                        float amount = resultSet.getFloat("amount");
                        if(flag == 0) System.out.print(", Deposited amount: " + amount + "\n");
                    } else if(i == 4) {
                        commissions += 20;
                        int ptid = resultSet.getInt("ptid");
                        if(flag == 0) System.out.print(", Cancelled transaction ID: " + ptid + "\n");
                    }
                    else {
                        float monthly_interest = resultSet.getFloat("monthly_interest");
                        total_earnings += monthly_interest;
                        if(flag == 0) System.out.print(", Monthly interest earned: $" + monthly_interest + "\n");
                    }
                }
            } catch (SQLException e) {e.printStackTrace();}
        }    
        if(flag == 0) System.out.println("=======================================================================================================================");
        getInitialAndFinalBalance(connection, tax_id, flag);
        String total_format = df.format(total_earnings);
        String commisions_format = df.format(commissions);
        if(total_earnings >= 0) {
            if(flag == 0) System.out.println("Total earnings this month = $" + total_format);
        } else {
            if(flag == 0) System.out.println("Total loss this month = $" + total_format);
        }
        if(flag == 0) System.out.println("Total commission paid = $" + commisions_format);
        return total_earnings;
    }
    public static int getSellLegs(OracleConnection connection, int tid, float sell_price, int flag) {
        int money_earned = 0;
        String selectQuery = "SELECT  L.* FROM Sell_leg L, Sell S WHERE L.tid = S.tid AND S.tid = " + tid; 
        try(Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while(resultSet.next()) {
                float bought_price = resultSet.getFloat("bought_price");
                DecimalFormat df = new DecimalFormat("#.##");
                String bought_format = df.format(bought_price);
                int shares = resultSet.getInt("shares");
                int leg_id = resultSet.getInt("leg_id");
                if(flag == 0) System.out.println("Leg ID = " + leg_id + ", Bought price = $" + bought_format + ", Shares = " + shares);
                money_earned += (sell_price - bought_price)*shares;
            }
        } catch (SQLException e) {e.printStackTrace();}
        return money_earned;
    }
    public static void getInitialAndFinalBalance(OracleConnection connection, int tax_id, int flag) {
        String curr_date = Demo.getDate(connection,1);
        DecimalFormat df = new DecimalFormat("#.##");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate curr_date2 = LocalDate.parse(curr_date, formatter2);
        Month month1 = curr_date2.getMonth();
        String selectQuery = "SELECT T.* FROM Market_Account M, Temp_money T WHERE tax_id = " + tax_id + 
                            " AND M.acc_id = T.acc_id ORDER BY T.balance_date DESC";
        String final_balance="0";
        String initial_balance = "0";
        try(Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if(resultSet.next()) {
                float temp_balance = resultSet.getFloat("temp_balance");
                final_balance = df.format(temp_balance);
                String balance_date = resultSet.getString("balance_date").trim();
                LocalDate balance_date2 = LocalDate.parse(balance_date, formatter);
                Month month2 = balance_date2.getMonth();
                if(month1 != month2) {
                    initial_balance = df.format(temp_balance);
                    if(flag == 0) System.out.println("Inital account balance = $" + initial_balance + ", Final account balance = $" + final_balance);
                    return;
                }
            }
            while(resultSet.next()) {
                String balance_date = resultSet.getString("balance_date").trim();
                LocalDate balance_date2 = LocalDate.parse(balance_date, formatter);
                Month month2 = balance_date2.getMonth();
                if(month1 != month2 ) {
                    float temp_balance = resultSet.getFloat("temp_balance");
                    initial_balance = df.format(temp_balance);
                    if(flag == 0) System.out.println("Inital account balance = $" + initial_balance + ", Final account balance = $" + final_balance);
                    return;
                }
            }
            if(resultSet.previous()) {
                float temp_balance = resultSet.getFloat("temp_balance");
                initial_balance = df.format(temp_balance);
                if(flag == 0) System.out.println("Inital account balance = $" + initial_balance + " Final account balance = $" + final_balance);
            }
        } catch (SQLException e) {e.printStackTrace();}
    }
    public static boolean checkIfMarketAccountExists(OracleConnection connection, int tax_id) {
        String selectQuery = "SELECT * FROM Market_Account WHERE tax_id = " + tax_id;
        try(Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if(resultSet.next()) {
                return false;
            }
        } catch (SQLException e) {e.printStackTrace();}
        return true;
    }

}
