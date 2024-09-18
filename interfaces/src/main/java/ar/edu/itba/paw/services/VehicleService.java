package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Vehicle;

import java.util.Optional;

public interface VehicleService {
    boolean plateNumberExists(String plateNumber);
}