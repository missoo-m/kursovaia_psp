package com.ulya.client;

import com.ulya.client.clientForms.AdminMainForm;
import com.ulya.client.clientForms.login.LoginForm;
import com.ulya.client.clientForms.login.RegisterForm;

import javax.swing.*;

public class ClientApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientConnection clientConnection = new ClientConnection("localhost", 1234);
            new AdminMainForm(clientConnection).show();
            //new LoginForm(clientConnection).show();
        });
    }
}
