package ar.edu.itba.paw.models;

import java.util.List;

public class Vehicle {
    private final long id;
    private final long driverId;
    private final String plateNumber;
    private final double volume;
    private final String description;

    private List<WeeklyAvailability> weeklyAvailability;

    public Vehicle(
            long id,
            long driverId,
            String plateNumber,
            double volume,
            String description,
            List<WeeklyAvailability> weeklyAvailability
    ) {
        this.id = id;
        this.driverId = driverId;
        this.plateNumber = plateNumber;
        this.volume = volume;
        this.description = description;
        this.weeklyAvailability = weeklyAvailability;
    }

    public Vehicle(long id, long driverId, String plateNumber, double volume, String description) {
        this(id, driverId, plateNumber, volume, description, null);
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
}
