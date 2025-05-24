package model;

public class Rental {
    private String rentalId;
    private Car car;
    private Customer customer;
    private int days;

    public Rental(String rentalId, Car car, Customer customer, int days) {
        this.rentalId = rentalId;
        this.car = car;
        this.customer = customer;
        this.days = days;
    }

    public String getRentalId() { return rentalId; }
    public Car getCar() { return car; }
    public Customer getCustomer() { return customer; }
    public int getDays() { return days; }
}
