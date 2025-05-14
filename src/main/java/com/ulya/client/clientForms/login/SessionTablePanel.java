package com.ulya.client.clientForms.login;

import com.ulya.client.ClientConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SessionTablePanel extends JPanel {
    private final ClientConnection clientConnection;
    private final JTable sessionTable;
    private TableRowSorter<DefaultTableModel> sorter;

    public SessionTablePanel(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
        this.sessionTable = createStyledTable();
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240)); // Светло-серый фон

        // Панель поиска
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        filterPanel.setBackground(new Color(240, 240, 240));

        JLabel searchLabel = new JLabel("Поиск:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setForeground(new Color(246, 108, 186));

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(246, 108, 186), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        filterPanel.add(searchLabel, BorderLayout.WEST);
        filterPanel.add(searchField, BorderLayout.CENTER);

        // Стилизованный ScrollPane с таблицей
        JScrollPane scrollPane = new JScrollPane(sessionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(250, 126, 192), 2),
                "Список сеансов",
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                new Color(246, 108, 186)
        ));

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Загрузка данных
        loadSessions();

        // Настройка фильтрации
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter(searchField.getText());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter(searchField.getText());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter(searchField.getText());
            }
        });
    }

    public void loadSessions() {
        try {
            clientConnection.send("GET_ALL_SESSIONS");
            int count = Integer.parseInt(clientConnection.receive());

            List<Object[]> rowData = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String[] parts = clientConnection.receive().split(",");
                rowData.add(new Object[]{parts[0], parts[1], parts[2], parts[3], parts[4]});
            }

            String[] columnNames = {"ID", "ID фильма", "Фильм", "Время", "Дата"};
            DefaultTableModel model = new DefaultTableModel(
                    rowData.toArray(new Object[0][]), columnNames
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            sessionTable.setModel(model);

            // Настройка сортировки
            sorter = new TableRowSorter<>(model);
            sessionTable.setRowSorter(sorter);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка загрузки сеансов: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JTable createStyledTable() {
        JTable table = new JTable();

        // Стилизация таблицы
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(255, 233, 244)); // Светло-розовый фон заголовка
        table.getTableHeader().setForeground(Color.WHITE); // Белый текст заголовков
        table.setSelectionBackground(new Color(246, 108, 186)); // Розовый для выделенной строки
        table.setSelectionForeground(Color.WHITE); // Белый текст для выделенной строки
        table.setGridColor(new Color(236, 143, 194)); // Розовые линии сетки
        table.setShowGrid(true); // Показать сетку

        // Включение сортировки
        table.setAutoCreateRowSorter(true);

        return table;
    }

    private void applyFilter(String searchText) {
        if (sorter != null) {
            if (searchText.trim().isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            }
        }
    }

    public JTable getSessionTable() {
        return sessionTable;
    }
}