import java.util.Scanner;

public class Login {
    public static void main(String[] args) {
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
                    authenticateUser(scanner);
                    break;
                case 2:
                    createAccount(scanner);
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

    private static void authenticateUser(Scanner scanner) {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        
        // Perform authentication logic here (e.g., check against a database)
        
        // Print the username and password to verify
        System.out.println("Authentication successful!");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
    }

    private static void createAccount(Scanner scanner) {
        System.out.print("Enter a new username: ");
        String newUsername = scanner.nextLine();
        
        System.out.print("Enter a new password: ");
        String newPassword = scanner.nextLine();
        
        // Perform account creation logic here (e.g., store in a database)
        
        // Print a message to indicate success
        System.out.println("Account created successfully!");
    }
}
