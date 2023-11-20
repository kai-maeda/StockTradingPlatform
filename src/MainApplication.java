import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.SwingUtilities;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;

// javac -cp lib/ojdbc11.jar src/MainApplication.java src/Login.java src/DataInput.java src/StartupOptions.java src/TraderInterface.java src/ManagerInterface.java -d .
// java -cp .:lib/ojdbc11.jar MainApplication                           

public class MainApplication {

    //final static String DB_URL = "jdbc:oracle:thin:@kaidb_tp?TNS_ADMIN=/Users/kaimaeda/Wallet_KaiDB";
       final static String DB_URL = "jdbc:oracle:thin:@kaidb_tp?TNS_ADMIN=/Users/edwardlavelle/Desktop/Wallet_KaiDB";
    final static String DB_USER = "ADMIN";
    final static String DB_PASSWORD = "KaiOracle123";
    
    public static void main(String[] args) throws SQLException{

        Properties info = new Properties();

        System.out.println("Initializing connection properties...");
        info.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER);
        info.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD);
        info.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20");

        System.out.println("Creating OracleDataSource...");
        OracleDataSource ods = new OracleDataSource();

        System.out.println("Setting connection properties...");
        ods.setURL(DB_URL);
        ods.setConnectionProperties(info);

        try (OracleConnection connection = (OracleConnection) ods.getConnection()) {
            
            // You can continue to manage other parts of your application here
            System.out.println("Connection established!");
            // Get JDBC driver name and version
            DatabaseMetaData dbmd = connection.getMetaData();
            System.out.println("Driver Name: " + dbmd.getDriverName());
            System.out.println("Driver Version: " + dbmd.getDriverVersion());
            // Print some connection properties
            System.out.println(
                "Default Row Prefetch Value: " + connection.getDefaultRowPrefetch()
            );
            System.out.println("Database username: " + connection.getUserName());
            System.out.println();

            setDate(connection);
            //int tax_id = 1000;
            //ManagerInterface.parentFunction(connection, tax_id);
            DataInput.main3(connection);
            StartupOptions.main2(connection);


            
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database connection error gracefully
        }
    }
    
    public static void setDate(OracleConnection connection) {
        String insertQuery = "INSERT INTO Current_Time (curr_date) VALUES (?)";
        String current_date = "2023-10-16";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date customDate = sdf.parse(current_date);
            Date sqlDate = new Date(customDate.getTime());
            String selectQuery = "SELECT * FROM Current_Time WHERE curr_date = TO_DATE(" + "'" + current_date + "', 'YYYY-MM-DD')";
            try(Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(selectQuery);
                if(!resultSet.next()) {
                    try(PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        preparedStatement.setDate(1,sqlDate);
                        preparedStatement.executeUpdate();
                    }
                } 
            } catch (SQLException e) {e.printStackTrace();}
        } catch (ParseException e) {e.printStackTrace();}
    }

}