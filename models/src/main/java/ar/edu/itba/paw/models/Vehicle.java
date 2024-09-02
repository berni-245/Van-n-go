package ar.edu.itba.paw.models;

public record Vehicle(long id, long driverId, String plateNumber, double volume, String description) {
}
