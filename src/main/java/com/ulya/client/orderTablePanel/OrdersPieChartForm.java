package com.ulya.client.orderTablePanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class OrdersPieChartForm extends JFrame {
    private final Map<String, Integer> ordersData;

    public OrdersPieChartForm(Map<String, Integer> ordersData) {
        this.ordersData = ordersData;
        initialize();
    }

    private void initialize() {
        setTitle("Аналитика заказов");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Создание диаграммы
        JFreeChart pieChart = createPieChart();
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Кнопка "Назад"
        JButton backButton = createStyledButton("Назад");
        backButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(backButton);

        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JFreeChart createPieChart() {
        // Создаем набор данных для круговой диаграммы
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : ordersData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // Создаем диаграмму
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Распределение заказов по категориям", // Заголовок диаграммы
                dataset,                               // Данные
                true,                                  // Легенда
                true,                                  // Подсказки
                false                                  // URL
        );

        // Настройка цветов диаграммы
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(new Color(255, 240, 245)); // Нежно-розовый фон
        plot.setSectionPaint("Комедия", new Color(255, 182, 193)); // Нежно-розовый для категории "Комедия"
        plot.setSectionPaint("Боевик", new Color(255, 192, 203)); // Нежно-розовый для категории "Боевик"
        plot.setSectionPaint("Драмма", new Color(255, 224, 230)); // Нежно-розовый для категории "Драмма"
        plot.setSectionPaint("Мультфильм", new Color(255, 240, 245)); // Светло-розовый для категории "Мультфильм"
        plot.setOutlinePaint(Color.WHITE); // Белая граница
        plot.setLabelBackgroundPaint(Color.WHITE); // Белый фон для меток

        return pieChart;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE); // Белый текст для контраста
        button.setBackground(new Color(255, 182, 193)); // Нежно-розовый фон
        button.setOpaque(true); // Гарантируем, что фон отображается
        button.setFocusPainted(false); // Убираем выделение рамки при фокусе
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Добавляем границу для визуального эффекта
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(244, 121, 184), 2), // Нежно-розовая граница
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Внутренние отступы
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(250, 129, 189)); // Насыщенно-розовый при наведении
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 182, 193)); // Нежно-розовый по умолчанию
            }
        });

        return button;
    }

    public static void main(String[] args) {
        // Пример данных для диаграммы
        Map<String, Integer> sampleData = new HashMap<>();
        sampleData.put("Комедия", 50);
        sampleData.put("Боевик", 30);
        sampleData.put("Драмма", 20);
        sampleData.put("Мультфильм", 40);

        new OrdersPieChartForm(sampleData);
    }
}