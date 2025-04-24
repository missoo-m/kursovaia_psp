package com.ulya.client.productForms.old;

import com.ulya.client.ClientConnection;
import com.ulya.client.clientForms.AdminMainForm;
import com.ulya.client.clientForms.UserMainForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ViewProductsForm {
    private final ClientConnection clientConnection;
    private final String role;
    private final int userId;

    public ViewProductsForm(ClientConnection clientConnection, String role, int userId) {
        this.clientConnection = clientConnection;
        this.role = role;
        this.userId = userId;
    }

    public void show() {
        JFrame frame = new JFrame("Список товаров");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Создаем главную панель
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Панель для фильтров и поиска
        JPanel filterPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel searchLabel = new JLabel("Поиск:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        filterPanel.add(searchLabel);
        filterPanel.add(searchField);

        // Создаем таблицу с продуктами
        JTable table = createStyledTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 182, 193), 2), // Нежно-розовая граница
                "Список товаров",
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                new Color(255, 105, 180) // Нежно-розовый цвет текста заголовка
        ));

        // Кнопка "Назад"
        JButton backButton = createStyledButton("Назад");
        backButton.addActionListener(e -> {
            frame.dispose();
            new UserMainForm(clientConnection, userId).show();
        });

        // Загрузка данных в таблицу
        DefaultTableModel model = loadProducts(table);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Добавление слушателя для поля поиска
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter(sorter, searchField.getText());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter(sorter, searchField.getText());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter(sorter, searchField.getText());
            }
        });

        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(backButton, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private DefaultTableModel loadProducts(JTable table) {
        clientConnection.send("VIEW_PRODUCTS");
        try {
            int productCount = Integer.parseInt(clientConnection.receive());
            List<Object[]> rowData = new ArrayList<>();
            for (int i = 0; i < productCount; i++) {
                String[] data = clientConnection.receive().split(",");
                rowData.add(new Object[]{data[0], data[1], data[2], data[3], data[4]});
            }

            String[] columnNames = {"ID", "Название", "Категория", "Цена", "Количество"};
            DefaultTableModel model = new DefaultTableModel(rowData.toArray(new Object[0][]), columnNames);
            table.setModel(model);
            return model;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки продуктов. Неверный формат ответа сервера!",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }

    private JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(255, 182, 193)); // Нежно-розовый фон заголовка
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 105, 180)); // Нежно-розовый цвет при выделении
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(255, 192, 203)); // Светло-розовые линии сетки
        table.setShowGrid(true);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE); // Белый текст для контраста
        button.setBackground(new Color(255, 182, 193)); // Нежно-розовый фон
        button.setOpaque(true); // Гарантируем отображение фона
        button.setFocusPainted(false); // Убираем выделение рамки при фокусе
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Добавление рамки для кнопки
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 105, 180), 2), // Нежно-розовая граница
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Внутренние отступы
        ));

        // Эффекты при наведении
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 105, 180)); // Насыщенно-розовый при наведении
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 182, 193)); // Нежно-розовый фон по умолчанию
            }
        });

        return button;
    }

    private void applyFilter(TableRowSorter<DefaultTableModel> sorter, String searchText) {
        if (searchText.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); // Фильтр поиска без учета регистра
        }
    }
}