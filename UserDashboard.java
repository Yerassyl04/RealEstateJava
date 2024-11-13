import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDashboard extends JPanel {
    private String username;
    private int userID;
    private Image backgroundImage;  // Variable to store the background image

    public UserDashboard(CardLayout cardLayout, JPanel container, String username, int userID) {
        this.username = username;
        this.userID = userID;
        setLayout(null);

        // Load the image as background
        ImageIcon originalIcon = new ImageIcon("userdashboardd.png");
        this.backgroundImage = originalIcon.getImage();  // Store the image

        // Set the panel to be non-opaque so background is drawn
        setOpaque(false);

        // Navigation Panel
        NavigationPanel navigationPanel = new NavigationPanel(cardLayout, container);
        navigationPanel.setBounds(0, 0, 1940, 50);
        add(navigationPanel);

        JLabel welcomeLabel = new JLabel("Қайта оралуыңызбен, " + username + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setBounds(650, 100, 700, 30);
        add(welcomeLabel);

        // Title for System Statistics
        JLabel statsTitle = new JLabel("Жүйе статистикасы", JLabel.CENTER);
        statsTitle.setFont(new Font("Arial", Font.BOLD, 20));
        statsTitle.setBounds(30, 650, 300, 30);
        add(statsTitle);

        // Stats Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(3, 1, 10, 10));
        statsPanel.setBounds(50, 700, 300, 150);  // Adjusted position
        add(statsPanel);

        JLabel usersCountLabel = new JLabel();
        JLabel housesSoldLabel = new JLabel();
        JLabel avgPriceLabel = new JLabel();

        statsPanel.add(usersCountLabel);
        statsPanel.add(housesSoldLabel);
        statsPanel.add(avgPriceLabel);

        loadData(usersCountLabel, housesSoldLabel, avgPriceLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBounds(50, 150, 200, 300);
        buttonPanel.setOpaque(false);
        add(buttonPanel);

        JButton sellButton = createStyledButton("Мүлікті сату");
        JButton buyButton = createStyledButton("Мүлікті сатып-алу");
        JButton favoriteButton = createStyledButton("Таңдалым");
        JButton cartButton = createStyledButton("Себет");

        buttonPanel.add(sellButton);
        buttonPanel.add(buyButton);
        buttonPanel.add(favoriteButton);
        buttonPanel.add(cartButton);

        sellButton.addActionListener(e -> {
            SellPropertyPage sellPage = new SellPropertyPage(cardLayout, container, userID);
            container.add(sellPage, "SellPropertyPage");
            cardLayout.show(container, "SellPropertyPage");
        });

        buyButton.addActionListener(e -> {
            BuyPropertyPage buyPage = new BuyPropertyPage(cardLayout, container, userID);
            container.add(buyPage, "BuyPropertyPage");
            cardLayout.show(container, "BuyPropertyPage");
        });

        favoriteButton.addActionListener(e -> {
            FavoritesPage favoritesPage = new FavoritesPage(cardLayout, container, userID);
            container.add(favoritesPage, "FavoritesPage");
            cardLayout.show(container, "FavoritesPage");
        });

        cartButton.addActionListener(e -> {
            CartPage cartPage = new CartPage(cardLayout, container, userID);
            container.add(cartPage, "CartPage");
            cardLayout.show(container, "CartPage");
        });
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

    private void loadData(JLabel usersCountLabel, JLabel housesSoldLabel, JLabel avgPriceLabel) {
        Connection connection = DBConnection.getConnection();
        try {
            String usersCountQuery = "SELECT COUNT(*) AS totalUsers FROM \"User\"";
            PreparedStatement usersCountStmt = connection.prepareStatement(usersCountQuery);
            ResultSet usersCountResult = usersCountStmt.executeQuery();
            if (usersCountResult.next()) {
                int totalUsers = usersCountResult.getInt("totalUsers");
                usersCountLabel.setText("Барлық тіркелген қолданушылар: " + totalUsers);
            }

            String housesSoldQuery = "SELECT COUNT(*) AS totalSold FROM Property WHERE Status = 'sold'";
            PreparedStatement housesSoldStmt = connection.prepareStatement(housesSoldQuery);
            ResultSet housesSoldResult = housesSoldStmt.executeQuery();
            if (housesSoldResult.next()) {
                int totalSold = housesSoldResult.getInt("totalSold");
                housesSoldLabel.setText("Сатылған үйлер саны: " + totalSold);
            }

            String avgPriceQuery = "SELECT AVG(Price) AS avgPrice FROM Property WHERE Status = 'sold'";
            PreparedStatement avgPriceStmt = connection.prepareStatement(avgPriceQuery);
            ResultSet avgPriceResult = avgPriceStmt.executeQuery();
            if (avgPriceResult.next()) {
                double avgPrice = avgPriceResult.getDouble("avgPrice");
                avgPriceLabel.setText("Орташа үй бағасы: " + String.format("%.2f", avgPrice));
            }

            usersCountResult.close();
            usersCountStmt.close();
            housesSoldResult.close();
            housesSoldStmt.close();
            avgPriceResult.close();
            avgPriceStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}










