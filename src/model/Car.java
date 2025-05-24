package model;

public class Car {
    private String carId;
    private String brand;
    private String model;
    private double pricePerDay;
    private boolean isRented;

    public Car(String carId, String brand, String model, double pricePerDay, boolean isRented) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.pricePerDay = pricePerDay;
        this.isRented = isRented;
    }

    public String getCarId() { return carId; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double getPricePerDay() { return pricePerDay; }
    public boolean isRented() { return isRented; }

    public void setRented(boolean rented) { isRented = rented; }

    @Override
    public String toString() {
        return brand + " " + model;
    }
}
