package com.ulya.client.productForms.old;

import com.ulya.client.ClientConnection;
import com.ulya.client.clientForms.AdminMainForm;

import javax.swing.*;
import java.awt.*;

public class AddProductForm {
    private final ClientConnection clientConnection;

    public AddProductForm(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void show() {
        JFrame frame = new JFrame("Добавить товар");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Название:");
        JTextField nameField = new JTextField(15);

        JLabel categoryLabel = new JLabel("Категория:");
        JTextField categoryField = new JTextField(15);

        JLabel priceLabel = new JLabel("Цена:");
        JTextField priceField = new JTextField(15);

        JLabel quantityLabel = new JLabel("Количество:");
        JTextField quantityField = new JTextField(15);

        JButton addButton = new JButton("Добавить");
        JButton backButton = new JButton("Назад");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        panel.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(priceLabel, gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(quantityLabel, gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(addButton, gbc);

        gbc.gridy = 5;
        panel.add(backButton, gbc);

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String category = categoryField.getText();
            double price;
            int quantity;

            try {
                price = Double.parseDouble(priceField.getText());
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Неверный формат данных!");
                return;
            }

            clientConnection.send("ADD_PRODUCT");
            clientConnection.send(name);
            clientConnection.send(category);
            clientConnection.send(String.valueOf(price));
            clientConnection.send(String.valueOf(quantity));

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(frame, "Товар успешно добавлен!");
                frame.dispose();
                new AdminMainForm(clientConnection).show();
            } else {
                JOptionPane.showMessageDialog(frame, "Ошибка добавления товара!");
            }
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            new AdminMainForm(clientConnection).show();
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}
