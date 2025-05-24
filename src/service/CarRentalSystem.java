package service;

import model.Car;
import model.Customer;
import model.Rental;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    // Add initial cars
    public void addCar(Car car) {
        cars.add(car);
    }

    public List<Car> getAvailableCars() {
        List<String> rentedCarIds = rentals.stream()
                .map(r -> r.getCar().getCarId())
                .toList();
        return cars.stream()
                .filter(car -> !rentedCarIds.contains(car.getCarId()))
                .collect(Collectors.toList());
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void rentCar(Car car, Customer customer, int days) {
        String rentalId = UUID.randomUUID().toString();
        Rental rental = new Rental(rentalId, car, customer, days);
        rentals.add(rental);
    }

    public void returnCar(Car car) {
        rentals.removeIf(r -> r.getCar().getCarId().equals(car.getCarId()));
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public Car findCarById(String id) {
        return cars.stream()
                .filter(c -> c.getCarId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Customer> getCustomers() {
        return customers;
    }
}
