package com.ulya.client.orderTablePanel;

import com.ulya.client.ClientConnection;
import com.ulya.client.clientForms.AdminMainForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ViewAllOrdersForm extends JFrame {
    private final ClientConnection clientConnection;

    public ViewAllOrdersForm(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
        initialize();
    }

    private void initialize() {
        setTitle("Просмотр всех заказов");
        setSize(1000, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Таблица для отображения заказов
        JTable ordersTable = createStyledTable();
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 182, 193), 2), // Нежно-розовая граница
                "Все заказы",
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                new Color(246, 124, 185) // Нежно-розовый текст заголовка
        ));

        // Загрузка всех заказов
        loadOrders(ordersTable);

        // Кнопки "Назад" и "Удалить"
        JButton backButton = createStyledButton("Назад");
        backButton.addActionListener(e -> {
            dispose(); // Закрыть текущую форму
            new AdminMainForm(clientConnection).show(); // Открыть панель администратора
        });

        JButton deleteButton = createStyledButton("Удалить заказ");
        deleteButton.addActionListener(e -> deleteSelectedOrder(ordersTable));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(backButton);
        buttonPanel.add(deleteButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void loadOrders(JTable ordersTable) {
        clientConnection.send("VIEW_ALL_ORDERS");

        try {
            int orderCount = Integer.parseInt(clientConnection.receive());
            List<Object[]> rowData = new ArrayList<>();
            for (int i = 0; i < orderCount; i++) {
                String[] data = clientConnection.receive().split(",");
                rowData.add(new Object[]{data[0], data[1], data[2], data[3], data[4], data[5]});
            }
            String[] columnNames = {"ID заказа", "ID пользователя", "Имя пользователя", "ID товара", "Номер места", "Сумма"};
            ordersTable.setModel(new DefaultTableModel(
                    rowData.toArray(new Object[0][]), columnNames
            ));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки заказов. Неверный формат ответа сервера!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteSelectedOrder(JTable ordersTable) {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите заказ для удаления!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int orderId = Integer.parseInt(ordersTable.getValueAt(selectedRow, 0).toString());

        // Подтверждение удаления
        int confirmation = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить заказ?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            clientConnection.send("DELETE_ORDER");
            clientConnection.send(String.valueOf(orderId));

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Заказ успешно удален!");
                loadOrders(ordersTable); // Обновление таблицы
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка удаления заказа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(255, 240, 245)); // Нежно-розовый фон заголовка
        table.getTableHeader().setForeground(new Color(248, 124, 182)); // Нежно-розовый текст заголовка
        table.setSelectionBackground(new Color(255, 192, 203)); // Нежно-розовый цвет выделения
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(255, 240, 245)); // Нежно-розовые линии сетки
        table.setShowGrid(true);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE); // Белый текст для контраста
        button.setBackground(new Color(255, 192, 203)); // Нежно-розовый фон
        button.setOpaque(true); // Гарантируем, что фон отображается
        button.setFocusPainted(false); // Убираем выделение рамки при фокусе
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Добавляем границу для визуального эффекта
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(244, 122, 181), 2), // Нежно-розовая граница
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Внутренние отступы
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(246, 123, 188)); // Насыщенно-розовый при наведении
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 192, 203)); // Нежно-розовый по умолчанию
            }
        });

        return button;
    }
}