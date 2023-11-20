import java.util.Scanner;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;


public class DataInput {

    public static void main3(OracleConnection connection) {

        // Scanner scanner = new Scanner(System.in);
        
        // System.out.println("Inserting data into data tables...");
        String filePath = "/Users/kaimaeda/csildown/cs174A_FinalProject/F23_Sample.data";
        try {
            Scanner scanner = new Scanner(new File(filePath));
            String[] markers = {"ADMINISTRATOR", "CUSTOMERS", "MARKET", "OWNERSHIP", "ACTORS"};
            String[] floats = {"balance"};
            String[] integers = {"tax_id", "num_share", "acc_id"};
            Map<String, String[]> markers_mapping = Map.of(
                    "ADMINISTRATOR", new String[]{"Manager"},
                    "CUSTOMERS", new String[]{"Customer"},
                    "OWNERSHIP", new String[]{"Stock_Account"},
                    "MARKET", new String[]{"Account_has", "Market_Account"},
                    "ACTORS", new String[]{"Stock_Actor", "Movie", "Contract"}
            );
            for(int i = 0; i < markers.length; i++) {
                while(scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if(line.trim().contains(markers[i])) {
                        break;
                    }
                }
                scanner.nextLine();
                scanner.nextLine();
                String line = scanner.nextLine();
                // System.out.println("parameters: " +  line);
                String[] parameters = line.split(",");
                while(!line.trim().isEmpty()) {
                    line = scanner.nextLine();
                    String[] values = line.split(",");
                    if(line.trim().isEmpty()) break;
                    String[] tableName = markers_mapping.get(markers[i]);
                    for(int j = 0; j < tableName.length; j++) {
                        String[] correctParameters;
                        String[] correctValues;
                        int[] indices = {0};
                        if(i == 4)  {
                            if(j == 0) {
                                indices = new int[]{0,1,2,3};
                            } else if(j == 1){
                                indices = new int[]{4,6};
                            } else if(j == 2) {
                                indices = new int[]{0,4,5,6,7};
                            }
                            correctParameters = reorder(parameters, indices);
                            correctValues = reorder(values, indices);
                        }
                        else if(i == 2 && j == 0) {
                            indices = new int[]{0,1};
                            correctParameters = reorder(parameters, indices);
                            correctValues = reorder(values, indices);
                        } else if(i==3) {
                            int n = parameters.length;
                            correctParameters = Arrays.copyOf(parameters, n + 1);
                            correctParameters[n] =  "acc_id";
                            correctValues = Arrays.copyOf(values, n + 1);
                            int tax_id = Integer.parseInt(correctValues[0]);
                            // System.out.println("acc_id: " + TraderInterface.getAccountId(tax_id,connection) + " tax_id: " + tax_id);
                            // import packageName.TraderInterface;
                            correctValues[n] = Integer.toString(TraderInterface.getAccountId(tax_id,connection));
                            
                        } else {
                            correctParameters = parameters;
                            correctValues = values;
                        }
                        // System.out.println("i and j: " + i + " " + j + " CV: " + correctValues[0] + " "  + correctValues[2] +  " " + correctValues[3] +  " CP: " + correctParameters[0]);
                        String selectQuery = buildSelectQuery(tableName[j], correctParameters,  correctValues, integers,floats);
                        // System.out.println("selectQuery: " + selectQuery);
                        try (Statement statement = connection.createStatement()) {
                            ResultSet resultSet = statement.executeQuery(selectQuery);
                            if (!resultSet.next()) {
                                String insertQuery = buildInsertQuery(tableName[j], correctParameters,  correctValues, integers,floats);
                                // System.out.println("InsertQuery: " + insertQuery);
                                try(Statement statement2 = connection.createStatement()) {
                                    int rowsAffected = statement2.executeUpdate(insertQuery);
                                    if (rowsAffected > 0) {
                                        System.out.println("Insertion successful!");
                                    } else {
                                        System.out.println("Insertion failed.");
                                    }
                                }
                            } 
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }           
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            e.printStackTrace();
        }
    }
  
    public static String buildInsertQuery(String tableName, String[] parameters, String[] values, String[] integers, String[] floats) {
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO ");
        queryBuilder.append(tableName).append(" (");

        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                queryBuilder.append(", ");
            }
            queryBuilder.append(parameters[i]);
        }
        queryBuilder.append(") VALUES (");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                queryBuilder.append(", ");
            }
            if(Arrays.asList(integers).contains(parameters[i])) {
                queryBuilder.append("'").append(Integer.parseInt(values[i])).append("'");
            } else if(Arrays.asList(floats).contains(parameters[i])) {
                queryBuilder.append("'").append(Float.parseFloat(values[i])).append("'");
            }
            else {
                queryBuilder.append("'").append(values[i]).append("'");
            }
        }
        queryBuilder.append(")");

        return queryBuilder.toString();
    }
    public static String buildSelectQuery(String tableName, String[] parameters, String[] values, String[] integers, String[] floats) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ");
        queryBuilder.append(tableName);

        queryBuilder.append(" WHERE ");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append(parameters[i]).append(" = ");
            if(Arrays.asList(integers).contains(parameters[i])) {
                queryBuilder.append("'").append(Integer.parseInt(values[i])).append("'");
            } else if(Arrays.asList(floats).contains(parameters[i])) {
                queryBuilder.append("'").append(Float.parseFloat(values[i])).append("'");
            }
            else {
                queryBuilder.append("'").append(values[i]).append("'");
            }
        }

        return queryBuilder.toString();
    }
    public static String[] reorder(String[] originalArray, int[] indices) {
        String[] newArray = new String[indices.length];
        for (int i = 0; i < indices.length; i++) {
            newArray[i] = originalArray[indices[i]];

        }
        return newArray;
    }
}



