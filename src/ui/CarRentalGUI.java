package ui;

import model.Car;
import model.Customer;
import model.Rental;
import service.CarRentalSystem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CarRentalGUI extends JFrame {
    private CarRentalSystem rentalSystem;

    public CarRentalGUI() {
        rentalSystem = new CarRentalSystem();

        // Add some initial cars
        rentalSystem.addCar(new Car("01", "Toyota", "Fortuner", 2000.0));
        rentalSystem.addCar(new Car("02", "Mahindra", "Scorpio", 1600.0));
        rentalSystem.addCar(new Car("03", "Mahindra", "Thar", 1500.0));
        rentalSystem.addCar(new Car("04", "Mahindra", "Scorpio N", 1800.0));

        setTitle("Car Rental System");
        setSize(450, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Use BoxLayout vertical
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 248, 255));  // light blue

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);

        JButton rentButton = new JButton("Rent a Car");
        JButton returnButton = new JButton("Return a Car");
        JButton viewButton = new JButton("View Available Cars");
        JButton exitButton = new JButton("Exit");

        rentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rentButton.setFont(buttonFont);
        rentButton.setBackground(new Color(70, 130, 180)); // steel blue
        rentButton.setForeground(Color.WHITE);
        rentButton.setFocusPainted(false);

        returnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        returnButton.setFont(buttonFont);
        returnButton.setBackground(new Color(70, 130, 180));
        returnButton.setForeground(Color.WHITE);
        returnButton.setFocusPainted(false);

        viewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewButton.setFont(buttonFont);
        viewButton.setBackground(new Color(70, 130, 180));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);

        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setFont(buttonFont);
        exitButton.setBackground(new Color(220, 20, 60)); // crimson red
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);

        panel.add(rentButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(returnButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(viewButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(exitButton);

        add(panel);

        rentButton.addActionListener(e -> rentCar());
        returnButton.addActionListener(e -> returnCar());
        viewButton.addActionListener(e -> viewAvailableCars());
        exitButton.addActionListener(e -> System.exit(0));

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void rentCar() {
        String name = JOptionPane.showInputDialog(this, "Enter your name:");
        if (name == null || name.trim().isEmpty()) return;

        List<Car> availableCars = rentalSystem.getAvailableCars();
        if (availableCars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cars available for rent.");
            return;
        }

        String[] carOptions = availableCars.stream()
                .map(c -> c.getCarId() + ": " + c.getBrand() + " " + c.getModel())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a car to rent:", "Available Cars",
                JOptionPane.QUESTION_MESSAGE, null, carOptions, carOptions[0]);

        if (selected == null) return;

        String carId = selected.split(":")[0];
        Car car = rentalSystem.findCarById(carId);
        if (car == null) return;

        String daysStr = JOptionPane.showInputDialog(this, "Enter number of rental days:");
        int days;
        try {
            days = Integer.parseInt(daysStr);
            if (days <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number of days.");
            return;
        }

        Customer customer = new Customer("CUS" + (rentalSystem.getCustomers().size() + 1), name);
        rentalSystem.addCustomer(customer);

        double price = car.calculatePrice(days);
        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Total Price: R%.2f. Confirm rental?", price),
                "Confirm Rental", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            rentalSystem.rentCar(car, customer, days);
            JOptionPane.showMessageDialog(this, "Car rented successfully.");

            // Show rental slip
            showRentalSlip(customer, car, days, price);
        }
    }

    private void showRentalSlip(Customer customer, Car car, int days, double price) {
        StringBuilder slip = new StringBuilder();
        slip.append("===== Rental Slip =====\n\n");
        slip.append("Customer Name: ").append(customer.getName()).append("\n");
        slip.append("Car: ").append(car.getBrand()).append(" ").append(car.getModel()).append("\n");
        slip.append("Rental Days: ").append(days).append("\n");
        slip.append(String.format("Total Price: R%.2f\n", price));
        slip.append("\nThank you for renting with us!");

        JTextArea slipArea = new JTextArea(slip.toString());
        slipArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        slipArea.setEditable(false);
        slipArea.setBackground(new Color(250, 250, 250));
        slipArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(slipArea);
        scrollPane.setPreferredSize(new Dimension(350, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "Rental Slip", JOptionPane.INFORMATION_MESSAGE);
    }

    private void returnCar() {
        List<Rental> rentals = rentalSystem.getRentals();
        if (rentals.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cars are currently rented.");
            return;
        }

        String[] rentedCars = rentals.stream()
                .map(r -> r.getCar().getCarId() + ": " + r.getCar().getBrand() + " " + r.getCar().getModel())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a car to return:", "Return Car",
                JOptionPane.QUESTION_MESSAGE, null, rentedCars, rentedCars[0]);

        if (selected == null) return;

        String carId = selected.split(":")[0];
        Car car = rentalSystem.findCarById(carId);
        if (car != null) {
            rentalSystem.returnCar(car);
            JOptionPane.showMessageDialog(this, "Car returned successfully.");
        }
    }

    private void viewAvailableCars() {
        List<Car> availableCars = rentalSystem.getAvailableCars();
        if (availableCars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cars available.");
            return;
        }

        StringBuilder sb = new StringBuilder("Available Cars:\n\n");
        for (Car car : availableCars) {
            sb.append(car.getCarId())
                    .append(" - ")
                    .append(car.getBrand())
                    .append(" ")
                    .append(car.getModel())
                    .append(" (R").append(car.getPricePerDay()).append("/day)\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setBackground(new Color(245, 245, 245));
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 250));

        JOptionPane.showMessageDialog(this, scrollPane, "Available Cars", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CarRentalGUI::new);
    }
}

