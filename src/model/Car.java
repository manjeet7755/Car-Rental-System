package model;

public class Car {
    private String carId;
    private String brand;
    private String model;
    private double pricePerDay;

    public Car(String carId, String brand, String model, double pricePerDay) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.pricePerDay = pricePerDay;
    }

    public String getCarId() { return carId; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double getPricePerDay() { return pricePerDay; }

    public double calculatePrice(int days) {
        return pricePerDay * days;
    }
}

