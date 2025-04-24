package com.ulya.client.clientForms;

import com.ulya.client.ClientConnection;
import com.ulya.client.clientForms.login.LoginForm;

import javax.swing.*;
import java.awt.*;

public abstract class BaseMainForm {
    protected final ClientConnection clientConnection;
    protected final String role;

    public BaseMainForm(ClientConnection clientConnection, String role) {
        this.clientConnection = clientConnection;
        this.role = role;
    }

    public void showBase(JFrame frame) {
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel headerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel roleLabel = new JLabel("Ваша роль: " + role);
        roleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        roleLabel.setForeground(new Color(255, 105, 180)); // Нежно-розовый текст

        JButton logoutButton = createStyledButton("Выйти");

        gbc.gridx = 0;
        gbc.gridy = 0;
        headerPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        headerPanel.add(logoutButton, gbc);

        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginForm(clientConnection).show();
        });

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        addCustomComponents(mainPanel, gbc, frame);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(255, 192, 203)); // Нежно-розовый фон
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 105, 180), 2), // Нежно-розовая граница
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Внутренний отступ
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 105, 180)); // Насыщенно-розовый при наведении
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 192, 203)); // Нежно-розовый по умолчанию
            }
        });

        return button;
    }

    protected abstract void addCustomComponents(JPanel panel, GridBagConstraints gbc, JFrame frame);
}