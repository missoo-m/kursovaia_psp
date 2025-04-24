package com.ulya.client.productForms;

import com.ulya.client.ClientConnection;
import com.ulya.client.clientForms.AdminMainForm;
import com.ulya.client.clientForms.UserMainForm;

import javax.swing.*;
import java.awt.*;

public class ProductCrudForm extends JFrame {
    private final ClientConnection clientConnection;
    private final ProductTablePanel productTablePanel;
    private final String role;
    private final int userId;

    public ProductCrudForm(ClientConnection clientConnection, String role, int userId) {
        this.userId = userId;
        this.role = role;
        this.clientConnection = clientConnection;
        setTitle("Управление фильмами");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        productTablePanel = new ProductTablePanel(clientConnection);
        JPanel buttonPanel = createButtonPanel();

        add(productTablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton addButton = createStyledButton("Добавить фильм");
        JButton editButton = createStyledButton("Редактировать фильм");
        JButton deleteButton = createStyledButton("Удалить фильм");
        JButton refreshButton = createStyledButton("Обновить");
        JButton backButton = createStyledButton("Назад");

        addButton.addActionListener(e -> showAddProductForm());
        editButton.addActionListener(e -> showEditProductForm());
        deleteButton.addActionListener(e -> deleteProduct());
        refreshButton.addActionListener(e -> productTablePanel.loadProducts());
        backButton.addActionListener(e -> {
            if (role.equals("ADMIN")) {
                dispose();
                new AdminMainForm(clientConnection, userId).show();
            }
            else {
                dispose();
                new UserMainForm(clientConnection, userId).show();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private void showAddProductForm() {
        new ProductForm(clientConnection, "ADD", this).show();
    }

    private void showEditProductForm() {
        JTable productTable = productTablePanel.getProductTable();
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите фильм для редактирования!");
            return;
        }

        String productId = productTable.getValueAt(selectedRow, 0).toString();
        String name = productTable.getValueAt(selectedRow, 1).toString();
        String category = productTable.getValueAt(selectedRow, 2).toString();
        String price = productTable.getValueAt(selectedRow, 3).toString();
        String quantity = productTable.getValueAt(selectedRow, 4).toString();

        new ProductForm(clientConnection, "EDIT", this, productId, name, category, price, quantity).show();
    }

    private void deleteProduct() {
        JTable productTable = productTablePanel.getProductTable();
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите фильм для удаления!");
            return;
        }

        String productId = productTable.getValueAt(selectedRow, 0).toString();

        int confirmation = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить фильм?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            clientConnection.send("DELETE_PRODUCT");
            clientConnection.send(productId);

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Фильм успешно удален!");
                productTablePanel.loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка удаления фильма!");
            }
        }
    }

    public void loadProducts() {
        productTablePanel.loadProducts();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE); // Белый текст для контраста
        button.setBackground(new Color(237, 142, 191)); // Светло-зелёный фон
        button.setOpaque(true); // Гарантируем, что фон отображается
        button.setFocusPainted(false); // Убираем выделение рамки при фокусе
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Добавляем границу для визуального эффекта
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(246, 108, 186), 2), // Тёмно-зелёная граница
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Внутренние отступы
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(246, 108, 186)); // Насыщенно-зелёный при наведении
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(223, 133, 178)); // Светло-зелёный по умолчанию
            }
        });

        return button;
    }

}
