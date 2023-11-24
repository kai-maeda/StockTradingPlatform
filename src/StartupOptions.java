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

public class StartupOptions {
    public static void main2(OracleConnection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Startup Options:");
        System.out.println("1. Customer Interface");
        System.out.println("2. Manager Interface");
        System.out.println("3. Demo");
        System.out.println("0. Exit");
        System.out.println("Please select an option:");

        int option = scanner.nextInt();

        //scanner.nextLine(); // Consume the newline

        switch (option) {
            case 1:
                System.out.println("You selected Customer Interface.");
                Login.main2(connection, 1);
                // Add your customer interface logic here
                break;
            case 2:
                System.out.println("You selected Manager Interface.");
                Login.main2(connection, 2);
                // Add your manager interface logic here
                break;
            case 3:
                System.out.println("You selected Demo.");
                Demo.initalizeInterface(connection, scanner);
                // Add your demo logic here
                break;
            case 0:
                System.out.println("Exiting...");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please select a valid option (1/2/3).");
        }

        scanner.close();
    }
}
