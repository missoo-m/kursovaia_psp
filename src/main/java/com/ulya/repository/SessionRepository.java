package com.ulya.repository;

import com.ulya.repository.DatabaseManager;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SessionRepository {
    private final Connection connection;

    public SessionRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public List<String> getAllSession() {
        String query = "SELECT s.id, s.product_id, p.name as movie_name, s.time, s.date " +
                "FROM sessions s JOIN products p ON s.product_id = p.id";
        List<String> sessions = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                sessions.add(
                        rs.getInt("id") + "," +
                                rs.getInt("product_id") + "," +
                                rs.getString("movie_name") + "," +
                                rs.getString("time") + "," +
                                rs.getString("date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }


    public int addSession(int productId, String timeStr, String dateStr) {
        String query = "INSERT INTO sessions (product_id, time, date) VALUES (?, ?::time, ?::date) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.setString(2, timeStr);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            java.util.Date utilDate = sdf.parse(dateStr);
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            stmt.setDate(3, sqlDate);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    public boolean updateSessionInfo(int id, int productId, String timeStr, String dateStr) {
        String query = "UPDATE sessions SET product_id = ?, time = ?::time, date = ?::date WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.setString(2, timeStr);
            stmt.setString(3, dateStr);
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSession(int id) {
        String query = "DELETE FROM sessions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}