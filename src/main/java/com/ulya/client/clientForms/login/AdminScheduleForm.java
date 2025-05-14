package com.ulya.client.clientForms.login;

import com.ulya.client.clientForms.AdminMainForm;
import com.ulya.client.ClientConnection;
import javax.swing.*;
import java.awt.*;

public class AdminScheduleForm extends JFrame {
    private final ClientConnection clientConnection;
    private final SessionTablePanel sessionTablePanel;
    private final String role;
    private final int userId;

    public AdminScheduleForm(ClientConnection clientConnection, String role, int userId) {
        this.clientConnection = clientConnection;
        this.role = role;
        this.userId = userId;

        setTitle("Управление сеансами");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240)); // Светло-серый фон

        sessionTablePanel = new SessionTablePanel(clientConnection);
        JPanel buttonPanel = createButtonPanel();

        add(sessionTablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        buttonPanel.setBackground(new Color(240, 240, 240)); // Светло-серый фон

        JButton addButton = createStyledButton("Добавить сеанс");
        JButton editButton = createStyledButton("Редактировать");
        JButton backButton = createStyledButton("Назад");

        addButton.addActionListener(e -> showAddSessionForm());
        editButton.addActionListener(e -> showEditSessionForm());
        backButton.addActionListener(e -> returnToMainForm());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(237, 142, 191)); // Розовый фон
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(246, 108, 186), 2), // Граница
                BorderFactory.createEmptyBorder(10, 20, 10, 20)) // Отступы
        );

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(246, 108, 186)); // Темнее при наведении
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(237, 142, 191)); // Возвращаем цвет
            }
        });

        return button;
    }

    private void showAddSessionForm() {
        new SessionForm(clientConnection, "ADD", this).setVisible(true);
    }

    private void showEditSessionForm() {
        int selectedRow = sessionTablePanel.getSessionTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите сеанс для редактирования",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = sessionTablePanel.getSessionTable().getValueAt(selectedRow, 0).toString();
        String productId = sessionTablePanel.getSessionTable().getValueAt(selectedRow, 1).toString();
        String movieName = sessionTablePanel.getSessionTable().getValueAt(selectedRow, 2).toString();
        String time = sessionTablePanel.getSessionTable().getValueAt(selectedRow, 3).toString();
        String date = sessionTablePanel.getSessionTable().getValueAt(selectedRow, 4).toString();

        new SessionForm(clientConnection, "EDIT", this, id, productId, movieName, time, date).setVisible(true);
    }

    public void loadSessions() {
        sessionTablePanel.loadSessions();
    }

    private void returnToMainForm() {
        dispose();
        new AdminMainForm(clientConnection, userId).show();
    }
}