package com.mycompany.vehicleservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseHelper {
    // XAMPP MySQL connection string pointing to your database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehicle_service_db";
    private static final String USER = "root";      // Default XAMPP username
    private static final String PASSWORD = "";  // Default XAMPP password is empty

    // Initialize and create the table if it doesn't exist
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "customer_name VARCHAR(255) NOT NULL, " +
                     "vehicle_reg VARCHAR(50) NOT NULL, " +
                     "vehicle_type VARCHAR(50) NOT NULL, " +
                     "service_type VARCHAR(100) NOT NULL, " +
                     "addons TEXT, " +
                     "service_date VARCHAR(50) NOT NULL, " +
                     "final_amount DOUBLE NOT NULL)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Insert a new calculated service transaction into XAMPP MySQL
    public static void insertTransaction(String customerName, String vehicleReg, String vehicleType, 
                                         String serviceType, String addons, String serviceDate, double finalAmount) {
        String sql = "INSERT INTO transactions(customer_name, vehicle_reg, vehicle_type, service_type, addons, service_date, final_amount) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerName);
            pstmt.setString(2, vehicleReg);
            pstmt.setString(3, vehicleType);
            pstmt.setString(4, serviceType);
            pstmt.setString(5, addons);
            pstmt.setString(6, serviceDate);
            pstmt.setDouble(7, finalAmount);
            
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    // 1. Fetch history from MySQL
    public static ObservableList<TransactionRecord> getAllTransactions() {
        ObservableList<TransactionRecord> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM transactions ORDER BY id DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new TransactionRecord(
                    rs.getInt("id"),
                    rs.getString("customer_name"),
                    rs.getString("vehicle_reg"),
                    rs.getString("vehicle_type"),
                    rs.getString("service_type"),
                    rs.getString("addons"),
                    rs.getString("service_date"),
                    rs.getDouble("final_amount")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}