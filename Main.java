import javax.swing.*;
import java.awt.*;
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Жылжымайтын мүлік");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1940, 1040);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Использование CardLayout для переключения между панелями
        CardLayout cardLayout = new CardLayout();
        JPanel container = new JPanel(cardLayout);
        container.setLayout(cardLayout);

        // Создание отдельных страниц
        HomePage homePage = new HomePage(cardLayout, container);
        AboutPage aboutPage = new AboutPage(cardLayout, container);
        LoginPage loginPage = new LoginPage(cardLayout, container);
        RegisterPage registerPage = new RegisterPage(cardLayout, container);

        // Добавление страниц в контейнер
        container.add(homePage, "Home");
        container.add(aboutPage, "About");
        container.add(loginPage, "Login");
        container.add(registerPage, "Register");


        // Отображаем главную страницу при старте
        cardLayout.show(container, "Home");

        frame.add(container);
        frame.setVisible(true);
    }
}


