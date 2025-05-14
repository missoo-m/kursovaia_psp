package com.ulya.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;



public class ProductRepository {
    private final Connection connection;

    public ProductRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean updateProduct(int id, String name, String category, double price, int quantity) {
        String query = "UPDATE products SET name = ?, category = ?, price = ?, quantity = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.setInt(5, id);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        String query = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllProducts() {
        String query = "SELECT id, name, category, price, quantity FROM products";
        List<String> products = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(
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
        return products;
    }

    ////////
    public int addProduct(String name, String category, double price, int quantity) {
        String query = "INSERT INTO products (name, category, price, quantity) VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }



    public int getProductIdByName(String productName) {
        String query = "SELECT id FROM products WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<String> getAllMovies() {
        String query = "SELECT id, name FROM products";
        List<String> movies = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                movies.add(rs.getInt("id") + "," + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }



}
