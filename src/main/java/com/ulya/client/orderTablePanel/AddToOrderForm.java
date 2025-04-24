package com.ulya.client.orderTablePanel;

import com.ulya.client.ClientConnection;
import com.ulya.client.cartForms.CartCrudForm;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddToOrderForm extends JFrame {
    private final ClientConnection clientConnection;
    private final int userId;

    public AddToOrderForm(ClientConnection clientConnection, int userId) {
        this.clientConnection = clientConnection;
        this.userId = userId;
        initialize();
    }

    private void initialize() {
        setTitle("Добавить товар из корзины в заказ");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Таблица с товарами в корзине
        JTable cartTable = createStyledTable();
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 182, 193), 2),
                "Список товаров в корзине",
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                new Color(255, 105, 180)
        ));

        // Загрузка товаров в корзине
        loadCartItems(cartTable);

        // Кнопки для действий
        JButton addButton = createStyledButton("Добавить в заказ");
        JButton backButton = createStyledButton("Назад");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчики кнопок
        addButton.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Пожалуйста, выберите товар для добавления в заказ!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int productId = Integer.parseInt(cartTable.getValueAt(selectedRow, 0).toString());
                double price = Double.parseDouble(cartTable.getValueAt(selectedRow, 3).toString());
                int quantity = Integer.parseInt(cartTable.getValueAt(selectedRow, 4).toString());

                // Отправка запроса на добавление товара в заказ
                clientConnection.send("ADD_ORDER");
                clientConnection.send(String.valueOf(userId));
                clientConnection.send(String.valueOf(productId));
                clientConnection.send(String.valueOf(quantity));
                clientConnection.send(String.valueOf(price * quantity));

                String response = clientConnection.receive();
                if ("SUCCESS".equals(response)) {
                    // Удаление товара из корзины после успешного добавления в заказ
                    removeFromCart(productId);
                    loadCartItems(cartTable);
                    JOptionPane.showMessageDialog(this, "Товар успешно добавлен в заказ!");
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка добавления товара в заказ!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Пожалуйста, выберите корректные данные!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new CartCrudForm(clientConnection, userId).show(); // Возврат к корзине
        });

        add(mainPanel);
        setVisible(true);
    }

    private void loadCartItems(JTable cartTable) {
        clientConnection.send("GET_CART_ITEMS");
        clientConnection.send(String.valueOf(userId));

        try {
            int cartSize = Integer.parseInt(clientConnection.receive());
            List<Object[]> rowData = new ArrayList<>();
            for (int i = 0; i < cartSize; i++) {
                String[] data = clientConnection.receive().split(",");
                rowData.add(new Object[]{data[0], data[1], data[2], data[3], data[4]});
            }
            String[] columnNames = {"ID", "Название", "Категория", "Цена", "Номер места"};
            cartTable.setModel(new javax.swing.table.DefaultTableModel(
                    rowData.toArray(new Object[0][]), columnNames
            ));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки корзины. Неверный формат ответа сервера!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void removeFromCart(int productId) {
        clientConnection.send("REMOVE_FROM_CART_TO_ORDER");
        clientConnection.send(String.valueOf(userId));
        clientConnection.send(String.valueOf(productId));

        String response = clientConnection.receive();
        if (!"SUCCESS".equals(response)) {
            JOptionPane.showMessageDialog(this, "Ошибка при удалении товара из корзины!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(255, 240, 245)); // Нежно-розовый цвет
        table.getTableHeader().setForeground(new Color(255, 105, 180));
        table.setSelectionBackground(new Color(255, 192, 203)); // Нежно-розовый для выделения
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(255, 240, 245)); // Нежно-розовые линии сетки
        table.setShowGrid(true);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.PINK);
        button.setBackground(new Color(255, 105, 180)); // Нежно-розовый
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 20, 147)); // Насыщенно-розовый при наведении
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 105, 180)); // Нежно-розовый по умолчанию
            }
        });

        return button;
    }
}