package com.ulya.client.cartForms;

import com.ulya.client.ClientConnection;
import com.ulya.client.clientForms.MovieSeatSelectionForm;
import com.ulya.client.clientForms.UserMainForm;
import com.ulya.client.orderTablePanel.AddToOrderForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CartCrudForm {
    private final ClientConnection clientConnection;
    private final int userId;

    public CartCrudForm(ClientConnection clientConnection, int userId) {
        this.clientConnection = clientConnection;
        this.userId = userId;
    }

    public void show() {
        JFrame frame = new JFrame("Управление корзиной");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Таблица для отображения содержимого корзины
        JTable cartTable = createStyledTable();
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 192, 203), 2),
                "Содержимое корзины",
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                new Color(246, 123, 182) // Нежно-розовый цвет заголовка
        ));

        // Кнопки CRUD операций
        JButton addButton = createStyledButton("Добавить товар в корзину");
        JButton removeButton = createStyledButton("Удалить товар из корзины");
        JButton clearButton = createStyledButton("Очистить корзину");
        JButton addToOrderButton = createStyledButton("Добавить товар в заказ");
        JButton backButton = createStyledButton("Назад");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(addToOrderButton);
        buttonPanel.add(backButton);

        // Загрузка содержимого корзины
        loadCartItems(cartTable);

        // Обработчики кнопок
        addButton.addActionListener(e -> showAddToCartForm(frame, cartTable));
        removeButton.addActionListener(e -> showRemoveFromCartForm(frame, cartTable));
        clearButton.addActionListener(e -> {
            clearCart();
            loadCartItems(cartTable);
        });

        addToOrderButton.addActionListener(e -> {
            frame.dispose();
            new AddToOrderForm(clientConnection, userId).show();
        });
        backButton.addActionListener(e -> {
            frame.dispose();
            new UserMainForm(clientConnection, userId).show();
        });

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(255, 240, 245)); // Нежно-розовый фон заголовка
        table.getTableHeader().setForeground(new Color(255, 192, 203));
        table.setSelectionBackground(new Color(255, 192, 203)); // Нежно-розовый при выделении
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(255, 240, 245)); // Нежно-розовые линии сетки
        table.setShowGrid(true);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE); // Белый текст для контраста
        button.setBackground(new Color(248, 123, 186)); // Нежно-розовый фон
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Добавление рамки для визуального эффекта
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(246, 108, 186), 2), // Насыщенно-розовая граница
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Внутренние отступы
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(244, 84, 169)); // Более насыщенный розовый при наведении
                button.setBorder(BorderFactory.createLineBorder(new Color(255, 192, 203), 2)); // Граница при наведении
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 192, 203)); // Нежно-розовый фон по умолчанию
                button.setBorder(BorderFactory.createLineBorder(new Color(243, 95, 182), 2)); // Оригинальная граница
            }
        });

        return button;
    }

    private void loadCartItems(JTable cartTable) {
        clientConnection.send("GET_CART_ITEMS");
        clientConnection.send(String.valueOf(userId));
        int cartSize = Integer.parseInt(clientConnection.receive());
        List<Object[]> rowData = new ArrayList<>();
        for (int i = 0; i < cartSize; i++) {
            String[] data = clientConnection.receive().split(",");
            rowData.add(new Object[]{data[0], data[1], data[2], data[3], data[4]});
        }
        String[] columnNames = {"ID", "Название", "Категория", "Цена", "Номер места"};
        DefaultTableModel model = new DefaultTableModel(
                rowData.toArray(new Object[0][]), columnNames
        );
        cartTable.setModel(model);

        // Устанавливаем TableRowSorter для сортировки
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        cartTable.setRowSorter(sorter);
    }

    private void showAddToCartForm(JFrame parentFrame, JTable cartTable) {
        JFrame frame = new JFrame("Выбор мест для добавления в корзину");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = createStyledLabel("Выберите фильм и места:");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Интеграция формы выбора мест
        MovieSeatSelectionForm seatSelectionForm = new MovieSeatSelectionForm(clientConnection, userId) {
            @Override
            protected void onSeatSelected(int productId, int seatNumber) {
                clientConnection.send("ADD_TO_CART");
                clientConnection.send(String.valueOf(userId));
                clientConnection.send(String.valueOf(productId));
                clientConnection.send(String.valueOf(seatNumber));

                String response = clientConnection.receive();
                if ("SUCCESS".equals(response)) {
                    JOptionPane.showMessageDialog(frame, "Место " + seatNumber + " успешно добавлено в корзину!");
                    loadCartItems(cartTable); // Обновляем содержимое корзины
                } else {
                    JOptionPane.showMessageDialog(frame, "Ошибка добавления места в корзину!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        // Добавляем панель выбора мест
        mainPanel.add(seatSelectionForm.getContentPane(), BorderLayout.CENTER);

        // Кнопки управления
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton cancelButton = createStyledButton("Отмена");

        cancelButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(255, 192, 203)); // Нежно-розовый текст
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        return textField;
    }

    private void showRemoveFromCartForm(JFrame parentFrame, JTable cartTable) {
        JFrame frame = new JFrame("Удалить товар из корзины");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(parentFrame);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel productIdLabel = createStyledLabel("ID товара:");
        JTextField productIdField = createStyledTextField();

        JButton removeButton = createStyledButton("Удалить");
        JButton cancelButton = createStyledButton("Отмена");

        formPanel.add(productIdLabel);
        formPanel.add(productIdField);
        formPanel.add(removeButton);
        formPanel.add(cancelButton);

        removeButton.addActionListener(e -> {
            try {
                int productId = Integer.parseInt(productIdField.getText());

                clientConnection.send("REMOVE_FROM_CART");
                clientConnection.send(String.valueOf(userId));
                clientConnection.send(String.valueOf(productId));

                String response = clientConnection.receive();
                if ("SUCCESS".equals(response)) {
                    JOptionPane.showMessageDialog(frame, "Товар успешно удален из корзины!");
                    loadCartItems(cartTable);
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Ошибка удаления товара из корзины!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Введите корректные данные!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.add(formPanel);
        frame.setVisible(true);
    }

    private void clearCart() {
        clientConnection.send("CLEAR_CART");
        clientConnection.send(String.valueOf(userId));
        String response = clientConnection.receive();
        if ("SUCCESS".equals(response)) {
            JOptionPane.showMessageDialog(null, "Корзина успешно очищена!");
        } else {
            JOptionPane.showMessageDialog(null, "Ошибка при очистке корзины!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}