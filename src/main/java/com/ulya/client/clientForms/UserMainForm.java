package com.ulya.client.clientForms;

import com.ulya.client.ClientConnection;
import com.ulya.client.cartForms.CartCrudForm;
import com.ulya.client.clientForms.login.LoginForm;
import com.ulya.client.orderTablePanel.ViewOrdersForm;
import com.ulya.client.productForms.ProductCrudForm;
import com.ulya.client.productForms.old.ViewProductsForm;

import javax.swing.*;
import java.awt.*;

public class UserMainForm extends BaseMainForm {
    private final int userId;

    public UserMainForm(ClientConnection clientConnection, int userId) {
        super(clientConnection, "USER");
        this.userId = userId;
    }

    @Override
    protected void addCustomComponents(JPanel panel, GridBagConstraints gbc, JFrame frame) {
        // Заголовок формы
        JLabel titleLabel = new JLabel("Панель пользователя", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 105, 180));

        // Настройка панели заголовка
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Кнопки
        JButton viewProductsButton = createStyledButton("Просмотреть товары");
        JButton manageCartButton = createStyledButton("Управление корзиной");
        JButton viewOrdersButton = createStyledButton("Мои заказы");
        JButton logoutButton = createStyledButton("Выйти");

        // Добавление кнопок с промежутками
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10)); // Уменьшил количество строк до 4
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.add(viewProductsButton);
        buttonPanel.add(manageCartButton);
        buttonPanel.add(viewOrdersButton);
        buttonPanel.add(logoutButton);

        // Основная панель
        panel.setLayout(new BorderLayout());
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        // Обработчики кнопок
        viewProductsButton.addActionListener(e -> {
            frame.dispose();
            new ViewProductsForm(clientConnection, role, userId).show();
        });

        manageCartButton.addActionListener(e -> {
            frame.dispose();
            new CartCrudForm(clientConnection, userId).show();
        });

        viewOrdersButton.addActionListener(e -> {
            frame.dispose();
            new ViewOrdersForm(clientConnection, userId).show();
        });

        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginForm(clientConnection).show();
        });
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(255, 182, 193));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Устанавливаем границу для визуального эффекта
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 105, 180), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 105, 180)); // Насыщенно-розовый при наведении
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 182, 193)); // Нежно-розовый фон по умолчанию
            }
        });

        return button;
    }

    public void show() {
        JFrame frame = new JFrame("Пользователь");
        showBase(frame);
    }
}