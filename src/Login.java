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
import java.sql.PreparedStatement;

public class Login {

    public static void main2(OracleConnection connection) {

        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Enter Username and Password");
            System.out.println("2. Create an Account");
            System.out.println("3. Exit");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline
            
            switch (choice) {
                case 1:
                    authenticateUser(scanner, connection);
                    break;
                case 2:
                    createAccount(scanner, connection);
                    break;
                case 3:
                    System.out.println("Exiting program.");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please choose 1, 2, or 3.");
            }
        }
    }

    private static void authenticateUser(Scanner scanner, OracleConnection connection) {
            try {
                System.out.print("Enter your username: ");
                String username = scanner.nextLine();
                System.out.println("Username: " + username);
    
                System.out.print("Enter your password: ");
                String password = scanner.nextLine();
                System.out.println("Password: " + password);
    
                //String selectQuery = "SELECT * FROM Customer WHERE username = ? AND password = ?";

                //String selectQuery = "SELECT * FROM Customer WHERE username = 'kevin' AND password = 'lavelle'";

                String selectQuery = "SELECT * FROM Customer WHERE username = " + "'" + username + "'" + " AND password = " + "'" + password + "'";
    
                try (Statement statement = connection.createStatement()) {

    
                    ResultSet resultSet = statement.executeQuery(
                        selectQuery
                    );
    
                    if (resultSet.next()) {
                        System.out.println("Authentication successful!");
                        System.out.println("Welcome, " + resultSet.getString("cname") + "!");
                        TraderInterface.main2(connection, resultSet.getInt("tax_id"));
                    } else {
                        System.out.println("Authentication failed. Please check your username and password.");
                    }
                }
            } catch (SQLException e) {
                System.out.println("ERROR: Authentication failed.");
                e.printStackTrace();
            }
    }

    private static void createAccount(Scanner scanner, Connection connection) {
        try {
            System.out.print("Enter state_id: ");
            int stateId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            System.out.print("Enter tax_id: ");
            int taxId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            System.out.print("Enter cname: ");
            String cname = scanner.nextLine();

            System.out.print("Enter phone: ");
            String phone = scanner.nextLine();

            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            String insertQuery = "INSERT INTO Customer (state_id, tax_id, cname, phone, email, username, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setInt(1, stateId);
                preparedStatement.setInt(2, taxId);
                preparedStatement.setString(3, cname);
                preparedStatement.setString(4, phone);
                preparedStatement.setString(5, email);
                preparedStatement.setString(6, username);
                preparedStatement.setString(7, password);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Account created successfully!");
                } else {
                    System.out.println("Account creation failed.");
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Account creation failed.");
            e.printStackTrace();
        }
    }

    
}
