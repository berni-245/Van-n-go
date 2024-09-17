package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {
    @Autowired
    private UserService userService;

    @Autowired
    private DriverDao driverDao;

    @Autowired
    private VehicleDao vehicleDao;

    @Autowired
    private WeeklyAvailabilityDao weeklyAvailabilityDao;

    @Autowired
    private BookingDao bookingDao;

    public DriverServiceImpl(final DriverDao driverDao) {
        this.driverDao = driverDao;
    }

    @Override
    public Driver create(String username, String mail, String password, String extra1) {
        User user = userService.create(username, mail, password);
        return driverDao.create(user, extra1);
    }

    @Override
    public Optional<Driver> findById(long id) {
        return driverDao.findById(id);
    }

    @Override
    public Vehicle addVehicle(long driverId, String plateNumber, double volume, String description) {
        return vehicleDao.create(driverId, plateNumber, volume, description);
    }

    @Override
    public List<Vehicle> getVehicles(long id) {
        return vehicleDao.getDriverVehicles(id);
    }

    @Override
    public List<WeeklyAvailability> getWeeklyAvailability(long id) {
        return weeklyAvailabilityDao.getDriverWeeklyAvailability(id);
    }

    // This should probably be handled better. Right now we won't know
    // which insertions failed.
    // And we Should probably use batch insertion.
    @Override
    public List<WeeklyAvailability> addWeeklyAvailability(
            long driverId,
            int[] weekDays,
            String timeStart,
            String timeEnd,
            long[] zoneIds,
            long[] vehicleIds
    ) {
        List<WeeklyAvailability> availabilities = new ArrayList<>();
        for (int weekDay : weekDays) {
            for (long vehicleId : vehicleIds) {
                for (long zoneId : zoneIds) {
                    Optional<WeeklyAvailability> availability = weeklyAvailabilityDao.create(
                            weekDay, timeStart, timeEnd, zoneId, vehicleId
                    );
                    availability.ifPresent(availabilities::add);
                }
            }
        }
        return availabilities;
    }

    @Override
    public List<Driver> getAll() {
        return driverDao.getAll();
    }

    @Override
    public List<Driver> getAll(long zoneId) {
        return List.of();
    }

    @Override
    public List<Driver> getAll(long zoneId, Size size) {
        return driverDao.getAll(zoneId, size);
    }

    @Override
    public List<Booking> getBookings(long driverId) {
        return bookingDao.getBookings(driverId);
    }

    @Override
    public List<Booking> getBookingsByDate(long driverId, Date date) {
        return bookingDao.getBookingsByDate(driverId, date);
    }

    @Override
    public void acceptBooking(long bookingId) {
        bookingDao.acceptBooking(bookingId);
    }

    @Override
    public void rejectBooking(long bookingId) {
        bookingDao.rejectBooking(bookingId);
    }
}
