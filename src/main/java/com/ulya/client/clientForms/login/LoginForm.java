package com.ulya.client.clientForms.login;

import com.ulya.client.ClientConnection;
import com.ulya.client.clientForms.AdminMainForm;
import com.ulya.client.clientForms.UserMainForm;

import javax.swing.*;
import java.awt.*;

public class LoginForm {
    private final ClientConnection clientConnection;

    public LoginForm(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void show() {
        JFrame frame = new JFrame("Вход");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = createStyledLabel("Имя пользователя:");
        JTextField usernameField = createStyledTextField();

        JLabel passwordLabel = createStyledLabel("Пароль:");
        JPasswordField passwordField = createStyledPasswordField();

        JButton loginButton = createStyledButton("Войти");
        JButton registerButton = createStyledButton("Регистрация");
        JButton logoutButton = createStyledButton("Выйти");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        gbc.gridy = 3;
        panel.add(registerButton, gbc);
        gbc.gridy = 4;
        panel.add(logoutButton, gbc);

        loginButton.addActionListener(e -> handleLogin(frame, usernameField, passwordField));
        registerButton.addActionListener(e -> {
            frame.dispose();
            new RegisterForm(clientConnection).show();
        });
        logoutButton.addActionListener(e -> {
            frame.dispose();
            JOptionPane.showMessageDialog(null, "Вы вышли из системы!");
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private void handleLogin(JFrame frame, JTextField usernameField, JPasswordField passwordField) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showMessage(frame, "Все поля должны быть заполнены!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        clientConnection.send("LOGIN");
        clientConnection.send(username);
        clientConnection.send(password);

        String response = clientConnection.receive();
        if ("SUCCESS".equals(response)) {
            String userId = clientConnection.receive();
            String role = clientConnection.receive();
            showMessage(frame, "Успешный вход! Ваша роль: " + role, JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();

            if ("ADMIN".equals(role)) {
                new AdminMainForm(clientConnection, Integer.parseInt(userId)).show();
            } else {
                new UserMainForm(clientConnection, Integer.parseInt(userId)).show();
            }
        } else if ("BLOCKED".equals(response)) {
            showMessage(frame, "Аккаунт заблокирован", JOptionPane.ERROR_MESSAGE);
        } else {
            showMessage(frame, "Неверное имя пользователя или пароль!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(JFrame frame, String message, int messageType) {
        JOptionPane.showMessageDialog(frame, message, "Информация", messageType);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(new Color(255, 105, 180)); // Нежно-розовый текст
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(255, 182, 193)); // Нежно-розовый фон
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Эффекты при наведении
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 105, 180)); // Насыщенно-розовый при наведении
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 182, 193)); // Нежно-розовый фон по умолчанию
            }
        });
        return button;
    }
}