package com.ulya.client.productForms;

import com.ulya.client.ClientConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProductTablePanel extends JPanel {
    private final ClientConnection clientConnection;
    private final JTable productTable;
    private TableRowSorter<DefaultTableModel> sorter;

    public ProductTablePanel(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
        this.productTable = createStyledTable();
        setLayout(new BorderLayout());

        // Панель для фильтрации и поиска
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel searchLabel = new JLabel("Поиск:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setForeground(new Color(246, 108, 186)); // Тёмно-зелёный текст

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(246, 108, 186), 1), // Зелёная граница
                BorderFactory.createEmptyBorder(5, 5, 5, 5) // Внутренние отступы
        ));

        filterPanel.add(searchLabel, BorderLayout.WEST);
        filterPanel.add(searchField, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(250, 126, 192), 2), // Тёмно-зелёная граница
                "Список продуктов", // Заголовок таблицы
                0,
                0,
                new Font("Arial", Font.BOLD, 16), // Шрифт заголовка
                new Color(246, 108, 186) // Тёмно-зелёный цвет заголовка
        ));


        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Загрузка продуктов
        loadProducts();

        // Подключение фильтрации к полю поиска
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

    public JTable getProductTable() {
        return productTable;
    }

    public void loadProducts() {
        clientConnection.send("VIEW_PRODUCTS");

        try {
            int productCount = Integer.parseInt(clientConnection.receive());
            List<Object[]> rowData = new ArrayList<>();
            for (int i = 0; i < productCount; i++) {
                String[] data = clientConnection.receive().split(",");
                rowData.add(new Object[]{data[0], data[1], data[2], data[3], data[4]});
            }
            String[] columnNames = {"ID", "Название", "Категория", "Цена", "Количество"};
            DefaultTableModel model = new DefaultTableModel(
                    rowData.toArray(new Object[0][]), columnNames
            );
            productTable.setModel(model);

            // Подключение сортировки через TableRowSorter
            sorter = new TableRowSorter<>(model);
            productTable.setRowSorter(sorter);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки продуктов. Неверный формат ответа сервера!",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(255, 233, 244)); // Светло-зелёный фон заголовка
        table.getTableHeader().setForeground(Color.WHITE); // Белый текст заголовков
        table.setSelectionBackground(new Color(246, 108, 186)); // Тёмно-зелёный для выделенной строки
        table.setSelectionForeground(Color.WHITE); // Белый текст для выделенной строки
        table.setGridColor(new Color(236, 143, 194)); // Ярко-зелёные линии сетки
        table.setShowGrid(true); // Показать сетку

        // Включение сортировки
        table.setAutoCreateRowSorter(true);

        return table;
    }


    private void applyFilter(String searchText) {
        if (searchText.trim().isEmpty()) {
            sorter.setRowFilter(null); // Сбрасываем фильтр
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); // Фильтр без учета регистра
        }
    }
}
