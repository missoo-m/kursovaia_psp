package com.ulya.client.orderTablePanel;

import com.ulya.client.ClientConnection;
import com.ulya.client.clientForms.UserMainForm;

import javax.swing.*;
import java.awt.*;

public class ViewOrdersForm {
    private final ClientConnection clientConnection;
    private final int userId;

    public ViewOrdersForm(ClientConnection clientConnection, int userId) {
        this.clientConnection = clientConnection;
        this.userId = userId;
    }

    public void show() {
        JFrame frame = new JFrame("Мои заказы");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        OrdersTablePanel ordersTablePanel = new OrdersTablePanel(clientConnection, userId);

        JButton backButton = createStyledButton("Назад");
        backButton.addActionListener(e -> {
            frame.dispose();
            new UserMainForm(clientConnection, userId).show();
        });

        mainPanel.add(ordersTablePanel, BorderLayout.CENTER);
        mainPanel.add(backButton, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE); // Белый текст для контраста
        button.setBackground(new Color(255, 192, 203)); // Нежно-розовый фон
        button.setOpaque(true); // Гарантируем отображение фона
        button.setFocusPainted(false); // Убираем выделение рамки при фокусе
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Добавление рамки для кнопки
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(248, 125, 185), 2), // Нежно-розовая граница
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Внутренние отступы
        ));

        // Эффекты при наведении
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(248, 125, 186)); // Насыщенно-розовый при наведении
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 192, 203)); // Нежно-розовый фон по умолчанию
            }
        });

        return button;
    }
}