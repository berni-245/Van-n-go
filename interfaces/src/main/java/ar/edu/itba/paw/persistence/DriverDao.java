package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;

import java.util.List;
import java.util.Optional;

// Should extend UserDao
public interface DriverDao {
    Driver create(long id, String extra1);

    Optional<Driver> findById(long id);

    List<Driver> getAll();

    List<Driver> getAll(long zoneId);

    List<Driver> getAll(Long zoneId, Size size);

    void updateDriverRating(long driverId);

    Optional<Driver> findByUsername(String username);

    void editProfile(long id, String extra1, String cbu);
}