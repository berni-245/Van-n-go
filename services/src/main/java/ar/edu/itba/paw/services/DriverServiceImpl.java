package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.*;
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

    private final VehicleService vehicleService;

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
            VehicleService vehicleService,
            PasswordEncoder passwordEncoder
    ) {
        super(userDao, passwordEncoder, mailService, bookingDao);
        this.driverDao = driverDao;
        this.vehicleDao = vehicleDao;
        this.imageDao = imageDao;
        this.zoneDao = zoneDao;
        this.availabilityDao = availabilityDao;
        this.vehicleService = vehicleService;
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
    public Driver create(String username, String mail, String password, String description, String cbu, Locale locale) {
        checkRepeatedUser(username, mail);
        Driver driver = driverDao.create(username, mail, passwordEncoder.encode(password), description, cbu, Language.fromLocale(locale));
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
            double rate
    ) {
        if (vehicleDao.getVehicleCount(driver) >= MAX_VEHICLES_PER_DRIVER) {
            throw new DriverVehicleLimitReachedException(driver);
        }
        if (vehicleService.plateNumberExists(plateNumber)) {
            throw new PlateNumberAlreadyExistsException();
        }
        Vehicle v = vehicleDao.create(
                driver, plateNumber, volume, description, rate
        );
        LOGGER.info("{} added a vehicle with plate {}", driver, plateNumber);
        return v;
    }

    @Transactional
    @Override
    public List<Vehicle> getVehicles(
            Driver driver,
            Integer zoneId,
            Size size,
            Double priceMax,
            DayOfWeek weekday,
            int page,
            boolean mustHaveAvailability
    ) {
        Zone zone = null;
        if (zoneId != null)
            zone = zoneDao.getZone(zoneId).orElseThrow(ZoneNotFoundException::new);
        return vehicleDao.getDriverVehicles(
                driver, zone, size, priceMax, weekday,
                (page - 1) * Pagination.VEHICLES_PAGE_SIZE, mustHaveAvailability
        );
    }

    @Transactional
    @Override
    public void updateAvailability(
            Vehicle vehicle,
            List<Integer> zoneIds,
            Map<DayOfWeek, ShiftPeriod[]> periods
    ) {
        vehicle.setZones(zoneDao.getZonesById(zoneIds));
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
        Zone zone = zoneDao.getZone(zoneId).orElseThrow(ZoneNotFoundException::new);
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
        if (plateNumber == null)
            return Optional.empty();
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
            double rate
    ) {
        Optional<Vehicle> oldVehicle = vehicleDao.findById(vehicleId);
        if (oldVehicle.isEmpty() || oldVehicle.get().isDeleted())
            throw new VehicleNotFoundException();

        if (plateNumber != null && vehicleService.plateNumberExists(plateNumber) && !oldVehicle.get().getPlateNumber().equals(plateNumber)) {
            throw new PlateNumberAlreadyExistsException();
        }
        Vehicle v = new Vehicle(vehicleId, driver, plateNumber, volume, description, null, rate);
        v.setImgId(oldVehicle.get().getImgId());
        vehicleDao.updateVehicle(v);
        LOGGER.info("Driver {} updated vehicle {}", driver.getUsername(), v.getPlateNumber());
    }

    @Transactional
    @Override
    public void updatePicture(
            Vehicle vehicle,
            String imgFilename,
            byte[] imgData,
            String type
    ) {
        Optional<Vehicle> oldVehicle = vehicleDao.findById(vehicle.getId());
        if (oldVehicle.isEmpty() || oldVehicle.get().isDeleted())
            throw new VehicleNotFoundException();
        Vehicle v = oldVehicle.get();
        imageDao.uploadVehicleImage(imgData, imgFilename, v, type);
    }

    @Transactional
    @Override
    public void editProfile(Driver driver, String username, String mail, String description, String cbu, String language) {
        driverDao.editProfile(driver, username, mail, description, cbu, Language.valueOf(language));
        LOGGER.info("Driver {} updated it's profile", driver.getUsername());
    }

    @Transactional
    @Override
    public int getVehicleCount(Driver driver,
                               Integer zoneId,
                               Size size,
                               Double priceMax,
                               DayOfWeek weekday,
                               boolean mustHaveAvailability
    ) {
        Zone zone = null;
        if (zoneId != null)
            zone = zoneDao.getZone(zoneId).orElseThrow(ZoneNotFoundException::new);
        return vehicleDao.getVehicleCount(driver, zone, size, priceMax, weekday, mustHaveAvailability);
    }

    @Transactional
    @Override
    public void deleteVehicle(Vehicle vehicle) {
        // TODO change these getBookings to database operations, idk if this getBookings brings all the vehicle bookings to memory.
        Optional<Booking> optional = vehicle.getBookings().stream()
                .filter(b -> b.getState().equals(BookingState.ACCEPTED))
                .findFirst();
        if (optional.isPresent())
            throw new VehicleHasAcceptedJobsException();
        vehicleDao.deleteVehicle(vehicle);
        vehicle.getBookings().stream()
                .filter(b -> b.getState().equals(BookingState.PENDING))
                .forEach(b -> rejectBooking(b.getDriver(), b.getId()));
        LOGGER.info("Driver {} deleted it's vehicle {}", vehicle.getDriver().getUsername(), vehicle.getPlateNumber());
    }

    @Transactional
    @Override
    public void setNewBookingState(Driver user, int bookingId, BookingState newState) {
        switch (newState) {
            case CANCELED -> cancelBooking(user, bookingId);
            case ACCEPTED -> acceptBooking(user, bookingId);
            case REJECTED -> rejectBooking(user, bookingId);
            case FINISHED -> finishBooking(user, bookingId);
            default -> throw new ForbiddenBookingStateOperationException();
        }
    }

}
