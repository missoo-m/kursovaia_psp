package com.ulya.client.clientForms.actions;

import com.ulya.client.ClientConnection;
import com.ulya.client.clientForms.AdminMainForm;

import javax.swing.*;
import java.awt.*;

public class ManageUsersForm extends JFrame {
    private final ClientConnection clientConnection;
    private final UserTablePanel userTablePanel;

    public ManageUsersForm(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;

        setTitle("Управление пользователями");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        userTablePanel = new UserTablePanel(clientConnection);
        JPanel buttonPanel = createButtonPanel();

        add(userTablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton addUserButton = createStyledButton("Добавить пользователя");
        JButton editUserButton = createStyledButton("Редактировать пользователя");
        JButton toggleBlockButton = createStyledButton("Заблокировать/Разблокировать");
        JButton deleteUserButton = createStyledButton("Удалить пользователя");
        JButton backButton = createStyledButton("Назад");

        addUserButton.addActionListener(e -> new AddUserForm(clientConnection, userTablePanel));
        editUserButton.addActionListener(e -> showEditUserForm());
        toggleBlockButton.addActionListener(e -> toggleBlockStatus());
        deleteUserButton.addActionListener(e -> deleteUser());
        backButton.addActionListener(e -> {
            dispose();
            new AdminMainForm(clientConnection).show();
        });

        buttonPanel.add(addUserButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(toggleBlockButton);
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(backButton);

        return buttonPanel;
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
                BorderFactory.createLineBorder(new Color(255, 105, 180), 2), // Нежно-розовая граница
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Внутренние отступы
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 105, 180)); // Насыщенно-розовый при наведении
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 182, 193)); // Нежно-розовый по умолчанию
            }
        });

        return button;
    }

    private void showEditUserForm() {
        JTable userTable = userTablePanel.getUserTable();
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пользователя для редактирования!");
            return;
        }

        String userId = userTable.getValueAt(selectedRow, 0).toString();
        String currentUsername = userTable.getValueAt(selectedRow, 1).toString();
        String currentRole = userTable.getValueAt(selectedRow, 2).toString();
        String currentBlockStatus = userTable.getValueAt(selectedRow, 3).toString();

        JFrame frame = new JFrame("Редактировать пользователя");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JLabel usernameLabel = createStyledLabel("Имя пользователя:");
        JTextField usernameField = createStyledTextField(currentUsername);

        JLabel roleLabel = createStyledLabel("Роль:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"USER", "ADMIN"});
        roleComboBox.setSelectedItem(currentRole);

        JLabel blockStatusLabel = createStyledLabel("Статус блокировки:");
        JLabel blockStatusField = createStyledLabel(currentBlockStatus.equals("Активен") ? "Активен" : "Заблокирован");

        JButton saveButton = createStyledButton("Сохранить");
        JButton cancelButton = createStyledButton("Отмена");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(roleLabel);
        panel.add(roleComboBox);
        panel.add(blockStatusLabel);
        panel.add(blockStatusField);
        panel.add(saveButton);
        panel.add(cancelButton);

        saveButton.addActionListener(e -> {
            String newUsername = usernameField.getText();
            String newRole = (String) roleComboBox.getSelectedItem();
            boolean isBlocked = currentBlockStatus.equals("Заблокирован");

            if (newUsername.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Имя пользователя не может быть пустым!");
                return;
            }

            clientConnection.send("EDIT_USER");
            clientConnection.send(userId);
            clientConnection.send(newUsername);
            clientConnection.send(newRole);
            clientConnection.send(String.valueOf(isBlocked));

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(frame, "Данные пользователя успешно обновлены!");
                userTablePanel.loadUsers();
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Ошибка при обновлении данных пользователя!");
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.add(panel);
        frame.setVisible(true);
    }

    private void toggleBlockStatus() {
        JTable userTable = userTablePanel.getUserTable();
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пользователя для изменения статуса блокировки!");
            return;
        }

        String username = userTable.getValueAt(selectedRow, 1).toString();
        String currentBlockStatus = userTable.getValueAt(selectedRow, 3).toString();
        boolean block = "Активен".equals(currentBlockStatus);

        clientConnection.send(block ? "BLOCK_USER" : "UNBLOCK_USER");
        clientConnection.send(username);

        String response = clientConnection.receive();
        if ("SUCCESS".equals(response)) {
            JOptionPane.showMessageDialog(this, block ? "Пользователь заблокирован!" : "Пользователь разблокирован!");
            userTablePanel.loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка изменения статуса блокировки!");
        }
    }

    private void deleteUser() {
        JTable userTable = userTablePanel.getUserTable();
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пользователя для удаления!");
            return;
        }

        String userId = userTable.getValueAt(selectedRow, 0).toString();

        int confirmation = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить пользователя?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            clientConnection.send("DELETE_USER");
            clientConnection.send(userId);

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Пользователь успешно удален!");
                userTablePanel.loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка удаления пользователя!");
            }
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(255, 105, 180)); // Нежно-розовый текст
        return label;
    }

    private JTextField createStyledTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        return textField;
    }
}