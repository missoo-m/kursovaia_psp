package com.ulya.client.productForms;

import com.ulya.client.ClientConnection;

import javax.swing.*;
import java.awt.*;

public class ProductForm extends JFrame {
    private final ClientConnection clientConnection;
    private final String mode; // "ADD" или "EDIT"
    private final ProductCrudForm parentForm;
    private final String productId;

    public ProductForm(ClientConnection clientConnection, String mode, ProductCrudForm parentForm) {
        this(clientConnection, mode, parentForm, null, "", "", "", "");
    }

    public ProductForm(ClientConnection clientConnection, String mode, ProductCrudForm parentForm,
                       String productId, String name, String category, String price, String quantity) {
        this.clientConnection = clientConnection;
        this.mode = mode;
        this.parentForm = parentForm;
        this.productId = productId;

        setTitle(mode.equals("ADD") ? "Добавить продукт" : "Редактировать продукт");
        setSize(450, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentForm);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = createStyledLabel("Название:");
        JTextField nameField = createStyledTextField(name);

        JLabel categoryLabel = createStyledLabel("Категория:");
        JTextField categoryField = createStyledTextField(category);

        JLabel priceLabel = createStyledLabel("Цена:");
        JTextField priceField = createStyledTextField(price);

        JLabel quantityLabel = createStyledLabel("Количество:");
        JTextField quantityField = createStyledTextField(quantity);

        JButton saveButton = createStyledButton(mode.equals("ADD") ? "Добавить" : "Сохранить");
        JButton cancelButton = createStyledButton("Отмена");

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
        panel.add(saveButton, gbc);

        gbc.gridy = 5;
        panel.add(cancelButton, gbc);

        saveButton.addActionListener(e -> {
            String productName = nameField.getText();
            String productCategory = categoryField.getText();
            double productPrice;
            int productQuantity;

            try {
                productPrice = Double.parseDouble(priceField.getText());
                productQuantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Пожалуйста, введите корректные данные!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (mode.equals("ADD")) {
                addProduct(productName, productCategory, productPrice, productQuantity);
            } else {
                editProduct(productId, productName, productCategory, productPrice, productQuantity);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        add(panel);
        setVisible(true);
    }

    private void addProduct(String name, String category, double price, int quantity) {
        clientConnection.send("ADD_PRODUCT");
        clientConnection.send(name);
        clientConnection.send(category);
        clientConnection.send(String.valueOf(price));
        clientConnection.send(String.valueOf(quantity));

        String response = clientConnection.receive();
        if ("SUCCESS".equals(response)) {
            JOptionPane.showMessageDialog(this, "Продукт успешно добавлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            parentForm.loadProducts();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении продукта!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editProduct(String id, String name, String category, double price, int quantity) {
        clientConnection.send("UPDATE_PRODUCT");
        clientConnection.send(id);
        clientConnection.send(name);
        clientConnection.send(category);
        clientConnection.send(String.valueOf(price));
        clientConnection.send(String.valueOf(quantity));

        String response = clientConnection.receive();
        if ("SUCCESS".equals(response)) {
            JOptionPane.showMessageDialog(this, "Продукт успешно обновлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            parentForm.loadProducts();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка при обновлении продукта!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(150, 50, 100)); // Розовый оттенок
        return label;
    }

    private JTextField createStyledTextField(String text) {
        JTextField textField = new JTextField(text, 15);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.PINK);
        button.setBackground(new Color(255, 105, 180)); // Розовый оттенок
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 182, 193)); // Светло-розовый при наведении
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 105, 180)); // Обычный розовый
            }
        });

        return button;
    }
}
