package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;

import java.time.DayOfWeek;
import java.util.List;

// Should extend UserDao
public interface DriverDao extends UserDao<Driver> {
    Driver create(String username, String mail, String password, String description, Language language);

    List<Driver> getSearchResults(Zone zone, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating, SearchOrder order, int offset);

    void editProfile(Driver driver, String username, String mail, String description, String cbu);

    int getSearchCount(Zone zone, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating);

    List<DayOfWeek> getDriverWeekDaysOnZoneAndSize(Driver driver, Zone zone, Size size);
}