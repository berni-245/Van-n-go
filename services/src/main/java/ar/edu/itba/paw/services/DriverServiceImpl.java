package ar.edu.itba.paw.services;

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
    public Driver create(String username, String mail, String password, String description, Locale locale) {
        Driver driver = driverDao.create(username, mail, passwordEncoder.encode(password), description);
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
                driverDao.findById(driverId).orElseThrow(), plateNumber, volume, description, zoneDao.getZonesById(zoneIds), rate
        );
        if (imgFilename != null && imgData != null && imgData.length > 0) {
            imageDao.uploadVehicleImage(imgData, imgFilename, v);
        }
        return v;
    }

    @Override
    public List<Vehicle> getVehicles(Driver driver) {
        return vehicleDao.getDriverVehicles(driver);
    }

    @Override
    public List<Vehicle> getVehicles(Driver driver, long zoneId, Size size, Double priceMin, Double priceMax, DayOfWeek weekday) {
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        return vehicleDao.getDriverVehicles(driver, zone, size, priceMin, priceMax, weekday);
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
    public List<Driver> getAll(long zoneId, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating, int page) {
        //Esto es por si alguien manda un POST con un priceMin>priceMax desde la consola
        if(priceMin != null && priceMax != null && priceMin > priceMax) {
            return Collections.emptyList();
        }
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        return driverDao.getAll(zone, size, priceMin, priceMax, weekday, rating, page * Pagination.SEARCH_PAGE_SIZE);
    }

    @Override
    public List<Booking> getBookings(Driver driver, BookingState state, int page) {
        return bookingDao.getDriverBookings(driver, state, (page - 1) * Pagination.BOOKINGS_PAGE_SIZE);
    }

    @Override
    public long getBookingCount(Driver driver, BookingState state) {
        return bookingDao.getDriverBookingCount(driver, state);
    }

    @Transactional
    @Override
    public Set<DayOfWeek> getWorkingDays(List<Vehicle> vehicles) {
        Set<DayOfWeek> days = new HashSet<>();
        for(Vehicle vehicle : vehicles) {
            for (Availability availability : vehicle.getAvailability()) {
                days.add(availability.getWeekDay());
                if (days.size() == DayOfWeek.values().length) {
                    return days;
                }
            }
        }
        return days;
    }

    @Override
    public int totalMatches(long zoneId, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating) {
        //Esto es por si alguien manda un POST con un priceMin>priceMax desde la consola
        if(priceMin!=null && priceMax!=null&&priceMin>priceMax) {
            return 0;
        }
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        return driverDao.getSearchCount(zone, size, priceMin, priceMax, weekday, rating);
    }

    @Override
    public List<Booking> getBookingsByVehicle(long vehicleId) {
        return bookingDao.getBookingsByVehicle(vehicleDao.findById(vehicleId).orElseThrow());
    }

    @Override
    public List<Booking> getBookingsByVehicle(long vehicleId, LocalDate date) {
        return bookingDao.getBookingsByVehicle(vehicleDao.findById(vehicleId).orElseThrow(), date);
    }

    @Transactional
    @Override
    public void acceptBooking(long bookingId, Locale locale) {
        Booking booking = bookingDao.getBookingById(bookingId).orElseThrow();
        bookingDao.acceptBooking(booking);
        mailService.sendAcceptedBooking(booking.getDate(),booking.getDriver().getUsername(),booking.getClient().getMail(),locale);
    }

    @Override
    public void rejectBooking(long bookingId, Locale locale) {
        Booking booking = bookingDao.getBookingById(bookingId).orElseThrow();
        bookingDao.rejectBooking(booking);
        mailService.sendRejectedBooking(booking.getDate(),booking.getDriver().getUsername(),booking.getClient().getMail(),locale);
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
        if (imgFilename != null && !imgFilename.isEmpty() && imgData != null && imgData.length > 0 && imgData.length < 10*1024*1024) {
            long imgId = imageDao.uploadVehicleImage(imgData, imgFilename, v);
            v.setImgId(imgId);
        }
        vehicleDao.updateVehicle(v);
    }


    @Override
    public void editProfile(Driver driver, String username, String mail, String description, String cbu) {
        driverDao.editProfile(driver, username, mail, description, cbu);
    }

    @Override
    public List<Vehicle> getVehicles(Driver driver, int page) {
        return vehicleDao.getDriverVehicles(driver, page * Pagination.VEHICLES_PAGE_SIZE);
    }

    @Override
    public long getVehicleCount(Driver driver) {
        return vehicleDao.getVehicleCount(driver);
    }

    @Override
    public void deleteVehicle(Vehicle vehicle) {
        vehicleDao.deleteVehicle(vehicle);
    }
}
