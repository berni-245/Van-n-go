package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.persistence.VehicleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {
    @Autowired
    private VehicleDao vehicleDao;

    @Override
    public boolean plateNumberExists(String plateNumber) {
        return vehicleDao.plateNumberExists(plateNumber);
    }
}
