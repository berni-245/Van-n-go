package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.VehicleAlreadyAcceptedException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class DriverServiceImpl extends UserServiceImpl implements DriverService {
    @Autowired
    private final DriverDao driverDao;

    @Autowired
    private final VehicleDao vehicleDao;

    @Autowired
    private final BookingDao bookingDao;

    @Autowired
    private final ImageDao imageDao;

    @Autowired
    private final ZoneDao zoneDao;

    @Autowired
    private final AvailabilityDao availabilityDao;

    public DriverServiceImpl(
            UserDao userDao,
            PasswordEncoder passwordEncoder,
            MailService mailService,
            DriverDao driverDao,
            VehicleDao vehicleDao,
            BookingDao bookingDao,
            ImageDao imageDao,
            ZoneDao zoneDao,
            AvailabilityDao availabilityDao
    ) {
        super(userDao, passwordEncoder, mailService);
        this.driverDao = driverDao;
        this.vehicleDao = vehicleDao;
        this.bookingDao = bookingDao;
        this.imageDao = imageDao;
        this.zoneDao = zoneDao;
        this.availabilityDao = availabilityDao;
    }

    @Transactional
    @Override
    public Driver create(String username, String mail, String password, String extra1, Locale locale) {
        // Driver instance will be created with unencrypted password.
        // Is that a problem tho?
        Driver driver = driverDao.create(username, mail, password, extra1);
        mailService.sendDriverWelcomeMail(mail, username, locale);
        return driver;
    }

    @Override
    public Optional<Driver> findById(long id) {
        return driverDao.findById(id);
    }

    @Override
    public Vehicle addVehicle(long driverId, String plateNumber, double volume, String description, double rate, String imgFilename, byte[] imgData) {
        Vehicle v = vehicleDao.create(driverId, plateNumber, volume, description, rate);
        if (imgFilename != null && imgData != null)
            imageDao.uploadVehicleImage(imgData, imgFilename, v.getId());
        return v;
    }

    @Override
    public List<Vehicle> getVehicles(Driver driver) {
        return vehicleDao.getDriverVehicles(driver);
    }

    @Override
    public List<Vehicle> getVehicles(Driver driver, long zoneId, Size size) {
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        return vehicleDao.getDriverVehicles(driver, zone, size);
    }

//    @Override
//    public List<WeeklyAvailability> getWeeklyAvailability(long id) {
//        return weeklyAvailabilityDao.getDriverWeeklyAvailability(id);
//    }
//
//    @Override
//    public List<WeeklyAvailability> getWeeklyAvailability(long id, long zoneId, Size size) {
//        return weeklyAvailabilityDao.getDriverWeeklyAvailability(id, zoneId, size);
//    }

    @Override
    public void addAvailability(
            long driverId,
            DayOfWeek[] weekDays,
            ShiftPeriod[] periods,
            long vehicleId
    ) {
        Vehicle v = vehicleDao.findById(vehicleId).orElseThrow();
        for (DayOfWeek weekDay : weekDays) {
            for (ShiftPeriod period : periods) {
                availabilityDao.create(v, weekDay, period);
            }
        }
    }


    @Transactional
    @Override
    public void updateWeeklyAvailability(
            long driverId,
            DayOfWeek weekDay,
            ShiftPeriod[] periods,
            long vehicleId
    ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Driver> getAll(long zoneId, Size size, int page) {
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        return driverDao.getAll(zone, size, page * Pagination.SEARCH_PAGE_SIZE);
    }

    @Transactional
    @Override
    public List<Booking> getBookings(long driverId, int page) {
        return bookingDao.getDriverBookings(driverId, page * Pagination.BOOKINGS_PAGE_SIZE);
    }

    @Override
    public List<Booking> getAllBookings(long id) {
        return bookingDao.getAllDriverBookings(id);
    }

    @Override
    public List<Booking> getHistory(long driverId, int page) {
        return bookingDao.getDriverHistory(driverId, Pagination.BOOKINGS_PAGE_SIZE * page);
    }

    @Override
    public int totalMatches(long zoneId, Size size) {
        return driverDao.getSearchCount(zoneId, size);
    }

    @Override
    public int getTotalBookingCount(long driverId) {
        return bookingDao.getDriverBookingCount(driverId);
    }

    @Override
    public int getTotalHistoryCount(long driverId) {
        return bookingDao.getDriverHistoryCount(driverId);
    }

    @Override
    public List<Booking> getBookingsByVehicle(long vehicleId) {
        return bookingDao.getBookingsByVehicle(vehicleId);
    }

    @Override
    public List<Booking> getBookingsByVehicleAndDate(long vehicleId, LocalDate date) {
        return bookingDao.getBookingsByVehicleAndDate(vehicleId, date);
    }

    @Transactional
    @Override
    public void acceptBooking(long bookingId) {
        // No se si lo correcto es conseguir el entity de booking desde el service
        // o desde el dao...
        Optional<Booking> booking = bookingDao.getBookingById(bookingId);
        //TODO move this try catch somewhere else
        booking.ifPresent(book -> {
            try {
                bookingDao.acceptBooking(book);
            } catch (VehicleAlreadyAcceptedException e) {
                // TODO LOGGEAR
            }
        });

    }

    @Transactional
    @Override
    public void rejectBooking(long bookingId) {
        bookingDao.rejectBooking(bookingId);
    }

    @Override
    public Optional<Vehicle> findVehicleByPlateNumber(Driver driver, String plateNumber) {
        return vehicleDao.findByPlateNumber(driver, plateNumber);
    }

    @Override
    public void updateVehicle(Driver driver, long vehicleId, String plateNumber, double volume, String description, double rate, Long oldImgId, String imgFilename, byte[] imgData) {
        Vehicle v = new Vehicle(driver,plateNumber,volume,description,oldImgId,rate);
        if(imgFilename != null && imgData != null){
            long imgId = imageDao.uploadVehicleImage(imgData, imgFilename, vehicleId);
            v.setImgId(imgId);
        }
        vehicleDao.updateVehicle(driver, v);
    }

    @Override
    public void editProfile(long id, String extra1, String cbu) {
        driverDao.editProfile(id, extra1, cbu);
    }

//    @Override
//    public List<WeeklyAvailability> activeAvailabilities(long vehicleId, long zoneId, LocalDate date) {
//        return weeklyAvailabilityDao.getVehicleActiveAvailability(vehicleId, zoneId, date);
//    }
}
