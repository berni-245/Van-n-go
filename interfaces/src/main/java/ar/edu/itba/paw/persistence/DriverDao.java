package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;

import java.util.Optional;

public interface DriverDao {
    Driver create(String username, String mail, String extra1);
    Optional<Driver> findById(long id);
}