package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;

import java.util.List;
import java.util.Optional;

// Should extend UserDao
public interface DriverDao {
    Driver create(String username, String mail, String password, String extra1);

    Optional<Driver> findById(long id);

    List<Driver> getAll();

    List<Driver> getAll(Long zoneId, Size size, int offset);

    void updateDriverRating(long driverId);

    Optional<Driver> findByUsername(String username);

    void editProfile(long id, String extra1, String cbu);

    int getSearchCount(long zoneId, Size size);
}