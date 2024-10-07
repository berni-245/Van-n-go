package ar.edu.itba.paw.models;

import com.google.gson.Gson;

import java.util.List;

public class Vehicle {
    private final long id;
    private final long driverId;
    private final String plateNumber;
    private final double volume;
    private final String description;
    private Integer img;
    private final double rate;

    private List<WeeklyAvailability> weeklyAvailability;

    private static final Gson gson = new Gson();

    public Vehicle(
            long id,
            long driverId,
            String plateNumber,
            double volume,
            String description,
            List<WeeklyAvailability> weeklyAvailability,
            Integer img,
            double rate
    ) {
        this.id = id;
        this.driverId = driverId;
        this.plateNumber = plateNumber;
        this.volume = volume;
        this.description = description;
        this.weeklyAvailability = weeklyAvailability;
        this.img = img;
        this.rate = rate;
    }

    public Vehicle(long id, long driverId, String plateNumber, double volume, String description, Integer img, double rate) {
        this(id, driverId, plateNumber, volume, description, null, img, rate);
    }

    public long getId() {
        return id;
    }

    public long getDriverId() {
        return driverId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public double getVolume() {
        return volume;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "%s||%s||%.2f".formatted(plateNumber, description, volume);
    }

    public List<WeeklyAvailability> getWeeklyAvailability() {
        return weeklyAvailability;
    }

    public void setWeeklyAvailability(List<WeeklyAvailability> weeklyAvailability) {
        this.weeklyAvailability = weeklyAvailability;
    }

    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
    }

    public double getRate() {
        return rate;
    }

    public String toJson() {
        return gson.toJson(this);
    }
}
