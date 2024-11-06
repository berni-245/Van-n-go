package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.InvalidImageException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);
    private final ImageDao imgDao;
    private final DriverDao driverDao;
    private final ClientDao clientDao;
    private final VehicleDao vehicleDao;
    private final BookingDao bookingDao;

    @Autowired
    public ImageServiceImpl(final ImageDao imgDao, final DriverDao driverDao, final ClientDao clientDao,
                            final VehicleDao vehicleDao, final BookingDao bookingDao) {
        this.imgDao = imgDao;
        this.driverDao = driverDao;
        this.clientDao = clientDao;
        this.vehicleDao = vehicleDao;
        this.bookingDao = bookingDao;
    }

    @Override
    public Image getImage(long imgId) {
        return imgDao.getImage(imgId);
    }

    @Transactional
    @Override
    public long uploadPfp(byte[] bin, String fileName, long userId) {
        validateImage(fileName,bin);
        Optional<Driver> driver = driverDao.findById(userId);
        if (driver.isPresent()) {
            return imgDao.uploadPfp(bin, fileName, driver.get());
        }
        Optional<Client> client = clientDao.findById(userId);
        if (client.isPresent()) {
            return imgDao.uploadPfp(bin, fileName, client.get());
        }

        throw new UserNotFoundException();
    }

    @Transactional
    @Override
    public long uploadVehicleImage(byte[] bin, String fileName, long vehicleId) {
        validateImage(fileName,bin);
        return imgDao.uploadVehicleImage(bin, fileName, vehicleDao.findById(vehicleId).orElseThrow());
    }

    @Transactional
    @Override
    public long uploadPop(byte[] bin, String fileName, long bookingId) {
        validateImage(fileName,bin);
        return imgDao.uploadPop(bin, fileName, bookingDao.getBookingById(bookingId).orElseThrow());
    }

    private void validateImage(String filename, byte[] bin) {
        if(bin == null || bin.length > 10*1024*1024 || filename == null || filename.isEmpty())
            throw new InvalidImageException();
    }
}
