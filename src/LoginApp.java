import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a JFrame with a fixed size
            JFrame frame = new JFrame("Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 750); // Set a fixed size (width x height)
            frame.setResizable(false); // Make the window non-resizable

            JPanel mainPanel = new JPanel();
            frame.add(mainPanel, BorderLayout.CENTER); // Center the main panel

            // Use a GridLayout for the login components
            JPanel loginPanel = new JPanel();
            loginPanel.setLayout(new GridLayout(2, 2)); // 2 rows, 2 columns
            mainPanel.add(loginPanel, BorderLayout.CENTER); // Place loginPanel in the center

            JLabel usernameLabel = new JLabel("Username:");
            JTextField usernameField = new JTextField(10);
            JLabel passwordLabel = new JLabel("Password:");
            JPasswordField passwordField = new JPasswordField(10);

            loginPanel.add(usernameLabel);
            loginPanel.add(usernameField);
            loginPanel.add(passwordLabel);
            loginPanel.add(passwordField);

            JPanel buttonPanel = new JPanel();
            mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Place buttonPanel at the bottom

            JButton loginButton = new JButton("Login");
            JButton createAccountButton = new JButton("Create Account");
            JButton managerInterfaceButton = new JButton("Manager Interface");

            buttonPanel.add(loginButton);
            buttonPanel.add(createAccountButton);
            buttonPanel.add(managerInterfaceButton);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    char[] password = passwordField.getPassword();

                    // You can perform authentication logic here
                    // For demonstration purposes, let's just display the input values
                    JOptionPane.showMessageDialog(frame, "Username: " + username + "\nPassword: " + new String(password));

                    // check the SQL shit
                }
            });

            createAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle create account button click
                    // need our SQL shit here
                    JFrame createAccountFrame = new JFrame("Create Account");
                    createAccountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only the new window
                    createAccountFrame.setSize(600, 200);
                    createAccountFrame.setResizable(false);

                    

                    // Add your account creation components to the new frame
                    JPanel createAccountPanel = new JPanel();
                    //createAccountPanel.setLayout(new BoxLayout(createAccountPanel, BoxLayout.Y_AXIS)); // Stack components vertically
                    createAccountFrame.add(createAccountPanel);

                    JLabel usernameLabel = new JLabel("Username:");
                    JLabel passwordLabel = new JLabel("Password:");

                    createAccountPanel.add(usernameLabel);
                    createAccountPanel.add(usernameField);
                    createAccountPanel.add(passwordLabel);
                    createAccountPanel.add(passwordField);

                    // Add "Create Account" button
                    JButton createAccountButtonInternal = new JButton("Create Account");
                    createAccountButtonInternal.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Handle "Create Account" button click
                            // Perform account creation (SQL or other logic) here
                            // You can access usernameField.getText() and passwordField.getPassword() to get user input
                            // ...

                            // Close the "Create Account" window
                            //do that SQL shit here
                            createAccountFrame.dispose();
                        }
                    });

                    createAccountPanel.add(createAccountButtonInternal);

                    createAccountFrame.setVisible(true);
                }
            });

            managerInterfaceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle manager interface button click
                    // need ourr SQL shit here
                }
            });

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
