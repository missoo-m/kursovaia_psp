package com.ulya.server;

import com.ulya.service.LoginService;
import com.ulya.repository.DatabaseManager;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Класс для обработки запросов клиента.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final LoginService loginService;
    private final DatabaseManager dbManager;
    private final ServerLogger logger;

    public ClientHandler(Socket clientSocket, LoginService loginService, DatabaseManager dbManager, ServerLogger logger) {
        this.clientSocket = clientSocket;
        this.loginService = loginService;
        this.dbManager = dbManager;
        this.logger = logger;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String command;
            while ((command = in.readLine()) != null) {
                logger.log("Получена команда от клиента: " + command);

                switch (command.toUpperCase()) {
                    case "REGISTER":
                        String username = in.readLine();
                        String password = in.readLine();
                        boolean registered = loginService.registerUser(username, password, "USER");
                        if (registered) {
                            logger.log("Пользователь зарегистрирован: " + username);
                            out.println("SUCCESS");
                        } else {
                            logger.log("Ошибка регистрации для пользователя: " + username);
                            out.println("FAILURE");
                        }
                        break;

                    case "LOGIN":
                        username = in.readLine();
                        password = in.readLine();
                        String role = loginService.authenticateUser(username, password);

                        if ("BLOCKED".equals(role)) {
                            logger.log("Аккаунт заблокирован: " + username);
                            out.println("BLOCKED");
                        } else if ("USER".equals(role) || "ADMIN".equals(role)) {
                            int userId = dbManager.getUserId(username); // Получаем ID пользователя из базы
                            if (userId != -1) {
                                logger.log("Пользователь вошел в систему: " + username + " (Роль: " + role + ", ID: " + userId + ")");
                                out.println("SUCCESS");
                                out.println(userId); // Отправляем ID пользователя клиенту
                                out.println(role);   // Отправляем роль пользователя клиенту
                            } else {
                                logger.log("Ошибка получения ID для пользователя: " + username);
                                out.println("FAILURE");
                            }
                        } else {
                            logger.log("Неудачная попытка входа: " + username);
                            out.println("FAILURE");
                        }
                        break;

                    case "BLOCK_USER": {
                        username = in.readLine();
                        boolean success = dbManager.updateUser(username, true);
                        out.println(success ? "SUCCESS" : "FAILURE");
                        break;
                    }
                    case "UNBLOCK_USER": {
                        username = in.readLine();
                        boolean success = dbManager.updateUser(username, false);
                        out.println(success ? "SUCCESS" : "FAILURE");
                        break;
                    }


                    case "VIEW_PRODUCTS":
                        List<String> products = dbManager.getAllProducts();
                        out.println(products.size());
                        for (String product : products) {
                            out.println(product);
                        }
                        logger.log("Отправлен список товаров клиенту");
                        break;

                    case "ADD_PRODUCT":

                        String productName = in.readLine();
                        String category = in.readLine();
                        double price = Double.parseDouble(in.readLine());
                        int quantity = Integer.parseInt(in.readLine());

                        int productId = dbManager.addProduct(productName, category, price, quantity);
                        //boolean added = dbManager.addProduct(productName, category, price, quantity);//  addProduct
                        //метод который возращает productId
                        if (productId > 0) {
                            for (int i = 0; i < 50; i ++)
                            {
                                /**
                                 Добавь метод для добавления места seat_number = i, productId = либо сделай что бы метод addProduct возвращал id добавленного продукта и измени логику так что бы
                                 @param added был равен false если id < 0
                                 либо сделай так что бы после строки 104 был метод, который получает id по productName тебе не нужно будет менять эту логику,
                                 но у тебя будет проблема с двумя запросами к бд это иногда кладет код не факт, но просто придется держать это в голове

                                 */
                            }
                            boolean seatsAdded = dbManager.addSeatsForProduct(productId, 50); // Добавляем 50 мест для нового продукта
                            if (!seatsAdded) {
                                out.println("FAILURE");
                                break;
                            }
                        }

                        out.println(productId > 0 ? "SUCCESS" : "FAILURE");

                        break;

                    case "UPDATE_PRODUCT":

                        productId = Integer.parseInt(in.readLine());
                        productName = in.readLine();
                        category = in.readLine();
                        price = Double.parseDouble(in.readLine());
                        quantity = Integer.parseInt(in.readLine());
                        boolean updated = dbManager.updateProduct(productId, productName, category, price, quantity);

                        logger.log("Продукт с ID " + productId + " обновлен");
                        out.println(updated ? "SUCCESS" : "FAILURE");
                        break;

                    case "DELETE_PRODUCT":


                        productId = Integer.parseInt(in.readLine());
                        boolean deleted = dbManager.deleteProduct(productId);
                        out.println(deleted ? "SUCCESS" : "FAILURE");

                        logger.log("Продукт с ID " + productId + " удален");
                        break;

                    case "ADD_TO_CART":
                        int userId = Integer.parseInt(in.readLine());
                        productId = Integer.parseInt(in.readLine());
                        quantity = Integer.parseInt(in.readLine());
                        boolean added = dbManager.addToCart(userId, productId, quantity);
                        out.println(added ? "SUCCESS" : "FAILURE");
                        break;

                    case "REMOVE_FROM_CART":
                        userId = Integer.parseInt(in.readLine());
                        productId = Integer.parseInt(in.readLine());
                        boolean removed = dbManager.removeFromCart(userId, productId);
                        out.println(removed ? "SUCCESS" : "FAILURE");
                        break;
                    case "REMOVE_FROM_CART_TO_ORDER":
                        userId = Integer.parseInt(in.readLine());
                        productId = Integer.parseInt(in.readLine());
                        removed = dbManager.removeFromCartToOrder(userId, productId);
                        out.println(removed ? "SUCCESS" : "FAILURE");
                        break;

                    case "GET_CART_ITEMS":
                        userId = Integer.parseInt(in.readLine());
                        List<String> cartItems = dbManager.getCartItems(userId);
                        out.println(cartItems.size());
                        for (String item : cartItems) {
                            out.println(item);
                        }
                        break;

                    case "CLEAR_CART":
                        userId = Integer.parseInt(in.readLine());
                        boolean cleared = dbManager.clearCart(userId);

                        out.println(cleared ? "SUCCESS" : "FAILURE");
                        break;

                    case "VIEW_USERS":
                        List<String> users = dbManager.getAllUsers();
                        out.println(users.size()); // Отправляем количество пользователей
                        for (String user : users) {
                            out.println(user); // Отправляем данные каждого пользователя
                        }
                        logger.log("Список пользователей отправлен клиенту");
                        break;

                    case "EDIT_USER":
                        userId = Integer.parseInt(in.readLine());
                        String newUsername = in.readLine();
                        String newRole = in.readLine();
                        boolean block = "Активен".equals(in.readLine());
                        logger.log("Пользователь с ID " + userId + " обновлен");
                        updated = dbManager.updateUserInfo(userId, newUsername, newRole, !block);
                        out.println(updated ? "SUCCESS" : "FAILURE");
                        break;


                    case "ADD_USER":
                        handleAddUser(in, out);
                        break;

                    case "DELETE_USER":
                        handleDeleteUser(in, out);
                        break;

                    case "VIEW_ORDERS":
                        userId = Integer.parseInt(in.readLine());
                        List<String[]> orders = dbManager.getUserOrders(userId);
                        out.println(orders.size());
                        for (String[] order : orders) {
                            out.println(String.join(",", order));
                        }
                        break;

                    case "VIEW_ALL_ORDERS":
                        try {
                            List<String[]> allOrders = dbManager.getAllOrders();

                            out.println(allOrders.size());

                            for (String[] order : allOrders) {
                                out.println(String.join(",", order));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            out.println("ERROR");
                        }
                        break;

                    case "ADD_ORDER":
                        userId = Integer.parseInt(in.readLine());
                        productId = Integer.parseInt(in.readLine());
                        quantity = Integer.parseInt(in.readLine());
                        double totalPrice = Double.parseDouble(in.readLine());
                        added = dbManager.addOrder(userId, productId, quantity, totalPrice);
                        out.println(added ? "SUCCESS" : "FAILURE");
                        break;
                    case "DELETE_ORDER":
                        int orderId = Integer.parseInt(in.readLine());
                        deleted = dbManager.deleteOrder(orderId);
                        out.println(deleted ? "SUCCESS" : "FAILURE");
                        break;
                    case "GET_ORDER_ANALYTICS":
                        Map<String, Integer> orderAnalytics = dbManager.getOrdersAnalytics(); // Этот метод должен возвращать карту с аналитикой заказов
                        out.println(orderAnalytics.size());
                        for (Map.Entry<String, Integer> entry : orderAnalytics.entrySet()) {
                            out.println(entry.getKey() + "," + entry.getValue());
                        }
                        break;

                    case "GET_TAKEN_SEATS": {
                        productId = Integer.parseInt(in.readLine());
                        List<Integer> takenSeats = dbManager.getTakenSeats(productId);
                        out.println(takenSeats.size());
                        for (int seat : takenSeats) {
                            out.println(seat);
                        }
                        break;
                    }
                    case "RESERVE_SEAT": {
                        productId = Integer.parseInt(in.readLine());
                        int seatNumber = Integer.parseInt(in.readLine());
                        boolean success = dbManager.reserveSeat(productId, seatNumber);
                        out.println(success ? "SUCCESS" : "FAILURE");
                        break;
                    }
                    case "GET_MOVIE_TITLE": {
                        productId = Integer.parseInt(in.readLine());
                        String title = dbManager.getMovieTitle(productId);
                        out.println(title != null ? title : "UNKNOWN");
                        break;
                    }
                    case "GET_MOVIES":
                        List<String> movies = dbManager.getAllMovies(); // Метод для получения фильмов из базы
                        out.println(movies.size());
                        for (String movie : movies) {
                            out.println(movie); // Формат: "ID,Название"
                        }
                        break;



                    case "ADD_SESSION": {
                        productId = Integer.parseInt(in.readLine());
                        String time = in.readLine();  // Формат: "HH:mm"
                        String date = in.readLine();  // Формат: "dd.MM.yyyy"
                        int success = dbManager.addSession(productId, time, date);
                        out.println(success != -1 ? "SUCCESS" : "FAILURE");
                        break;
                    }

                    case "GET_ALL_SESSIONS": {
                        List<String> sessions = dbManager.getAllSession();
                        out.println(sessions.size());
                        for (String session : sessions) {
                            out.println(session); // Формат: "ID,productId,movieName,time,date"
                        }
                        break;
                    }
                    case "UPDATE_SESSION" :
                    {
                        int id = Integer.parseInt(in.readLine());
                        productId = Integer.parseInt(in.readLine());
                        String time = in.readLine();
                        String date = in.readLine();
                        updated = dbManager.updateSessionInfo (id,productId, time, date);
                        out.println(updated ? "SUCCESS" : "FAILURE");
                        break;
                    }







                    default:
                        logger.log("Неизвестная команда от клиента");
                        out.println("UNKNOWN_COMMAND");
                        break;
                }
            }
        } catch (IOException e) {
            logger.log("Ошибка связи с клиентом: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                logger.log("Клиент отключился");
            } catch (IOException e) {
                logger.log("Ошибка при закрытии соединения с клиентом: " + e.getMessage());
            }
        }
    }



    private void handleAddUser(BufferedReader in, PrintWriter out) {
        try {
            String username = in.readLine();
            String password = in.readLine();
            String role = in.readLine();

            boolean added = dbManager.registerUser(username, loginService.hashPassword(password), role);
            if (added) {
                logger.log("Пользователь добавлен: " + username);
                out.println("SUCCESS");
            } else {
                logger.log("Ошибка добавления пользователя: " + username);
                out.println("FAILURE");
            }
        } catch (IOException e) {
            logger.log("Ошибка обработки команды ADD_USER: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    private void handleDeleteUser(BufferedReader in, PrintWriter out) {
        try {
            int userId = Integer.parseInt(in.readLine());

            boolean deleted = dbManager.deleteUser(userId);
            if (deleted) {
                logger.log("Пользователь с ID " + userId + " удален");
                out.println("SUCCESS");
            } else {
                logger.log("Ошибка удаления пользователя с ID " + userId);
                out.println("FAILURE");
            }
        } catch (IOException | NumberFormatException e) {
            logger.log("Ошибка обработки команды DELETE_USER: " + e.getMessage());
            out.println("FAILURE");
        }
    }
}
