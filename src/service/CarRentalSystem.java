package service;

import model.Car;
import model.Customer;
import model.Rental;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CarRentalSystem {
    private List<Car> cars;
    private List<Customer> customers;
    private List<Rental> rentals;

    public CarRentalSystem() {
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public List<Car> getAvailableCars() {
        return cars.stream()
                .filter(car -> !car.isRented())
                .collect(Collectors.toList());
    }

    public Car findCarById(String carId) {
        return cars.stream()
                .filter(c -> c.getCarId().equals(carId))
                .findFirst()
                .orElse(null);
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void rentCar(Car car, Customer customer, int days) {
        car.setRented(true);
        Rental rental = new Rental(car, customer, days);
        rentals.add(rental);
    }

    public void returnCar(Car car) {
        car.setRented(false);
        rentals.removeIf(rental -> rental.getCar().getCarId().equals(car.getCarId()));
    }
}