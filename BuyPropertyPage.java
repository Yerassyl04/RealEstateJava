import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuyPropertyPage extends JPanel {
    private JComboBox<String> cityFilter;
    private JComboBox<String> propertyTypeFilter;
    private JTextField minPriceField, maxPriceField;
    private JComboBox<String> sortByPrice;
    private JPanel propertyListPanel;
    private Connection connection;
    private Property selectedProperty; // To track the selected property
    private int userID; // Assuming you have user ID available

    public BuyPropertyPage(CardLayout cardLayout, JPanel container, int userID) {
        this.userID = userID; // Initialize userID
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Set preferred size and background color
        setPreferredSize(new Dimension(500, 700));
        setBackground(Color.WHITE);

        // Header
        JLabel label = new JLabel("Жылжымайтын мүлік сатып алу");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; // Center in the grid
        gbc.gridy = 0; // Top of the grid
        gbc.gridwidth = 2; // Span 2 columns
        add(label, gbc);

        // Initialize database connection
        connection = DBConnection.getConnection();
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.", "Қате", JOptionPane.ERROR_MESSAGE);
            return; // Exit constructor if connection failed
        }

        // Filters
        gbc.gridwidth = 1; // Reset to 1 column
        gbc.gridx = 0; // Reset to column 0
        gbc.gridy = 1;

        JLabel cityLabel = new JLabel("Қала:");
        cityLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(cityLabel, gbc);

        cityFilter = new JComboBox<>(loadCitiesFromDatabase());
        cityFilter.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1; // Move to column 1
        add(cityFilter, gbc);

        // Property type filter
        JLabel propertyTypeLabel = new JLabel("Мүлік түрі:");
        propertyTypeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0; // Reset to column 0
        gbc.gridy = 2;
        add(propertyTypeLabel, gbc);

        propertyTypeFilter = new JComboBox<>(loadPropertyTypesFromDatabase());
        propertyTypeFilter.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1; // Move to column 1
        add(propertyTypeFilter, gbc);

        // Price filters
        JLabel priceLabel = new JLabel("Бағасы:");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0; // Reset to column 0
        gbc.gridy = 3;
        add(priceLabel, gbc);

        minPriceField = new JTextField(10);
        minPriceField.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1; // Move to column 1
        add(minPriceField, gbc);

        JLabel toLabel = new JLabel("дейін");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0; // Reset to column 0
        gbc.gridy = 4;
        add(toLabel, gbc);

        maxPriceField = new JTextField(10);
        maxPriceField.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1; // Move to column 1
        add(maxPriceField, gbc);

        // Sort options
        JLabel sortLabel = new JLabel("Бағасы бойынша іріктеу:");
        sortLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0; // Reset to column 0
        gbc.gridy = 5;
        add(sortLabel, gbc);

        sortByPrice = new JComboBox<>(new String[]{"Өсім", "Кему"});
        sortByPrice.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1; // Move to column 1
        add(sortByPrice, gbc);

        // Property list panel
        propertyListPanel = new JPanel();
        propertyListPanel.setLayout(new BoxLayout(propertyListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(propertyListPanel);
        scrollPane.setPreferredSize(new Dimension(400, 200)); // Set a preferred size for the scroll pane
        gbc.gridx = 0; // Reset to column 0
        gbc.gridy = 6;
        gbc.gridwidth = 2; // Span 2 columns
        add(scrollPane, gbc);

        // Apply filters button
        JButton applyFiltersButton = createStyledButton("Шектеуді орындау");
        applyFiltersButton.setBackground(Color.BLUE);
        applyFiltersButton.setForeground(Color.WHITE);

        gbc.gridwidth = 1; // Reset to 1 column
        gbc.gridx = 0; // Reset to column 0
        gbc.gridy = 7;
        add(applyFiltersButton, gbc);

        applyFiltersButton.addActionListener(e -> applyFiltersAndSort());

        // Back button
        JButton backButton = createStyledButton("Артқа");
        gbc.gridx = 1; // Move to column 1
        add(backButton, gbc);

        backButton.addActionListener(e -> cardLayout.show(container, "Dashboard"));

        // Add to cart button
        JButton addToCartButton = createStyledButton("Себетке салу");
        addToCartButton.setBackground(Color.BLUE);
        addToCartButton.setForeground(Color.WHITE);
        gbc.gridx = 0; // Reset to column 0
        gbc.gridy = 8; // Move down one row
        add(addToCartButton, gbc);

        addToCartButton.addActionListener(e -> addToCart());

        // Add to wishlist button
        JButton addToWishlistButton = createStyledButton("Таңдаулыға салу");
        addToWishlistButton.setBackground(Color.BLUE);
        addToWishlistButton.setForeground(Color.WHITE);
        gbc.gridx = 1; // Move to column 1
        add(addToWishlistButton, gbc);

        addToWishlistButton.addActionListener(e -> addToWishlist());

        // Load and display all properties by default
        displayProperties(loadPropertiesFromDatabase(null, null, 0, Double.MAX_VALUE, "ASC"));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        return button;
    }

    private String[] loadCitiesFromDatabase() {
        ArrayList<String> cities = new ArrayList<>();
        cities.add("Все");
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.", "Қате", JOptionPane.ERROR_MESSAGE);
            return cities.toArray(new String[0]);
        }

        try {
            String query = "SELECT CityName FROM City";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                cities.add(rs.getString("CityName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities.toArray(new String[0]);
    }

    private String[] loadPropertyTypesFromDatabase() {
        ArrayList<String> propertyTypes = new ArrayList<>();
        propertyTypes.add("Все");
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.", "Қате", JOptionPane.ERROR_MESSAGE);
            return propertyTypes.toArray(new String[0]);
        }

        try {
            String query = "SELECT TypeName FROM PropertyType";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                propertyTypes.add(rs.getString("TypeName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return propertyTypes.toArray(new String[0]);
    }

    private List<Property> loadPropertiesFromDatabase(String city, String type, double minPrice, double maxPrice, String sortOrder) {
        List<Property> properties = new ArrayList<>();
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.", "Қате", JOptionPane.ERROR_MESSAGE);
            return properties;
        }

        try {
            StringBuilder query = new StringBuilder("SELECT p.PropertyID, c.CityName, pt.TypeName, p.Price, p.Address, p.OwnerID " +
                    "FROM Property p " +
                    "JOIN City c ON p.CityID = c.CityID " +
                    "JOIN PropertyType pt ON p.PropertyTypeID = pt.PropertyTypeID " +
                    "WHERE 1=1");
            if (!"Все".equals(city) && city != null) {
                query.append(" AND c.CityName = '").append(city).append("'");
            }
            if (!"Все".equals(type) && type != null) {
                query.append(" AND pt.TypeName = '").append(type).append("'");
            }
            if (minPrice >= 0) {
                query.append(" AND p.Price >= ").append(minPrice);
            }
            if (maxPrice < Double.MAX_VALUE) {
                query.append(" AND p.Price <= ").append(maxPrice);
            }
            query.append(" ORDER BY p.Price ").append(sortOrder);

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                int id = rs.getInt("PropertyID");
                String cityName = rs.getString("CityName"); // Get the city name from the City table
                String typeName = rs.getString("TypeName"); // Get the property type from the PropertyType table
                double price = rs.getDouble("Price");
                String address = rs.getString("Address");
                int ownerID = rs.getInt("OwnerID");
                properties.add(new Property(id, cityName, typeName, price, address, ownerID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private void displayProperties(List<Property> properties) {
        propertyListPanel.removeAll();
        for (Property property : properties) {
            JButton propertyButton = new JButton(property.toString());
            propertyButton.setFont(new Font("Arial", Font.PLAIN, 16));
            propertyButton.addActionListener(e -> selectProperty(property));
            propertyListPanel.add(propertyButton);
        }
        propertyListPanel.revalidate();
        propertyListPanel.repaint();
    }

    private void applyFiltersAndSort() {
        String selectedCity = (String) cityFilter.getSelectedItem();
        String selectedType = (String) propertyTypeFilter.getSelectedItem();
        double minPrice = minPriceField.getText().isEmpty() ? 0 : Double.parseDouble(minPriceField.getText());
        double maxPrice = maxPriceField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceField.getText());
        String selectedSortOrder = sortByPrice.getSelectedItem().equals("По возрастанию") ? "ASC" : "DESC";

        List<Property> filteredProperties = loadPropertiesFromDatabase(selectedCity, selectedType, minPrice, maxPrice, selectedSortOrder);
        displayProperties(filteredProperties);
    }

    private void selectProperty(Property property) {
        selectedProperty = property; // Set the selected property
        // You can add any other actions to take when a property is selected
    }

    private void addToCart() {
        if (selectedProperty == null) {
            JOptionPane.showMessageDialog(this, "Өтініш, себетке қосу үшін таңдау жасаңыз", "Қате", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Insert the selected property into the Cart table instead of SalesRecord
            String query = "INSERT INTO Cart (UserID, PropertyID) VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userID); // UserID for the current user (buyer)
            pstmt.setInt(2, selectedProperty.getId()); // PropertyID for the selected property

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected); // Debugging output
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Мүлік себетке қосылды: " + selectedProperty.toString(), "Сәтті", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Мүлік себетке қосылмады", "Қате", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error for debugging
            JOptionPane.showMessageDialog(this, "Себетке қосу кезінде қателік туындады " + e.getMessage(), "Қате", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void addToWishlist() {
        if (selectedProperty == null) {
            JOptionPane.showMessageDialog(this, "Таңдалымға қосу үшін мүлікті таңдаңыз", "Қате", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // SQL logic to add selectedProperty to wishlist
        try {
            String query = "INSERT INTO Wishlist (UserID, PropertyID) VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userID);
            pstmt.setInt(2, selectedProperty.getId()); // Assuming you have a getId() method in Property class

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Мүлік таңдалымға қосылды: " + selectedProperty.toString(), "Сәтті", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Мүлік таңдалымға қосылмады", "Қате", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Мүлікті таңдалымға қосу кезінде қателік орын алды: " + e.getMessage(), "Қате", JOptionPane.ERROR_MESSAGE);
        }
    }

}







