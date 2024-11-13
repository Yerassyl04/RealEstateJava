import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavigationPanel extends JPanel {
    public NavigationPanel(CardLayout cardLayout, JPanel container) {
        setLayout(new FlowLayout(FlowLayout.CENTER));

        // Создание кнопок с измененным цветом
        JButton homeButton = createNavButton("Басты бет");
        JButton aboutButton = createNavButton("Біз жайлы");
        JButton loginButton = createNavButton("Кіру");
        JButton registerButton = createNavButton("Тіркелу");

        // Добавление кнопок на панель
        add(homeButton);
        add(aboutButton);
        add(loginButton);
        add(registerButton);

        homeButton.addActionListener(e -> cardLayout.show(container, "Home"));
        aboutButton.addActionListener(e -> cardLayout.show(container, "About"));
        loginButton.addActionListener(e -> cardLayout.show(container, "Login"));
        registerButton.addActionListener(e -> cardLayout.show(container, "Register"));
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));

        // Увеличиваем шрифт кнопки
        button.setFont(new Font("Arial", Font.BOLD, 16));

        return button;
    }
}



