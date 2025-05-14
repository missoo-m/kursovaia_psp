package com.ulya.client.clientForms;

import com.ulya.client.ClientConnection;
import com.ulya.client.clientForms.actions.ManageUsersForm;
import com.ulya.client.clientForms.login.LoginForm;
import com.ulya.client.orderTablePanel.OrdersPieChartForm;
import com.ulya.client.orderTablePanel.ViewAllOrdersForm;
import com.ulya.client.productForms.ProductCrudForm;
import com.ulya.client.clientForms.login.AdminScheduleForm;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AdminMainForm extends BaseMainForm {

    private final int userId;

    public AdminMainForm(ClientConnection clientConnection, int userId) {
        super(clientConnection, "ADMIN");
        this.userId = userId;
    }

    public AdminMainForm(ClientConnection clientConnection) {
        super(clientConnection, "ADMIN");
        this.userId = -1;
    }

    @Override
    protected void addCustomComponents(JPanel panel, GridBagConstraints gbc, JFrame frame) {
        // Заголовок формы
        JLabel titleLabel = new JLabel("Панель управления администратора", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 105, 180)); // Нежно-розовый цвет для текста

        // Настройка панели заголовка
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Создание кнопок
        JButton viewProductsButton = createStyledButton("Управление продуктами");
        JButton manageUsersButton = createStyledButton("Управление пользователями");
        JButton editseans = createStyledButton("Формирование сенсов");
        JButton viewOrdersButton = createStyledButton("Просмотр всех заказов");
        JButton viewOrdersChartButton = createStyledButton("Аналитика заказов");
        JButton logoutButton = createStyledButton("Выйти");

        // Добавление кнопок с промежутками
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        buttonPanel.add(viewProductsButton);
        buttonPanel.add(manageUsersButton);
        buttonPanel.add(editseans);
        buttonPanel.add(viewOrdersButton);
        buttonPanel.add(viewOrdersChartButton);
        buttonPanel.add(logoutButton);

        // Основная панель
        panel.setLayout(new BorderLayout());
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        viewOrdersButton.addActionListener(e -> {
            frame.dispose();
            new ViewAllOrdersForm(clientConnection).show();
        });

        viewOrdersChartButton.addActionListener(e -> {
            Map<String, Integer> ordersData = fetchOrdersAnalytics();
            new OrdersPieChartForm(ordersData).setVisible(true);
        });

        // Обработчики кнопок
        viewProductsButton.addActionListener(e -> {
            frame.dispose();
            new ProductCrudForm(clientConnection, role, userId).show();
        });

        manageUsersButton.addActionListener(e -> {
            frame.dispose();
            new ManageUsersForm(clientConnection).show();
        });

        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginForm(clientConnection).show();
        });
        editseans.addActionListener(e -> {
            frame.dispose();
            new AdminScheduleForm(clientConnection, role, userId).show();
        });
    }

    private Map<String, Integer> fetchOrdersAnalytics() {
        Map<String, Integer> ordersData = new HashMap<>();
        try {
            clientConnection.send("GET_ORDER_ANALYTICS");
            int categoryCount = Integer.parseInt(clientConnection.receive());
            for (int i = 0; i < categoryCount; i++) {
                String[] data = clientConnection.receive().split(",");
                String category = data[0];
                int count = Integer.parseInt(data[1]);
                ordersData.put(category, count);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки аналитики заказов!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return ordersData;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(255, 182, 193)); // Нежно-розовый цвет
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 105, 180), 2), // Нежно-розовая граница
                BorderFactory.createEmptyBorder(15, 40, 15, 40) // Отступы для ширины кнопки
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Эффекты при наведении
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 105, 180)); // Насыщенно-розовый при наведении
                button.setBorder(BorderFactory.createLineBorder(new Color(255, 20, 147), 2)); // Более яркая граница
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 182, 193)); // Нежно-розовый по умолчанию
                button.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 2)); // Оригинальная граница
            }
        });

        return button;
    }

    public void show() {
        JFrame frame = new JFrame("Администратор");
        showBase(frame);
    }
}