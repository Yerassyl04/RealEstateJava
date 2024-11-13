import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private Image backgroundImage;

    public LoginPage(CardLayout cardLayout, JPanel container) {
        // Load the background image
        backgroundImage = new ImageIcon(getClass().getResource("/logregpage.png")).getImage();

        setLayout(null); // Set manual layout

        // Create the navigation panel
        NavigationPanel navigationPanel = new NavigationPanel(cardLayout, container);
        navigationPanel.setBounds(0, 0, 1940, 50);
        add(navigationPanel);

        // Create email label and field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(700, 300, 100, 30);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 20));
        emailField = new JTextField();
        emailField.setBounds(800, 300, 250, 40);
        emailField.setFont(new Font("Arial", Font.PLAIN, 18));

        // Create password label and field
        JLabel passwordLabel = new JLabel("Құпиясөз:");
        passwordLabel.setBounds(700, 350, 100, 30);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 20));
        passwordField = new JPasswordField();
        passwordField.setBounds(800, 350, 250, 40);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));

        // Create login button
        JButton loginButton = new JButton("Кіру");
        loginButton.setBounds(850, 400, 100, 40);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(Color.BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);

        // Create message label
        messageLabel = new JLabel("", JLabel.CENTER);
        messageLabel.setBounds(800, 450, 400, 30);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // Add components to panel
        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(messageLabel);

        // Handle login button click
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser(cardLayout, container);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw the background image
    }

    // Method to authenticate user with database
    private void authenticateUser(CardLayout cardLayout, JPanel container) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM \"User\" WHERE Email = ? AND Password = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String username = rs.getString("Sname") + " " + rs.getString("Lname");
                    int userID = rs.getInt("UserID");

                    UserDashboard dashboard = new UserDashboard(cardLayout, container, username, userID);
                    container.add(dashboard, "Dashboard");
                    cardLayout.show(container, "Dashboard");
                } else {
                    messageLabel.setText("e-mail немесе құпиясөз дұрыс емес ");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                messageLabel.setText("Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.");
            }
        } else {
            messageLabel.setText("Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.");
        }
    }
}


