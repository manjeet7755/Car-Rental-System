package ui;

import model.Car;
import service.DatabaseHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarRentalGUI extends JFrame {
    private JTextArea slipArea;

    public CarRentalGUI() {
        setTitle("Car Rental System");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // Global font settings
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 16));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 16));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 16));
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 16));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBackground(Color.decode("#f0f0f0"));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton rentButton = createStyledButton(" Rent");
        JButton returnButton = createStyledButton("Return");
        JButton viewButton = createStyledButton("View Cars");
        JButton exitButton = createStyledButton("Exit");

        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(exitButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        centerPanel.setBackground(Color.decode("#ffffff"));

        slipArea = new JTextArea();
        slipArea.setEditable(false);
        slipArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        slipArea.setMargin(new Insets(10, 10, 10, 10));
        slipArea.setBackground(Color.decode("#fdfdfd"));
        slipArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JScrollPane scrollPane = new JScrollPane(slipArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(" Rental Slip"));

        add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        rentButton.addActionListener(e -> rentCar());
        returnButton.addActionListener(e -> returnCar());
        viewButton.addActionListener(e -> viewAvailableCars());
        exitButton.addActionListener(e -> System.exit(0));

        getContentPane().setBackground(Color.decode("#e8ecf0"));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(0x2D89EF));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(0x1E5BA2)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }

    private List<Car> loadAvailableCars() {
        List<Car> cars = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cars WHERE is_rented = false")) {
            while (rs.next()) {
                cars.add(new Car(
                        rs.getString("car_id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getDouble("price_per_day"),
                        rs.getBoolean("is_rented")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load cars from database.");
        }
        return cars;
    }

    private void rentCar() {
        String customerName = JOptionPane.showInputDialog(this, "Enter your name:");
        if (customerName == null || customerName.trim().isEmpty()) return;

        List<Car> availableCars = loadAvailableCars();
        if (availableCars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cars available for rent.");
            return;
        }

        String[] carOptions = availableCars.stream()
                .map(c -> c.getCarId() + ": " + c.getBrand() + " " + c.getModel())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this, "Select a car to rent:", "Cars",
                JOptionPane.QUESTION_MESSAGE, null, carOptions, carOptions[0]);
        if (selected == null) return;

        String carId = selected.split(":")[0];
        Car selectedCar = null;
        for (Car car : availableCars) {
            if (car.getCarId().equals(carId)) {
                selectedCar = car;
                break;
            }
        }
        if (selectedCar == null) return;

        String daysStr = JOptionPane.showInputDialog(this, "Enter number of rental days:");
        int days;
        try {
            days = Integer.parseInt(daysStr);
            if (days <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number of days.");
            return;
        }

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);

            String customerId = "C" + System.currentTimeMillis();
            PreparedStatement custStmt = conn.prepareStatement("INSERT INTO customers VALUES (?, ?)");
            custStmt.setString(1, customerId);
            custStmt.setString(2, customerName);
            custStmt.executeUpdate();

            PreparedStatement carStmt = conn.prepareStatement("UPDATE cars SET is_rented = true WHERE car_id = ?");
            carStmt.setString(1, selectedCar.getCarId());
            carStmt.executeUpdate();

            PreparedStatement rentalStmt = conn.prepareStatement("INSERT INTO rentals (car_id, customer_id, days) VALUES (?, ?, ?)");
            rentalStmt.setString(1, selectedCar.getCarId());
            rentalStmt.setString(2, customerId);
            rentalStmt.setInt(3, days);
            rentalStmt.executeUpdate();

            conn.commit();

            double total = days * selectedCar.getPricePerDay();

            slipArea.setText("====== Rental Slip ======\n");
            slipArea.append("Customer: " + customerName + "\n");
            slipArea.append("Car: " + selectedCar.getBrand() + " " + selectedCar.getModel() + "\n");
            slipArea.append("Days: " + days + "\n");
            slipArea.append("Price/Day: ₹" + selectedCar.getPricePerDay() + "\n");
            slipArea.append("-------------------------\n");
            slipArea.append("Total: ₹" + total + "\n");
            slipArea.append("========================\n");

            try (PrintWriter writer = new PrintWriter("rental_slip.txt")) {
                writer.write(slipArea.getText());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save rental slip.");
            }

            JOptionPane.showMessageDialog(this, "Car rented successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Rental failed. See console.");
        }
    }

    private void returnCar() {
        List<Car> rentedCars = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cars WHERE is_rented = true")) {
            while (rs.next()) {
                rentedCars.add(new Car(
                        rs.getString("car_id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getDouble("price_per_day"),
                        rs.getBoolean("is_rented")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load rented cars.");
            return;
        }

        if (rentedCars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cars are currently rented.");
            return;
        }

        String[] rentedOptions = rentedCars.stream()
                .map(c -> c.getCarId() + ": " + c.getBrand() + " " + c.getModel())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this, "Select a car to return:", "Return Car",
                JOptionPane.QUESTION_MESSAGE, null, rentedOptions, rentedOptions[0]);
        if (selected == null) return;

        String carId = selected.split(":")[0];
        Car returnCar = null;
        for (Car car : rentedCars) {
            if (car.getCarId().equals(carId)) {
                returnCar = car;
                break;
            }
        }
        if (returnCar == null) return;

        try (Connection conn = DatabaseHelper.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE cars SET is_rented = false WHERE car_id = ?");
            stmt.setString(1, returnCar.getCarId());
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                PreparedStatement deleteRental = conn.prepareStatement("DELETE FROM rentals WHERE car_id = ?");
                deleteRental.setString(1, returnCar.getCarId());
                deleteRental.executeUpdate();

                JOptionPane.showMessageDialog(this, "Car returned successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Car return failed.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Return failed. See console.");
        }
    }

    private void viewAvailableCars() {
        List<Car> availableCars = loadAvailableCars();
        StringBuilder sb = new StringBuilder("Available Cars:\n");

        if (availableCars.isEmpty()) {
            sb.append("No cars available.");
        } else {
            for (Car car : availableCars) {
                sb.append(car.getCarId()).append(" - ")
                        .append(car.getBrand()).append(" ").append(car.getModel())
                        .append(" (₹").append(car.getPricePerDay()).append("/day)\n");
            }
        }

        JOptionPane.showMessageDialog(this, sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CarRentalGUI::new);
    }
}
