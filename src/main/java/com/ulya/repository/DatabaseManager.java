package com.ulya.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DatabaseConnection databaseConnection;

    private DatabaseManager() {
        databaseConnection = DatabaseConnection.getInstance();
        userRepository = new UserRepository();
        productRepository = new ProductRepository();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public int getUserId(String username){
        return userRepository.getUserId(username);
    }
    // Методы работы с пользователями
    public boolean isUsernameTaken(String username) {
        return userRepository.isUsernameTaken(username);
    }

    public boolean registerUser(String username, String hashedPassword, String role) {
        return userRepository.registerUser(username, hashedPassword, role);
    }

    public String authenticateUserWithHash(String username, String hashedPassword) {
        return userRepository.authenticateUserWithHash(username, hashedPassword);
    }

    public boolean updateUser(String username, boolean isBlocked) {
        return userRepository.updateUserBlockedStatus(username, isBlocked);
    }

    // Методы работы с товарами
    /*public boolean addProduct(String name, String category, double price, int quantity) {
        return productRepository.addProduct(name, category, price, quantity);
    }*/
    public int addProduct(String name, String category, double price, int quantity) {
        return productRepository.addProduct(name, category, price, quantity);
    }

    public int getProductIdByName(String productName) {
        return productRepository.getProductIdByName(productName);
    }


    public boolean addSeatsForProduct(int productId, int totalSeats) {
        return userRepository.addSeatsForProduct(productId, totalSeats);
    }

    public List<String> getAllMovies() {
        return productRepository.getAllMovies();
    }

    public boolean updateProduct(int id, String name, String category, double price, int quantity) {
        return productRepository.updateProduct(id, name, category, price, quantity);
    }

    public boolean deleteProduct(int id) {
        return productRepository.deleteProduct(id);
    }

    public List<String> getAllProducts() {
        return productRepository.getAllProducts();
    }

    // Методы работы с корзиной
    public boolean addToCart(int userId, int productId, int quantity) {
        return userRepository.addToCart(userId, productId, quantity);
    }

    public boolean removeFromCart(int userId, int productId) {
        return userRepository.removeFromCart(userId, productId);
    }
    public boolean removeFromCartToOrder(int userId, int productId) {
        return userRepository.removeFromCartToOrder(userId, productId);
    }

    public List<String> getCartItems(int userId) {
        return userRepository.getCartItems(userId);
    }

    public boolean clearCart(int userId) {
        return userRepository.clearCartAndReturnToStock(userId);
    }




    // Закрытие соединения
    public void closeConnection() {
        databaseConnection.closeConnection();
    }
    public boolean deleteUser(int id) {
        return userRepository.deleteUser(id);
    }

    public List<String> getAllUsers() {
        return userRepository.getAllUsers();
    }
    public boolean updateUserInfo(int userId, String newUsername, String newRole, boolean isBlocked) {
        return userRepository.updateUser(userId, newUsername, newRole, isBlocked);
    }

    public List<String[]> getUserOrders(int userId) {
        return userRepository.getUserOrders(userId);
    }
    public boolean addOrder(int userId, int productId, int quantity, double totalPrice) {
        return userRepository.addOrder(userId, productId, quantity, totalPrice);
    }

    public List<String[]> getAllOrders() {
        return userRepository.getAllOrders();
    }

    public Map<String, Integer> getOrdersAnalytics() {
        return userRepository.getOrdersAnalytics();
    }
    public boolean deleteOrder(int id) {
        return userRepository.deleteOrder(id);
    }

    public List<Integer> getTakenSeats(int productId) {
        return userRepository.getTakenSeats(productId);
    }

    public boolean reserveSeat(int productId, int seatNumber) {
        return userRepository.reserveSeat(productId, seatNumber);
    }

    public String getMovieTitle(int productId) {
        return userRepository.getMovieTitle(productId);
    }



}
