package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.persistence.DriverDao;
import ar.edu.itba.paw.persistence.VehicleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {
    @Autowired
    private DriverDao driverDao;

    @Autowired
    private VehicleDao vehicleDao;

    public DriverServiceImpl(final DriverDao driverDao) {
        this.driverDao = driverDao;
    }

    @Override
    public Optional<Driver> findById(long id) {
        return driverDao.findById(id);
    }

    @Override
    public Vehicle addVehicle(long driverId, String plateNumber, double volume, String description) {
        return vehicleDao.create(driverId, plateNumber, volume, description);
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
}
