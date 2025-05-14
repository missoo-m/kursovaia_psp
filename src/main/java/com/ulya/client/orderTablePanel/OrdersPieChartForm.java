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
        JFreeChart pieChart = createPieChart();
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
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
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : ordersData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Распределение заказов по категориям", // Заголовок диаграммы
                dataset,                               // Данные
                true,                                  // Легенда
                true,                                  // Подсказки
                false                                  // URL
        );

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(new Color(255, 240, 245));
        plot.setSectionPaint("Комедия", new Color(255, 182, 193));
        plot.setSectionPaint("Боевик", new Color(255, 192, 203));
        plot.setSectionPaint("Драмма", new Color(255, 224, 230));
        plot.setSectionPaint("Мультфильм", new Color(255, 240, 245));
        plot.setOutlinePaint(Color.WHITE);
        plot.setLabelBackgroundPaint(Color.WHITE);

        return pieChart;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(255, 182, 193));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(244, 121, 184), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(250, 129, 189));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 182, 193));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        Map<String, Integer> sampleData = new HashMap<>();
        sampleData.put("Комедия", 50);
        sampleData.put("Боевик", 30);
        sampleData.put("Драмма", 20);
        sampleData.put("Мультфильм", 40);

        new OrdersPieChartForm(sampleData);
    }
}