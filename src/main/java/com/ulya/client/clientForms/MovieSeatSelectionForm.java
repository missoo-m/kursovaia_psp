package com.ulya.client.clientForms;

import com.ulya.client.ClientConnection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MovieSeatSelectionForm extends JFrame {
    private final ClientConnection clientConnection;
    private final int userId;

    public MovieSeatSelectionForm(ClientConnection clientConnection, int userId) {
        this.clientConnection = clientConnection;
        this.userId = userId;
        initialize();
    }

    private void initialize() {
        setTitle("Выбор фильма и мест");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Компоненты выбора фильма
        JComboBox<String> movieComboBox = new JComboBox<>();
        JButton selectMovieButton = createStyledButton("Выбрать фильм");
        JButton backButton = createStyledButton("Назад");

        JPanel moviePanel = new JPanel(new FlowLayout());
        moviePanel.add(new JLabel("Выберите фильм:"));
        moviePanel.add(movieComboBox);
        moviePanel.add(selectMovieButton);
        moviePanel.add(backButton);

        mainPanel.add(moviePanel, BorderLayout.NORTH);

        // Панель для отображения мест
        JPanel seatPanel = new JPanel();
        JScrollPane seatScrollPane = new JScrollPane(seatPanel);
        seatScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 105, 180), 2),
                "Выбор мест",
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                new Color(255, 105, 180)
        ));
        mainPanel.add(seatScrollPane, BorderLayout.CENTER);

        loadMovies(movieComboBox);

        selectMovieButton.addActionListener(e -> {
            String selectedMovie = (String) movieComboBox.getSelectedItem();
            if (selectedMovie == null || selectedMovie.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Выберите фильм!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int productId = Integer.parseInt(selectedMovie.split(",")[0]); // Извлечение ID фильма
            loadSeats(seatPanel, productId);
        });

        backButton.addActionListener(e -> {
            dispose();
            new UserMainForm(clientConnection, userId).show(); // Возврат в главное меню юзера
        });

        add(mainPanel);
        setVisible(true);
    }

    private void loadMovies(JComboBox<String> movieComboBox) {
        clientConnection.send("GET_MOVIES");

        try {
            int movieCount = Integer.parseInt(clientConnection.receive());
            List<String> movies = new ArrayList<>();
            for (int i = 0; i < movieCount; i++) {
                movies.add(clientConnection.receive()); // Формат: "ID,Название"
            }

            movieComboBox.setModel(new DefaultComboBoxModel<>(movies.toArray(new String[0])));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки фильмов. Неверный формат ответа сервера!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadSeats(JPanel seatPanel, int productId) {
        clientConnection.send("GET_TAKEN_SEATS");
        clientConnection.send(String.valueOf(productId));

        try {
            int totalSeats = 50;
            List<Integer> takenSeats = new ArrayList<>();

            int takenSeatsCount = Integer.parseInt(clientConnection.receive());
            for (int i = 0; i < takenSeatsCount; i++) {
                takenSeats.add(Integer.parseInt(clientConnection.receive()));
            }

            seatPanel.removeAll();
            seatPanel.setLayout(new GridLayout((totalSeats + 9) / 10, 10, 5, 5));

            for (int i = 1; i <= totalSeats; i++) {
                final int seatNumber = i;
                JButton seatButton = new JButton(String.valueOf(seatNumber));
                seatButton.setPreferredSize(new Dimension(50, 50));
                seatButton.setFont(new Font("Arial", Font.BOLD, 14));

                if (takenSeats.contains(seatNumber)) {
                    seatButton.setBackground(new Color(255, 99, 71)); // Томатный для занятых мест
                    seatButton.setEnabled(false);
                } else {
                    seatButton.setBackground(new Color(255, 192, 203)); // Светло-зелёный для свободных мест
                    seatButton.addActionListener(e -> {
                        if (reserveSeat(productId, seatNumber)) {
                            seatButton.setBackground(new Color(255, 99, 71)); // Меняем цвет на томатный при бронировании
                            seatButton.setEnabled(false);
                            JOptionPane.showMessageDialog(this, "Место " + seatNumber + " успешно забронировано!");
                            onSeatSelected(productId, seatNumber);
                        } else {
                            JOptionPane.showMessageDialog(this, "Не удалось забронировать место " + seatNumber + ". Возможно, оно уже занято.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }

                seatPanel.add(seatButton);
            }

            seatPanel.revalidate();
            seatPanel.repaint();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки мест. Неверный формат ответа сервера!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    protected void onSeatSelected(int productId, int seatNumber) {
        // Пустой метод, чтобы наследники могли его переопределить
    }

    private boolean reserveSeat(int productId, int seatNumber) {
        clientConnection.send("RESERVE_SEAT");
        clientConnection.send(String.valueOf(productId));
        clientConnection.send(String.valueOf(seatNumber));
        return "SUCCESS".equals(clientConnection.receive());
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
}