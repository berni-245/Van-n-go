package ar.edu.itba.paw.services;

import ar.edu.itba.paw.persistence.VehicleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleDao vehicleDao;

    @Autowired
    public VehicleServiceImpl(VehicleDao vehicleDao) {
        this.vehicleDao = vehicleDao;
    }

    @Override
    public boolean plateNumberExists(String plateNumber) {
        return vehicleDao.plateNumberExists(plateNumber);
    }
}
