package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImpl extends UserServiceImpl implements DriverService {
    @Autowired
    private final DriverDao driverDao;

    @Autowired
    private final VehicleDao vehicleDao;

    @Autowired
    private final WeeklyAvailabilityDao weeklyAvailabilityDao;

    @Autowired
    private final BookingDao bookingDao;

    public DriverServiceImpl(
            UserDao userDao,
            PasswordEncoder passwordEncoder,
            MailService mailService,
            DriverDao driverDao,
            VehicleDao vehicleDao,
            WeeklyAvailabilityDao weeklyAvailabilityDao,
            BookingDao bookingDao
    ) {
        super(userDao, passwordEncoder, mailService);
        this.driverDao = driverDao;
        this.vehicleDao = vehicleDao;
        this.weeklyAvailabilityDao = weeklyAvailabilityDao;
        this.bookingDao = bookingDao;
    }

    //@Transactional
    @Override
    public Driver create(String username, String mail, String password, String extra1) {
        long id = createUser(username, mail, password);
        // Driver instance will be created with unencrypted password.
        // Is that a problem tho?
        Driver driver = driverDao.create(id, username, mail, password, extra1);
        mailService.sendDriverWelcomeMail(mail, username);
        return driver;
    }

    @Override
    public Optional<Driver> findById(long id) {
        return driverDao.findById(id);
    }

    @Override
    public Vehicle addVehicle(long driverId, String plateNumber, double volume, String description, double rate) {
        return vehicleDao.create(driverId, plateNumber, volume, description, rate);
    }

    @Override
    public List<Vehicle> getVehicles(long id) {
        return vehicleDao.getDriverVehicles(id);
    }

    @Override
    public List<Vehicle> getVehiclesFull(long id) {
        return vehicleDao.getDriverVehiclesFull(id);
    }

    @Override
    public List<Vehicle> getVehiclesFull(long id, long zoneId, Size size) {
        return vehicleDao.getDriverVehicles(id, zoneId, size);
    }

    @Override
    public List<WeeklyAvailability> getWeeklyAvailability(long id) {
        return weeklyAvailabilityDao.getDriverWeeklyAvailability(id);
    }

    @Override
    public List<WeeklyAvailability> getWeeklyAvailability(long id, long zoneId, Size size) {
        return weeklyAvailabilityDao.getDriverWeeklyAvailability(id, zoneId, size);
    }

    // This should probably be handled better. Right now we won't know
    // which insertions failed.
    // And we Should probably use batch insertion.
    @Override
    public void addWeeklyAvailability(
            long driverId,
            int[] weekDays,
            String[] hourBlocks,
            long[] zoneIds,
            long[] vehicleIds
    ) {
        if (hourBlocks.length == 0) return;
        for (int weekDay : weekDays) {
            for (long vehicleId : vehicleIds) {
                for (long zoneId : zoneIds) {
                    weeklyAvailabilityDao.create(
                            weekDay, hourBlocks, zoneId, vehicleId
                    );
                }
            }
        }
    }

    // @Transactional
    @Override
    public void updateWeeklyAvailability(long driverId, int weekDay, String[] hourBlocks, long zoneId, long vehicleId) {
        weeklyAvailabilityDao.removeAll(weekDay, zoneId, vehicleId);
        addWeeklyAvailability(
                driverId, new int[]{weekDay}, hourBlocks,
                new long[]{zoneId}, new long[]{vehicleId}
        );
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
    public List<Booking> getBookingsByDate(long driverId, LocalDate date) {
        return bookingDao.getBookingsByDate(driverId, date);
    }

    @Override
    public List<Booking> getBookingsByVehicle(long vehicleId) {
        return bookingDao.getBookingsByVehicle(vehicleId);
    }

    @Override
    public List<Booking> getBookingsByVehicleAndDate(long vehicleId, LocalDate date) {
        return bookingDao.getBookingsByVehicleAndDate(vehicleId, date);
    }

    @Override
    public void acceptBooking(long bookingId) {
        bookingDao.acceptBooking(bookingId);
    }

    @Override
    public void rejectBooking(long bookingId) {
        bookingDao.rejectBooking(bookingId);
    }

    @Override
    public Optional<Vehicle> findVehicleByPlateNumber(long driverId, String plateNumber) {
        return vehicleDao.findByPlateNumber(driverId, plateNumber);
    }

    @Override
    public boolean updateVehicle(long driverId, Vehicle vehicle) {
        return vehicleDao.updateVehicle(driverId, vehicle);
    }
}
