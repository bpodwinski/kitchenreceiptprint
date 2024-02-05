package com.kitchenreceiptprint.model;

import com.kitchenreceiptprint.util.ExceptionUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DatabaseModel {
    // Volatile variable to ensure visibility of changes across threads.
    private static volatile DatabaseModel instance;
    private static final String DB = "jdbc:h2:~/kitchenreceiptprint";
    private static final String USER = "krp";
    private static final String PASS = "1234";
    private Connection connection;

    private DatabaseModel() {
        // Private constructor to prevent direct instantiation.
        // Initialize the database connection or other resources here.
    }

    // Double-checked locking to ensure only one instance is created.
    public static DatabaseModel getInstance() {
        if (instance == null) {
            synchronized (DatabaseModel.class) {
                if (instance == null) {
                    instance = new DatabaseModel();
                }
            }
        }
        return instance;
    }

    private static Connection getConnection() throws SQLException {
        if (instance.connection == null || instance.connection.isClosed()) {
            return DriverManager.getConnection(DB, USER, PASS);
        }
        return instance.connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                ExceptionUtil.handleException(e);
            }
        }
    }

    public static void createDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS configuration (" +
                "id_configuration INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL UNIQUE, " +
                "\"value\" VARCHAR(255) NOT NULL);" +
                "CREATE TABLE IF NOT EXISTS printers (" +
                "id_printer INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL);";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            ExceptionUtil.handleException(e);
        }
    }

    public void addConfiguration(String name, String value) {
        if (value != null && !value.isEmpty()) {
            String sql = "MERGE INTO configuration (name, \"value\") KEY(name) VALUES (?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, value);
                stmt.executeUpdate();
            } catch (SQLException e) {
                ExceptionUtil.handleException(e);
            }
        }
    }

    public void addPrinters(List<String> printers) {
        if (printers != null && !printers.isEmpty()) {
            String sql = "MERGE INTO printers (name) KEY (name) VALUES (?)";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                    for (String printer : printers) {
                        stmt.setString(1, printer);
                        stmt.executeUpdate();
                    }
            } catch (SQLException e) {
                ExceptionUtil.handleException(e);
            }
        }
    }

    /**
     * Get all printers
     *
     * @return List<String>.
     */
    public List<String> getAllPrinter() {
        List<String> printerNames = new ArrayList<>();
        String sql = "SELECT name FROM printers";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                printerNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            ExceptionUtil.handleException(e);
        }
        return printerNames;
    }

    /**
     * Delete printers
     *
     * @return void.
     */
    public void delPrinters() {
        String sql = "DELETE FROM printers";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            ExceptionUtil.handleException(e);
        }
    }

    public String getConfiguration(String name) {
        if (name != null && name.isEmpty()) {
            System.out.println("The name is empty, no configuration will be retrieved from the database");
            return null;
        } else {
            String sql = "SELECT \"value\" FROM configuration WHERE name = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                try (ResultSet result = stmt.executeQuery()) {
                    if (result.next()) {
                        return result.getString("value");
                    }
                }
            } catch (SQLException e) {
                ExceptionUtil.handleException(e);
            }
            return null;
        }
    }

    public static void main(String[] args) {
        DatabaseModel.getInstance();
        createDatabase();
    }
}
