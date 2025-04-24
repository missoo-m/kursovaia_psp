package com.ulya.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private final Connection connection;

    public UserRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public int getUserId(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Возвращаем -1, если пользователь не найден или произошла ошибка
    }

    public boolean isUsernameTaken(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Если результат больше 0, имя уже занято
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(String username, String hashedPassword, String role) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, role);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String authenticateUserWithHash(String username, String hashedPassword) {
        String query = "SELECT role, is_blocked FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                boolean isBlocked = rs.getBoolean("is_blocked");
                if (isBlocked) {
                    return "BLOCKED"; // Учетная запись заблокирована
                }
                return rs.getString("role"); // Возвращаем роль пользователя
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserBlockedStatus(String username, boolean isBlocked) {
        String query = "UPDATE users SET is_blocked = ? WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, isBlocked);
            stmt.setString(2, username);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getCartItems(int userId) {
        String query = "SELECT p.id, p.name, p.category, p.price, c.quantity " +
                "FROM cart c JOIN products p ON c.product_id = p.id " +
                "WHERE c.user_id = ?";
        List<String> cartItems = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cartItems.add(
                        rs.getInt("id") + "," +
                                rs.getString("name") + "," +
                                rs.getString("category") + "," +
                                rs.getDouble("price") + "," +
                                rs.getInt("quantity")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartItems;
    }

    public boolean addToCart(int userId, int productId, int quantity) {
        String addToCartQuery = "INSERT INTO cart (user_id, product_id, quantity) " +
                "VALUES (?, ?, ?) ";

        try (PreparedStatement addToCartStmt = connection.prepareStatement(addToCartQuery)) {

            addToCartStmt.setInt(1, userId);
            addToCartStmt.setInt(2, productId);
            addToCartStmt.setInt(3, quantity);
            addToCartStmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean removeFromCart(int userId, int productId) {
        String getCartQuantityQuery = "SELECT quantity FROM cart WHERE user_id = ? AND product_id = ?";
        String deleteFromCartQuery = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        String updateProductStockQuery = "UPDATE products SET quantity = quantity + ? WHERE id = ?";

        try (PreparedStatement getCartQuantityStmt = connection.prepareStatement(getCartQuantityQuery);
             PreparedStatement deleteFromCartStmt = connection.prepareStatement(deleteFromCartQuery);
             PreparedStatement updateProductStockStmt = connection.prepareStatement(updateProductStockQuery)) {

            getCartQuantityStmt.setInt(1, userId);
            getCartQuantityStmt.setInt(2, productId);
            ResultSet rs = getCartQuantityStmt.executeQuery();
            if (rs.next()) {
                int cartQuantity = rs.getInt("quantity");

                deleteFromCartStmt.setInt(1, userId);
                deleteFromCartStmt.setInt(2, productId);
                int rowsDeleted = deleteFromCartStmt.executeUpdate();
                if (rowsDeleted > 0) {
                    updateProductStockStmt.setInt(1, cartQuantity);
                    updateProductStockStmt.setInt(2, productId);
                    updateProductStockStmt.executeUpdate();
                    return true;
                } else {
                    System.out.println("Не удалось удалить товар из корзины для userId: " + userId + ", productId: " + productId);
                    return false;
                }
            } else {
                System.out.println("Товар не найден в корзине для userId: " + userId + ", productId: " + productId);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean removeFromCartToOrder(int userId, int productId) {
        String deleteFromCartQuery = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";

        try (PreparedStatement deleteFromCartStmt = connection.prepareStatement(deleteFromCartQuery)) {

            deleteFromCartStmt.setInt(1, userId);
            deleteFromCartStmt.setInt(2, productId);
            int rowsDeleted = deleteFromCartStmt.executeUpdate();

            if (rowsDeleted > 0) {
                return true;
            } else {
                System.out.println("Не удалось удалить товар из корзины для userId: " + userId + ", productId: " + productId);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearCartAndReturnToStock(int userId) {
        String getCartItemsQuery = "SELECT product_id, quantity FROM cart WHERE user_id = ?";
        String deleteCartQuery = "DELETE FROM cart WHERE user_id = ?";
        String updateProductStockQuery = "UPDATE products SET quantity = quantity + ? WHERE id = ?";

        try (PreparedStatement getCartItemsStmt = connection.prepareStatement(getCartItemsQuery);
             PreparedStatement deleteCartStmt = connection.prepareStatement(deleteCartQuery);
             PreparedStatement updateProductStockStmt = connection.prepareStatement(updateProductStockQuery)) {

            // Получаем все товары из корзины
            getCartItemsStmt.setInt(1, userId);
            ResultSet rs = getCartItemsStmt.executeQuery();
            while (rs.next()) {
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");

                // Возвращаем каждый товар на склад
                updateProductStockStmt.setInt(1, quantity);
                updateProductStockStmt.setInt(2, productId);
                updateProductStockStmt.executeUpdate();
            }

            // Очищаем корзину
            deleteCartStmt.setInt(1, userId);
            deleteCartStmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        String query = "SELECT id, username, role, is_blocked FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(
                        rs.getInt("id") + "," +
                                rs.getString("username") + "," +
                                rs.getString("role") + "," +
                                (rs.getBoolean("is_blocked") ? "Заблокирован" : "Активен")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }


    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(int userId, String newUsername, String newRole, boolean isBlocked) {
        String query = "UPDATE users SET username = ?, role = ?, is_blocked = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newUsername);
            stmt.setString(2, newRole);
            stmt.setBoolean(3, isBlocked);
            stmt.setInt(4, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String[]> getAllOrders() {
        List<String[]> orders = new ArrayList<>();
        String query = "SELECT o.id AS order_id, " +
                "u.username AS user_name, " +
                "p.name AS product_name, " +
                "o.quantity AS quantity, " +
                "o.total_price AS total_price, " +
                "o.order_date AS order_date " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.id " +
                "JOIN products p ON o.product_id = p.id " +
                "ORDER BY o.id";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(new String[]{
                        rs.getString("order_id"),
                        rs.getString("user_name"),
                        rs.getString("product_name"),
                        rs.getString("quantity"),
                        rs.getString("total_price"),
                        rs.getString("order_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }



    // Получение всех заказов для конкретного пользователя
    public List<String[]> getUserOrders(int userId) {
        List<String[]> orders = new ArrayList<>();
        String query = "SELECT o.id, p.name, o.quantity, o.total_price, o.order_date " +
                "FROM orders o " +
                "JOIN products p ON o.product_id = p.id " +
                "WHERE o.user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(new String[]{
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("quantity"),
                            rs.getString("total_price"),
                            rs.getString("order_date")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Добавление нового заказа
    public boolean addOrder(int userId, int productId, int quantity, double totalPrice) {
        String query = "INSERT INTO orders (user_id, product_id, quantity, total_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setDouble(4, totalPrice/quantity);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> getOrdersAnalytics() {
        String query = "SELECT category, COUNT(*) AS order_count " +
                "FROM orders o JOIN products p ON o.product_id = p.id " +
                "GROUP BY category";
        Map<String, Integer> analytics = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                analytics.put(rs.getString("category"), rs.getInt("order_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return analytics;
    }

    public boolean deleteOrder(int orderId) {
        String query = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, orderId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Если удалено больше 0 строк, возвращаем true
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Если произошла ошибка, возвращаем false
        }
    }

    //новое

    public List<Integer> getTakenSeats(int productid) {
        String query = "SELECT seat_number FROM seats WHERE product_id = ? AND is_reserved = TRUE";
        List<Integer> takenSeats = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                takenSeats.add(rs.getInt("seat_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return takenSeats;
    }

    public boolean reserveSeat(int productid, int seatNumber) {
        String query = "UPDATE seats SET is_reserved = TRUE WHERE product_id = ? AND seat_number = ? AND is_reserved = FALSE";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productid);
            stmt.setInt(2, seatNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getMovieTitle(int productid) {
        String query = "SELECT name FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //////
    public boolean addSeatsForProduct(int productId, int totalSeats) {
        String query = "INSERT INTO seats (product_id, seat_number, is_reserved) VALUES (?, ?, FALSE)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 1; i <= totalSeats; i++) {
                stmt.setInt(1, productId);
                stmt.setInt(2, i);
                stmt.addBatch(); // Добавляем в батч
            }
            stmt.executeBatch(); // Выполняем батч
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}
