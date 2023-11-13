import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 150);

            JPanel panel = new JPanel();
            frame.add(panel);

            panel.setLayout(new GridLayout(3, 2)); // 3 rows, 2 columns

            JLabel usernameLabel = new JLabel("Username:");
            JTextField usernameField = new JTextField();
            JLabel passwordLabel = new JLabel("Password:");
            JPasswordField passwordField = new JPasswordField();
            JButton loginButton = new JButton("Login");

            panel.add(usernameLabel);
            panel.add(usernameField);
            panel.add(passwordLabel);
            panel.add(passwordField);
            panel.add(new JLabel()); // Empty label for spacing
            panel.add(loginButton);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    char[] password = passwordField.getPassword();

                    // You can perform authentication logic here
                    // For demonstration purposes, let's just display the input values
                    JOptionPane.showMessageDialog(frame, "Username: " + username + "\nPassword: " + new String(password));
                }
            });

            frame.setVisible(true);
        });
    }
}
