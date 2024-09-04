package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.models.WeeklyAvailability;
import ar.edu.itba.paw.persistence.DriverDao;
import ar.edu.itba.paw.persistence.VehicleDao;
import ar.edu.itba.paw.persistence.WeeklyAvailabilityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {
    @Autowired
    private DriverDao driverDao;

    @Autowired
    private VehicleDao vehicleDao;

    @Autowired
    private WeeklyAvailabilityDao weeklyAvailabilityDao;

    public DriverServiceImpl(final DriverDao driverDao) {
        this.driverDao = driverDao;
    }

    public Driver create(String username, String mail, String extra1) {
        // Register user
        // TODO
        // 1. Validate inputs
        // 2. Store in database
        // 3. Generate validation token and store in db
        // 4. Send validation token in a welcome email
        // 5. Add user to manual verification queue
        // 6. ... Whatever else the app needs
        return driverDao.create(username, mail, extra1);
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
}
