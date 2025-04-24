package com.ulya.client.orderTablePanel;

import com.ulya.client.ClientConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrdersTablePanel extends JPanel {
    private final ClientConnection clientConnection;
    private final JTable ordersTable;

    public OrdersTablePanel(ClientConnection clientConnection, int userId) {
        this.clientConnection = clientConnection;
        this.ordersTable = createStyledTable();
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 182, 193), 2), // Нежно-розовая граница
                "Список заказов",
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                new Color(251, 125, 191) // Нежно-розовый текст заголовка
        ));

        add(scrollPane, BorderLayout.CENTER);
        loadOrders(userId);
    }

    private void loadOrders(int userId) {
        clientConnection.send("VIEW_ORDERS");
        clientConnection.send(String.valueOf(userId));
        try {
            int orderCount = Integer.parseInt(clientConnection.receive());
            List<Object[]> rowData = new ArrayList<>();
            for (int i = 0; i < orderCount; i++) {
                String[] data = clientConnection.receive().split(",");
                rowData.add(new Object[]{data[0], data[1], data[2], data[3], data[4]});
            }
            String[] columnNames = {"ID", "Название продукта", "Номер места", "Сумма", "Дата заказа"};
            ordersTable.setModel(new DefaultTableModel(rowData.toArray(new Object[0][]), columnNames));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки заказов!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(255, 240, 245)); // Нежно-розовый фон заголовка
        table.getTableHeader().setForeground(new Color(248, 123, 179)); // Нежно-розовый текст заголовка
        table.setSelectionBackground(new Color(255, 192, 203)); // Нежно-розовый цвет выделения
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(255, 240, 245)); // Нежно-розовые линии сетки
        table.setShowGrid(true);
        table.setAutoCreateRowSorter(true);
        return table;
    }
}