import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class SellPropertyPage extends JPanel {
    private JTextField addressField, priceField, sizeField, numRoomsField, numBathsField, builtYearField;
    private JTextField deletePropertyIdField; // Поле для ввода ID недвижимости для удаления
    private JCheckBox parkingBox;
    private JComboBox<String> propertyTypeComboBox, cityComboBox;
    private JButton sellButton, deleteButton, backButton;

    public SellPropertyPage(CardLayout cardLayout, JPanel container, int userID) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Отступы между элементами

        // Поля ввода данных для продажи недвижимости
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Мекен-жай:"), gbc);

        addressField = new JTextField(20);
        gbc.gridy++;
        add(addressField, gbc);

        gbc.gridy++;
        add(new JLabel("Бағасы:"), gbc);

        priceField = new JTextField(20);
        gbc.gridy++;
        add(priceField, gbc);

        gbc.gridy++;
        add(new JLabel("Город:"), gbc);

        cityComboBox = new JComboBox<>(loadCitiesFromDatabase());
        gbc.gridy++;
        add(cityComboBox, gbc);

        gbc.gridy++;
        add(new JLabel("Мүлік типі:"), gbc);

        propertyTypeComboBox = new JComboBox<>(loadPropertyTypesFromDatabase());
        gbc.gridy++;
        add(propertyTypeComboBox, gbc);

        gbc.gridy++;
        add(new JLabel("Өлшемі (кв. м):"), gbc);

        sizeField = new JTextField(20);
        gbc.gridy++;
        add(sizeField, gbc);

        gbc.gridy++;
        add(new JLabel("Бөлме саны:"), gbc);

        numRoomsField = new JTextField(20);
        gbc.gridy++;
        add(numRoomsField, gbc);

        gbc.gridy++;
        add(new JLabel("Жуыну бөлме саны:"), gbc);

        numBathsField = new JTextField(20);
        gbc.gridy++;
        add(numBathsField, gbc);

        gbc.gridy++;
        add(new JLabel("Салынған жылы:"), gbc);

        builtYearField = new JTextField(20);
        gbc.gridy++;
        add(builtYearField, gbc);

        parkingBox = new JCheckBox("Парковка");
        gbc.gridy++;
        add(parkingBox, gbc);

        // Кнопка для добавления недвижимости в базу данных
        sellButton = new JButton("Сату");
        sellButton.setBackground(Color.BLUE);
        sellButton.setForeground(Color.WHITE);
        gbc.gridy++;
        add(sellButton, gbc);

        // Поле для ввода ID недвижимости для удаления
        gbc.gridy++;
        add(new JLabel("Мүлік ID бойынша өшіру:"), gbc);

        deletePropertyIdField = new JTextField(20);
        gbc.gridy++;
        add(deletePropertyIdField, gbc);

        // Кнопка для удаления недвижимости
        deleteButton = new JButton("Мүлікті өшіру");
        deleteButton.setBackground(Color.BLUE);
        deleteButton.setForeground(Color.WHITE);
        gbc.gridy++;
        add(deleteButton, gbc);

        // Кнопка "Назад"
        backButton = new JButton("Артқа");
        backButton.setBackground(Color.BLUE);
        backButton.setForeground(Color.WHITE);
        gbc.gridy++;
        add(backButton, gbc);

        // Действие при нажатии на кнопку "Назад"
        backButton.addActionListener(e -> {
            cardLayout.show(container, "Dashboard");
        });

        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPropertyToDatabase(userID);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePropertyFromDatabase();
            }
        });
    }

    private void addPropertyToDatabase(int userID) {
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO Property (CityID, PropertyTypeID, OwnerID, Price, Address, Status) VALUES (?, ?, ?, ?, ?, 'selling')";
                PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, cityComboBox.getSelectedIndex() + 1);
                stmt.setInt(2, propertyTypeComboBox.getSelectedIndex() + 1);
                stmt.setInt(3, userID);
                stmt.setBigDecimal(4, new BigDecimal(priceField.getText()));
                stmt.setString(5, addressField.getText());
                stmt.executeUpdate();

                // Получаем ID последней вставленной недвижимости
                int propertyId = 0;
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    propertyId = generatedKeys.getInt(1);
                }

                // Вставляем детали в таблицу PropertyDetails
                String detailsQuery = "INSERT INTO PropertyDetails (PropertyID, Size, P_description, Parking, Num_rooms, Num_bath, Built_year) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement detailsStmt = connection.prepareStatement(detailsQuery);
                detailsStmt.setInt(1, propertyId);
                detailsStmt.setBigDecimal(2, new BigDecimal(sizeField.getText()));
                detailsStmt.setString(3, "Описание недвижимости");
                detailsStmt.setBoolean(4, parkingBox.isSelected());
                detailsStmt.setInt(5, Integer.parseInt(numRoomsField.getText()));
                detailsStmt.setInt(6, Integer.parseInt(numBathsField.getText()));
                detailsStmt.setInt(7, Integer.parseInt(builtYearField.getText()));
                detailsStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Мүлік сәтті қосылды!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Мүлікті қосу кезіңде қателік туды");
            }
        }
    }

    private void deletePropertyFromDatabase() {
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                int propertyId = Integer.parseInt(deletePropertyIdField.getText().trim()); // Получаем ID недвижимости для удаления

                // Сначала удаляем связанные записи из PropertyDetails
                String detailsQuery = "DELETE FROM PropertyDetails WHERE PropertyID = ?";
                PreparedStatement detailsStmt = connection.prepareStatement(detailsQuery);
                detailsStmt.setInt(1, propertyId);
                detailsStmt.executeUpdate();

                // Теперь удаляем запись из Property
                String query = "DELETE FROM Property WHERE PropertyID = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, propertyId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Мүлік сәтті өшірілді");
                } else {
                    JOptionPane.showMessageDialog(this, "Мұндай ID бар мүлік табылмады.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Мүлікті өшіруде қателік туды");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Мүлік ID дұрыс еңгізіңіз");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Деректер қоырна қосыла алмадық.");
        }
    }

    private String[] loadCitiesFromDatabase() {
        ArrayList<String> cities = new ArrayList<>();
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT CityName FROM City"; // Измените название таблицы и поля, если они отличаются
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    cities.add(rs.getString("CityName"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Қалалар деректер қорынан жүктелмеді");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Деректер қоырна қосыла алмадық.");
        }
        return cities.toArray(new String[0]);
    }

    private String[] loadPropertyTypesFromDatabase() {
        ArrayList<String> propertyTypes = new ArrayList<>();
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT TypeName FROM PropertyType"; // Измените название таблицы, если необходимо
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    propertyTypes.add(rs.getString("TypeName"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Мүліктердің типін деректер қорынан жүктей алмадық");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Деректер қоырна қосыла алмадық.");
        }
        return propertyTypes.toArray(new String[0]);
    }
}



