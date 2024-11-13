import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterPage extends JPanel {
    private Image backgroundImage;
    public RegisterPage(CardLayout cardLayout, JPanel container) {
        backgroundImage = new ImageIcon(getClass().getResource("/logregpage.png")).getImage();

        setLayout(null); // Ручная компоновка

        // Создаем общий навигационный панель
        NavigationPanel navigationPanel = new NavigationPanel(cardLayout, container);
        navigationPanel.setBounds(0, 0, 1940, 50);
        add(navigationPanel);

        // Создание меток и полей для ввода с увеличенным расстоянием
        JLabel snameLabel = new JLabel("Аты:");
        snameLabel.setBounds(700, 100, 190, 30);
        snameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JTextField snameField = new JTextField();
        snameField.setBounds(850, 100, 200, 40);
        snameField.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel mnameLabel = new JLabel("Әкесінің аты:");
        mnameLabel.setBounds(700, 160, 190, 30);
        mnameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JTextField mnameField = new JTextField();
        mnameField.setBounds(850, 160, 200, 40);
        mnameField.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel lnameLabel = new JLabel("Тегі:");
        lnameLabel.setBounds(700, 220, 190, 30);
        lnameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JTextField lnameField = new JTextField();
        lnameField.setBounds(850, 220, 200, 40);
        lnameField.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel passportLabel = new JLabel("Паспорт ID:");
        passportLabel.setBounds(700, 280, 190, 30);
        passportLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JTextField passportField = new JTextField();
        passportField.setBounds(850, 280, 200, 40);
        passportField.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(700, 340, 190, 30);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JTextField emailField = new JTextField();
        emailField.setBounds(850, 340, 200, 40);
        emailField.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel passwordLabel = new JLabel("Құпиясөз:");
        passwordLabel.setBounds(700, 400, 190, 30);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(850, 400, 200, 40);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel cityLabel = new JLabel("Қала:");
        cityLabel.setBounds(700, 460, 190, 30);
        cityLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JTextField cityField = new JTextField();
        cityField.setBounds(850, 460, 200, 40);
        cityField.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel phoneLabel = new JLabel("Телефон:");
        phoneLabel.setBounds(700, 520, 190, 30);
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JTextField phoneField = new JTextField();
        phoneField.setBounds(850, 520, 200, 40);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel addressLabel = new JLabel("Мекен-жай:");
        addressLabel.setBounds(700, 580, 190, 30);
        addressLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JTextField addressField = new JTextField();
        addressField.setBounds(850, 580, 200, 40);
        addressField.setFont(new Font("Arial", Font.PLAIN, 18));

        JButton registerButton = new JButton("Тіркелу");
        registerButton.setBounds(850, 640, 200, 40);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(Color.BLUE);
        registerButton.setForeground(Color.WHITE);
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);

        // Добавление компонентов на панель
        add(snameLabel);
        add(snameField);
        add(mnameLabel);
        add(mnameField);
        add(lnameLabel);
        add(lnameField);
        add(passportLabel);
        add(passportField);
        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(cityLabel);
        add(cityField);
        add(phoneLabel);
        add(phoneField);
        add(addressLabel);
        add(addressField);
        add(registerButton);

        // Логика для кнопки регистрации
        registerButton.addActionListener(e -> {
            String sname = snameField.getText();
            String mname = mnameField.getText();
            String lname = lnameField.getText();
            String passportId = passportField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String city = cityField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();

            // Вызов метода для регистрации пользователя
            registerUser(sname, mname, lname, passportId, email, password, city, phone, address);
        });
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void registerUser(String sname, String mname, String lname, String passportId, String email, String password, String city, String phone, String address) {
        String sql = "INSERT INTO \"User\" (Sname, Mname, Lname, Passport_id, Email, Password, City, Phone, Address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка значений в подготовленный запрос
            pstmt.setString(1, sname);
            pstmt.setString(2, mname);
            pstmt.setString(3, lname);
            pstmt.setString(4, passportId);
            pstmt.setString(5, email);
            pstmt.setString(6, password);
            pstmt.setString(7, city);
            pstmt.setString(8, phone);
            pstmt.setString(9, address);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Қолданушы сәтті тіркелді!");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Тіркелу кезіңде қателік орын алды: " + ex.getMessage());
        }
    }
}

