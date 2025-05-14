package com.ulya.client.clientForms.login;

import com.ulya.client.ClientConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SessionForm extends JFrame {
    private final ClientConnection clientConnection;
    private final AdminScheduleForm parentForm;
    private final String mode;
    private final String sessionId;

    private JComboBox<ProductItem> productComboBox;
    private JTextField timeField;
    private JTextField dateField;

    private class ProductItem {
        private final int id;
        private final String name;

        public ProductItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }

        @Override
        public String toString() {
            return name;
        }
    }

    public SessionForm(ClientConnection clientConnection, String mode,
                       AdminScheduleForm parentForm, String... sessionData) {
        this.clientConnection = clientConnection;
        this.mode = mode;
        this.parentForm = parentForm;
        this.sessionId = mode.equals("EDIT") ? sessionData[0] : null;

        initUI(sessionData);
    }

    private void initUI(String[] sessionData) {
        setTitle(mode.equals("ADD") ? "Добавление сеанса" : "Редактирование сеанса");
        setSize(400, 300);
        setLocationRelativeTo(parentForm);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        productComboBox = new JComboBox<>();
        loadProducts();

        mainPanel.add(new JLabel("Фильм:"));
        mainPanel.add(productComboBox);

        mainPanel.add(new JLabel("Время (HH:mm):"));
        timeField = new JTextField();
        mainPanel.add(timeField);

        mainPanel.add(new JLabel("Дата (dd.MM.yyyy):"));
        dateField = new JTextField();
        mainPanel.add(dateField);

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(this::saveSession);

        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> dispose());

        mainPanel.add(saveButton);
        mainPanel.add(cancelButton);

        if (mode.equals("EDIT") && sessionData.length >= 5) {
            // Устанавливаем выбранный фильм
            for (int i = 0; i < productComboBox.getItemCount(); i++) {
                ProductItem item = productComboBox.getItemAt(i);
                if (item.getId() == Integer.parseInt(sessionData[1])) {
                    productComboBox.setSelectedIndex(i);
                    break;
                }
            }
            timeField.setText(sessionData[3]);
            dateField.setText(sessionData[4]);
        }

        add(mainPanel);
    }

    private void loadProducts() {
        try {
            productComboBox.removeAllItems();
            clientConnection.send("VIEW_PRODUCTS");
            int count = Integer.parseInt(clientConnection.receive());

            for (int i = 0; i < count; i++) {
                String productData = clientConnection.receive();
                String[] parts = productData.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                productComboBox.addItem(new ProductItem(id, name));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки фильмов: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void saveSession(ActionEvent e) {
        try {
            ProductItem selectedItem = (ProductItem) productComboBox.getSelectedItem();
            String time = timeField.getText();
            String date = dateField.getText();

            if (selectedItem == null || time.isEmpty() || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!");
                return;
            }

            if (!time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                JOptionPane.showMessageDialog(this, "Неверный формат времени! Используйте HH:mm");
                return;
            }

            if (!date.matches("^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.(19|20)\\d\\d$")) {
                JOptionPane.showMessageDialog(this, "Неверный формат даты! Используйте dd.MM.yyyy");
                return;
            }

            int productId = selectedItem.getId();

            clientConnection.send(mode.equals("ADD") ? "ADD_SESSION" : "UPDATE_SESSION");
            if (mode.equals("EDIT")) {
                clientConnection.send(sessionId);
            }
            clientConnection.send(String.valueOf(productId));
            clientConnection.send(time);
            clientConnection.send(date);

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Сеанс успешно сохранен!");
                parentForm.loadSessions();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения: " + response);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
        }
    }
}