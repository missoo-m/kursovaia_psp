package com.ulya.client;

import com.ulya.client.clientForms.login.LoginForm;
import com.ulya.client.productForms.old.AddProductForm;

import javax.swing.*;
import java.awt.*;

public class MainForm {
    private final ClientConnection clientConnection;
    private final String role;

    public MainForm(ClientConnection clientConnection, String role) {
        this.clientConnection = clientConnection;
        this.role = role;
    }

    public void show() {
        JFrame frame = new JFrame("Главное окно");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel roleLabel = new JLabel("Ваша роль: " + role);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton logoutButton = new JButton("Выйти");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(roleLabel, gbc);

        gbc.gridy = 1;
        panel.add(logoutButton, gbc);

        if ("ADMIN".equals(role)) {
            JButton addProductButton = new JButton("Добавить товар");
            addProductButton.setFont(new Font("Arial", Font.BOLD, 18));

            gbc.gridy = 2;
            panel.add(addProductButton, gbc);

            addProductButton.addActionListener(e -> {
                frame.dispose();
                new AddProductForm(clientConnection).show();
            });
        }

        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginForm(clientConnection).show();
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}
