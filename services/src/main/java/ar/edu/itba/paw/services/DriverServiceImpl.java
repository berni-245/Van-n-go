package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.DriverVehicleLimitReachedException;
import ar.edu.itba.paw.exceptions.ForbiddenBookingStateOperationException;
import ar.edu.itba.paw.exceptions.ZoneNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;

@Service
public class DriverServiceImpl extends UserServiceImpl<Driver> implements DriverService {
    private final static int MAX_VEHICLES_PER_DRIVER = 100;

    private final DriverDao driverDao;

    private final VehicleDao vehicleDao;

    private final ImageDao imageDao;

    private final ZoneDao zoneDao;

    private final AvailabilityDao availabilityDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverServiceImpl.class);

    @Autowired
    public DriverServiceImpl(
            UserDao<Driver> userDao,
            MailService mailService,
            DriverDao driverDao,
            VehicleDao vehicleDao,
            BookingDao bookingDao,
            ImageDao imageDao,
            ZoneDao zoneDao,
            AvailabilityDao availabilityDao,
            PasswordEncoder passwordEncoder
    ) {
        super(userDao, passwordEncoder, mailService, bookingDao);
        this.driverDao = driverDao;
        this.vehicleDao = vehicleDao;
        this.imageDao = imageDao;
        this.zoneDao = zoneDao;
        this.availabilityDao = availabilityDao;
    }

    @Transactional
    @Override
    public Optional<Driver> findByUsername(String username) {
        return driverDao.findByUsername(username);
    }

    @Override
    public Booking getBookingById(Driver user, int id) {
        return bookingDao.getDriverBookingById(user, id);
    }

    @Transactional
    @Override
    public Driver create(String username, String mail, String password, String description, Locale locale) {
        Driver driver = driverDao.create(username, mail, passwordEncoder.encode(password), description, Language.fromLocale(locale));
        LOGGER.info("Successfully created {} driver", username);
        mailService.sendDriverWelcomeMail(mail, username, locale);
        return driver;
    }

    @Transactional
    @Override
    public Vehicle addVehicle(
            Driver driver,
            String plateNumber,
            double volume,
            String description,
            List<Integer> zoneIds,
            double rate,
            String imgFilename,
            byte[] imgData
    ) {
        if (vehicleDao.getVehicleCount(driver) >= MAX_VEHICLES_PER_DRIVER) {
            throw new DriverVehicleLimitReachedException(driver);
        }
        Vehicle v = vehicleDao.create(
                driver, plateNumber, volume, description, zoneDao.getZonesById(zoneIds), rate
        );
        if (imgFilename != null && imgData != null && imgData.length > 0) {
            imageDao.uploadVehicleImage(imgData, imgFilename, v);
        }
        LOGGER.info("{} added a vehicle with plate {}", driver, plateNumber);
        return v;
    }

    @Transactional
    @Override
    public List<Vehicle> getVehicles(Driver driver, int zoneId, Size size, Double priceMax, DayOfWeek weekday) {
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        List<Vehicle> vehicleList = vehicleDao.getDriverVehicles(driver, zone, size, priceMax, weekday);
        vehicleList.removeIf(v -> v.getAvailability().isEmpty());
        return vehicleList;
    }

    @Transactional
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
        LOGGER.info("Updated weekly availability for plate {}", vehicle.getPlateNumber());
    }

    @Transactional
    @Override
    public List<Driver> getSearchResults(
            int zoneId, Size size, Double priceMax,
            DayOfWeek weekday, Integer rating, SearchOrder order, int page
    ) {
        Zone zone = zoneDao.getZone(zoneId).orElseThrow(ZoneNotFoundException::new);
        return driverDao.getSearchResults(
                zone, size, priceMax, weekday, rating, order,
                (page - 1) * Pagination.SEARCH_PAGE_SIZE
        );
    }

    @Transactional
    @Override
    public int getSearchResultCount(
            int zoneId, Size size, Double priceMax,
            DayOfWeek weekday, Integer rating
    ) {
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        return driverDao.getSearchCount(zone, size, priceMax, weekday, rating);
    }

    @Transactional
    @Override
    public List<Booking> getBookings(Driver driver, BookingState state, int page) {
        if (state.equals(BookingState.PENDING) || state.equals(BookingState.REJECTED))
            bookingDao.checkPending();
        return bookingDao.getDriverBookings(driver, state, (page - 1) * Pagination.BOOKINGS_PAGE_SIZE);
    }

    @Transactional
    @Override
    public int getBookingCount(Driver driver, BookingState state) {
        return bookingDao.getDriverBookingCount(driver, state);
    }

    @Transactional
    @Override
    public Set<DayOfWeek> getWorkingDays(List<Vehicle> vehicles) {
        Set<DayOfWeek> days = new HashSet<>();
        for (Vehicle vehicle : vehicles) {
            for (Availability availability : vehicle.getAvailability()) {
                days.add(availability.getWeekDay());
                if (days.size() == DayOfWeek.values().length) {
                    return days;
                }
            }
        }
        return days;
    }

    @Transactional
    @Override
    public void acceptBooking(Driver driver, int bookingId) {
        Booking booking = getBookingById(driver, bookingId);
        if (!booking.getState().equals(BookingState.PENDING)) {
            throw new ForbiddenBookingStateOperationException();
        }
        bookingDao.acceptBooking(booking);
        mailService.sendAcceptedBooking(booking.getDate(), booking.getDriver().getUsername(), booking.getClient().getMail(),
                booking.getClient().getLanguage().getLocale());
        LOGGER.info("User {} accepted booking {}", driver.getUsername(), bookingId);
    }

    @Transactional
    @Override
    public void rejectBooking(Driver driver, int bookingId) {
        Booking booking = bookingDao.getDriverBookingById(driver, bookingId);
        if (!booking.getState().equals(BookingState.PENDING)) {
            throw new ForbiddenBookingStateOperationException();
        }
        bookingDao.rejectBooking(booking);
        mailService.sendRejectedBooking(
                booking.getDate(), booking.getDriver().getUsername(),
                booking.getClient().getMail(), booking.getClient().getLanguage().getLocale()
        );
        LOGGER.info("User {} rejected booking {}", driver.getUsername(), bookingId);
    }

    @Transactional
    @Override
    public void finishBooking(Driver driver, int bookingId) {
        Booking booking = bookingDao.getDriverBookingById(driver, bookingId);
        if (!booking.getState().equals(BookingState.ACCEPTED)) {
            throw new ForbiddenBookingStateOperationException();
        }
        bookingDao.finishBooking(booking);
        LOGGER.info("User {} finished booking {}", driver.getUsername(), bookingId);
    }

    @Transactional
    @Override
    public Booking cancelBooking(Driver driver, int bookingId) {
        Booking booking = super.cancelBooking(driver, bookingId);
        mailService.sendDriverCanceledBooking(
                booking.getDate(),
                booking.getClient().getUsername(),
                booking.getClient().getMail(),
                booking.getClient().getLanguage().getLocale()
        );
        return booking;
    }

    @Override
    public Optional<Vehicle> findVehicleById(Driver driver, int vehicleId) {
        return vehicleDao.findOwnedById(driver, vehicleId);
    }

    @Transactional
    @Override
    public Optional<Vehicle> findVehicleByPlateNumber(Driver driver, String plateNumber) {
        return vehicleDao.findByPlateNumber(driver, plateNumber);
    }

    @Transactional
    @Override
    public void updateVehicle(
            Driver driver,
            Integer vehicleId,
            String plateNumber,
            double volume,
            String description,
            List<Integer> zoneIds,
            double rate,
            Integer oldImgId,
            String imgFilename,
            byte[] imgData
    ) {
        Vehicle v = new Vehicle(vehicleId, driver, plateNumber, volume, description, oldImgId, rate);
        v.setZones(zoneDao.getZonesById(zoneIds));
        if (imgFilename != null && !imgFilename.isEmpty() && imgData != null && imgData.length > 0 && imgData.length < 10 * 1024 * 1024) {
            int imgId = imageDao.uploadVehicleImage(imgData, imgFilename, v);
            v.setImgId(imgId);
        }
        vehicleDao.updateVehicle(v);
        LOGGER.info("Driver {} updated vehicle {}", driver.getUsername(), v.getPlateNumber());
    }

    @Transactional
    @Override
    public void editProfile(Driver driver, String username, String mail, String description, String cbu, String language) {
        driverDao.editProfile(driver, username, mail, description, cbu, Language.valueOf(language));
        LOGGER.info("Driver {} updated it's profile", driver.getUsername());
    }

    @Transactional
    @Override
    public List<Vehicle> getVehicles(Driver driver, int page) {
        return vehicleDao.getDriverVehicles(driver, (page - 1) * Pagination.VEHICLES_PAGE_SIZE);
    }

    @Transactional
    @Override
    public int getVehicleCount(Driver driver) {
        return (int) Math.ceil((double) vehicleDao.getVehicleCount(driver) / Pagination.VEHICLES_PAGE_SIZE);
    }

    @Transactional
    @Override
    public void deleteVehicle(Vehicle vehicle) {
        vehicleDao.deleteVehicle(vehicle);
        LOGGER.info("Driver {} deleted it's vehicle {}", vehicle.getDriver().getUsername(), vehicle.getPlateNumber());
    }
}
