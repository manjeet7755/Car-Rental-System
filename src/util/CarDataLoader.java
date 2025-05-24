package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CarDataLoader {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/car_rental_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";     // Change as needed
    private static final String DB_PASSWORD = "@Manjeet77"; // Change as needed

    public static void main(String[] args) {
        String insertSQL = "INSERT INTO cars (car_id, brand, model, price_per_day, is_rented) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            conn.setAutoCommit(false);  // Start transaction

            // Insert car 1
            pstmt.setString(1, "01");
            pstmt.setString(2, "Toyota");
            pstmt.setString(3, "Fortuner");
            pstmt.setDouble(4, 2000.0);
            pstmt.setBoolean(5, false);
            pstmt.addBatch();

            // Insert car 2
            pstmt.setString(1, "02");
            pstmt.setString(2, "Mahindra");
            pstmt.setString(3, "Scorpio");
            pstmt.setDouble(4, 1600.0);
            pstmt.setBoolean(5, false);
            pstmt.addBatch();

            // Insert car 3
            pstmt.setString(1, "03");
            pstmt.setString(2, "Mahindra");
            pstmt.setString(3, "Thar");
            pstmt.setDouble(4, 1500.0);
            pstmt.setBoolean(5, false);
            pstmt.addBatch();

            // Insert car 4
            pstmt.setString(1, "04");
            pstmt.setString(2, "Mahindra");
            pstmt.setString(3, "Scorpio N");
            pstmt.setDouble(4, 1800.0);
            pstmt.setBoolean(5, false);
            pstmt.addBatch();

            int[] counts = pstmt.executeBatch();
            conn.commit();

            System.out.println("Inserted " + counts.length + " cars into the database successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error inserting car data.");
        }
    }
}
