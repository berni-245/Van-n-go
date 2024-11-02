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
import java.util.*;

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

    private final PasswordEncoder passwordEncoder;

    public DriverServiceImpl(
            UserDao userDao,
            MailService mailService,
            DriverDao driverDao,
            VehicleDao vehicleDao,
            BookingDao bookingDao,
            ImageDao imageDao,
            ZoneDao zoneDao,
            AvailabilityDao availabilityDao,
            PasswordEncoder passwordEncoder
    ) {
        super(userDao, passwordEncoder, mailService);
        this.driverDao = driverDao;
        this.vehicleDao = vehicleDao;
        this.bookingDao = bookingDao;
        this.imageDao = imageDao;
        this.zoneDao = zoneDao;
        this.availabilityDao = availabilityDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public Driver create(String username, String mail, String password, String extra1, Locale locale) {
        Driver driver = driverDao.create(username, mail, passwordEncoder.encode(password), extra1);
        mailService.sendDriverWelcomeMail(mail, username, locale);
        return driver;
    }

    @Override
    public Optional<Driver> findById(long id) {
        return driverDao.findById(id);
    }

    @Override
    public Vehicle addVehicle(
            long driverId,
            String plateNumber,
            double volume,
            String description,
            List<Long> zoneIds,
            double rate,
            String imgFilename,
            byte[] imgData
    ) {
        Vehicle v = vehicleDao.create(
                driverId, plateNumber, volume, description, zoneDao.getZonesById(zoneIds), rate
        );
        if (imgFilename != null && imgData != null && imgData.length > 0) {
            imageDao.uploadVehicleImage(imgData, imgFilename, v.getId());
        }
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

    @Override
    public void updateAvailability(
            Vehicle vehicle,
            ShiftPeriod[] mondayPeriods,
            ShiftPeriod[] tuesdayPeriods,
            ShiftPeriod[] wednesdayPeriods,
            ShiftPeriod[] thursdayPeriods,
            ShiftPeriod[] fridayPeriods,
            ShiftPeriod[] saturdayPeriods,
            ShiftPeriod[] sundayPeriods
    ) {
        Map<DayOfWeek, ShiftPeriod[]> periods = new EnumMap<>(DayOfWeek.class);
        periods.put(DayOfWeek.MONDAY, mondayPeriods);
        periods.put(DayOfWeek.TUESDAY, tuesdayPeriods);
        periods.put(DayOfWeek.WEDNESDAY, wednesdayPeriods);
        periods.put(DayOfWeek.THURSDAY, thursdayPeriods);
        periods.put(DayOfWeek.FRIDAY, fridayPeriods);
        periods.put(DayOfWeek.SATURDAY, saturdayPeriods);
        periods.put(DayOfWeek.SUNDAY, sundayPeriods);
        availabilityDao.updateVehicleAvailability(vehicle, periods);
    }

    @Override
    public List<Driver> getAll(long zoneId, Size size, int page) {
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        return driverDao.getAll(zone, size, page * Pagination.SEARCH_PAGE_SIZE);
    }

    @Override
    public List<Booking> getBookings(long driverId, int page) {
        Driver driver = driverDao.findById(driverId).orElseThrow();
        return bookingDao.getDriverBookings(driver, page * Pagination.BOOKINGS_PAGE_SIZE);
    }

    @Override
    public List<Booking> getHistory(long driverId, int page) {
        Driver driver = driverDao.findById(driverId).orElseThrow();
        return bookingDao.getDriverHistory(driver, Pagination.BOOKINGS_PAGE_SIZE * page);
    }

    @Override
    public List<Booking> getAllBookings(long id) {
        return bookingDao.getAllDriverBookings(id);
    }

    @Transactional
    @Override
    public Set<DayOfWeek> getDriverWorkingDaysOnZoneWithSize(Driver driver, long zoneId, Size size) {
        List<Vehicle> vehicles = vehicleDao.getDriverVehicles(driver);
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        Set<DayOfWeek> days = new HashSet<>();
        vehicles.forEach(vehicle -> {
            if (vehicle.getZones().contains(zone) && vehicle.getSize().equals(size)) {
                vehicle.getAvailabilitiy().forEach(availability -> days.add(availability.getWeekDay()));
            }
        });
        return days;

    }

    @Override
    public int totalMatches(long zoneId, Size size) {
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        return driverDao.getSearchCount(zone, size);
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

    @Override
    public void acceptBooking(long bookingId) {
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

    @Override
    public void rejectBooking(long bookingId) {
        Booking booking = bookingDao.getBookingById(bookingId).orElseThrow();
        bookingDao.rejectBooking(booking);
    }

    @Override
    public void finishBooking(long bookingId) {
        Booking booking = bookingDao.getBookingById(bookingId).orElseThrow();
        bookingDao.finishBooking(booking);
    }

    @Override
    public Optional<Vehicle> findVehicleByPlateNumber(Driver driver, String plateNumber) {
        return vehicleDao.findByPlateNumber(driver, plateNumber);
    }

    @Override
    public void updateVehicle(
            Driver driver,
            long vehicleId,
            String plateNumber,
            double volume,
            String description,
            List<Long> zoneIds,
            double rate,
            Long oldImgId,
            String imgFilename,
            byte[] imgData
    ) {
        Vehicle v = new Vehicle(vehicleId, driver, plateNumber, volume, description, oldImgId, rate);
        v.setZones(zoneDao.getZonesById(zoneIds));
        if (imgFilename != null && imgData != null && imgData.length > 0) {
            long imgId = imageDao.uploadVehicleImage(imgData, imgFilename, vehicleId);
            v.setImgId(imgId);
        }
        vehicleDao.updateVehicle(v);
    }

    @Transactional
    @Override
    public void editProfile(long id, String extra1, String cbu) {
        driverDao.editProfile(id, extra1, cbu);
    }

    @Override
    public List<Vehicle> getVehicles(Driver driver, int page) {
        return vehicleDao.getDriverVehicles(driver,page*Pagination.VEHICLES_PAGE_SIZE);
    }

    @Override
    public int getVehicleCount(Driver driver){return vehicleDao.getDriverVehicles(driver).size();}

    @Override
    public void deleteVehicle(Vehicle vehicle) {
        vehicleDao.deleteVehicle(vehicle);
    }
}
