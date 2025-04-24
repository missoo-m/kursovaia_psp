package com.ulya.repository;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final Connection connection;

    private DatabaseConnection() {
        try {
            // Подключение к PostgreSQL
            String url = "jdbc:postgresql://localhost:5432/postgres"; // URL базы данных
            String user = "postgres"; // Ваше имя пользователя PostgreSQL
            String password = "4321"; // Ваш пароль PostgreSQL
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Подключение к базе данных успешно установлено!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка подключения к базе данных!");
            throw new RuntimeException(e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение с базой данных закрыто.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
