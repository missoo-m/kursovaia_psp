package com.ulya.client.productForms.old;

import com.ulya.client.ClientConnection;
import com.ulya.client.clientForms.AdminMainForm;


import javax.swing.*;
import java.awt.*;

public class DeleteProductForm {
    private final ClientConnection clientConnection;

    public DeleteProductForm(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void show() {
        JFrame frame = new JFrame("Удалить товар");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("ID товара:");
        JTextField idField = new JTextField(15);

        JButton deleteButton = new JButton("Удалить");
        JButton backButton = new JButton("Назад");

        idLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        idField.setFont(new Font("Arial", Font.PLAIN, 16));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(deleteButton, gbc);

        gbc.gridy = 2;
        panel.add(backButton, gbc);

        deleteButton.addActionListener(e -> {
            handleDelete(frame, idField);
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            new AdminMainForm(clientConnection).show();
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private void handleDelete(JFrame frame, JTextField idField) {
        String idText = idField.getText();
        int productId;

        try {
            productId = Integer.parseInt(idText);
        } catch (NumberFormatException ex) {
            showMessage(frame, "ID должен быть числом!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        clientConnection.send("DELETE_PRODUCT");
        clientConnection.send(String.valueOf(productId));

        String response = clientConnection.receive();
        if ("SUCCESS".equals(response)) {
            showMessage(frame, "Товар успешно удален!", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            new AdminMainForm(clientConnection).show();
        } else {
            showMessage(frame, "Ошибка удаления товара! Проверьте ID.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(JFrame frame, String message, int messageType) {
        JOptionPane.showMessageDialog(frame, message, "Информация", messageType);
    }
}
