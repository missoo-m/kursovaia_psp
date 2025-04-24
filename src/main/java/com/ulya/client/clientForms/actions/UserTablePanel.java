package com.ulya.client.clientForms.actions;

import com.ulya.client.ClientConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserTablePanel extends JPanel {
    private final ClientConnection clientConnection;
    private final JTable userTable;

    public UserTablePanel(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
        this.userTable = createStyledTable();
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 105, 180), 2), // Нежно-розовая граница
                "Список пользователей",
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                new Color(255, 105, 180) // Нежно-розовый цвет текста заголовка
        ));

        add(scrollPane, BorderLayout.CENTER);
        loadUsers();
    }

    public JTable getUserTable() {
        return userTable;
    }

    public void loadUsers() {
        clientConnection.send("VIEW_USERS");

        try {
            int userCount = Integer.parseInt(clientConnection.receive());
            List<Object[]> rowData = new ArrayList<>();
            for (int i = 0; i < userCount; i++) {
                String[] data = clientConnection.receive().split(",");
                rowData.add(new Object[]{data[0], data[1], data[2], data[3]});
            }
            String[] columnNames = {"ID", "Имя пользователя", "Роль", "Статус блокировки"};
            DefaultTableModel model = new DefaultTableModel(
                    rowData.toArray(new Object[0][]), columnNames
            );
            userTable.setModel(model);

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            userTable.setRowSorter(sorter);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки пользователей. Неверный формат ответа сервера!",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(255, 240, 245)); // Нежно-розовый фон шапки
        table.getTableHeader().setForeground(new Color(255, 105, 180)); // Нежно-розовый текст
        table.setSelectionBackground(new Color(255, 192, 203)); // Нежно-розовая подсветка строки
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(255, 240, 245)); // Нежно-розовые линии
        table.setShowGrid(true);

        // Включение сортировки
        table.setAutoCreateRowSorter(true);

        return table;
    }
}