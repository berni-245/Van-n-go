package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.Zone;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

// Should extend UserDao
public interface DriverDao {
    Driver create(String username, String mail, String password, String extra1);

    Optional<Driver> findById(long id);

    List<Driver> getAll();

    List<Driver> getAll(Zone zone, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating, int offset);

    Optional<Driver> findByUsername(String username);

    void editProfile(Driver driver, String username, String mail, String extra1, String cbu);

    int getSearchCount(Zone zone, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating);

    List<DayOfWeek> getDriverWeekDaysOnZoneAndSize(Driver driver, Zone zone, Size size);
}