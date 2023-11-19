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


       while(doesAccountExist(connection, tax_id) == false){
           System.out.println(
           "Account does not exist. Please create an account by typing Deposit and a balance greater than $1000");
           System.out.println("Ex: To Deposit $1000, type: Deposit 1000");

           String userInput = scanner.nextLine();
        
            // Split the user input into words
            String[] inputWords = userInput.split(" ");

           if (inputWords.length == 2 && inputWords[0].equalsIgnoreCase("Deposit")) {
            try {
                double amount = Double.parseDouble(inputWords[1]);
                if (amount >= 1000) {
                    // Deposit logic here
                    System.out.println("Successfully deposited $" + amount + ".");
                    // You can add code to perform the deposit operation
                    break; // Exit the loop after successful deposit
                } else {
                    System.out.println("Deposit amount must be $1000 or more. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric amount.");
            }
        } else {
            System.out.println("Invalid input format. Please enter a valid deposit command.");
        }
       }


      


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
                   deposit(scanner);
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


   private static void deposit(Scanner scanner) {
    System.out.println("Deposit option selected.");
    while (true) {
        System.out.println("Please enter the amount you would like to deposit as a decimal number: ");
        if (scanner.hasNextDouble()) {
            double amount = scanner.nextDouble();
            // Process the valid double input (amount) here
            System.out.println("You entered: " + amount);
            break; // Exit the loop since valid input was provided
        } else {
            System.out.println("Invalid input. Please enter a valid decimal number.");
            scanner.next(); // Consume the invalid input to avoid an infinite loop
        }
    }
    // Continue with your deposit logic here using the valid amount.
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




public static boolean doesAccountExist(OracleConnection connection, int tax_id) {
   String selectQuery = "SELECT * FROM Account_Has WHERE tax_id = " + Integer.toString(tax_id);
   try (Statement statement = connection.createStatement()) {


  
       ResultSet resultSet = statement.executeQuery(
           selectQuery
       );
       if(resultSet.next()){
           return true;
       }
       else{
           return false;
       }
}
catch (SQLException e) {
       e.printStackTrace();
       // Handle any exceptions that may occur during database access
   }


   return(false);
}


}



