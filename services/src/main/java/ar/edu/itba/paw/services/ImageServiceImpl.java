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
    private final ImageDao imgDao;
    private final DriverDao driverDao;
    private final ClientDao clientDao;
    private final VehicleDao vehicleDao;
    private final BookingDao bookingDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Autowired
    public ImageServiceImpl(final ImageDao imgDao, final DriverDao driverDao, final ClientDao clientDao,
                            final VehicleDao vehicleDao, final BookingDao bookingDao) {
        this.imgDao = imgDao;
        this.driverDao = driverDao;
        this.clientDao = clientDao;
        this.vehicleDao = vehicleDao;
        this.bookingDao = bookingDao;
    }

    @Transactional
    @Override
    public Image getImage(int imgId) {
        return imgDao.getImage(imgId);
    }

    @Transactional
    @Override
    public int uploadPfp(byte[] bin, String fileName, int userId) {
        validateImage(fileName,bin);
        Optional<Driver> driver = driverDao.findById(userId);
        if (driver.isPresent()) {
            int toReturn = imgDao.uploadPfp(bin, fileName, driver.get());
            LOGGER.info("Uploaded pfp for driver {}", userId);
            return toReturn;
        }
        Optional<Client> client = clientDao.findById(userId);
        if (client.isPresent()) {
            int toReturn = imgDao.uploadPfp(bin, fileName, client.get());
            LOGGER.info("Uploaded pfp for client {}", userId);
            return toReturn;
        }

        throw new UserNotFoundException();
    }

    @Transactional
    @Override
    public int uploadPop(byte[] bin, String fileName, int bookingId) {
        validateImage(fileName,bin);
        int toReturn = imgDao.uploadPop(bin, fileName, bookingDao.getBookingById(bookingId).orElseThrow());
        LOGGER.info("Uploaded proof of payment pfp for booking {}", bookingId);
        return toReturn;
    }

    private void validateImage(String filename, byte[] bin) {
        if(bin == null || bin.length > 10*1024*1024 || filename == null || filename.isEmpty())
            throw new InvalidImageException();
    }
}
