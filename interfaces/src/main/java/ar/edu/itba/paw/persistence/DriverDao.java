package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Vehicle;

import java.util.List;
import java.util.Optional;

// Should extend UserDao
public interface DriverDao {
    Driver create(User user, String extra1);

    Optional<Driver> findById(long id);

    List<Driver> getAll();

    List<Driver> getAll(long zoneId);

    List<Driver> getAll(Long zoneId, Size size);
}