package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.InvalidImageException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageDao imgDao;
    private final DriverDao driverDao;
    private final ClientDao clientDao;
    private final VehicleDao vehicleDao;
    private final BookingDao bookingDao;
    private final MailService mailService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Autowired
    public ImageServiceImpl(final ImageDao imgDao, final DriverDao driverDao, final ClientDao clientDao,
                            final VehicleDao vehicleDao, final BookingDao bookingDao, final MailService mailService) {
        this.imgDao = imgDao;
        this.driverDao = driverDao;
        this.clientDao = clientDao;
        this.vehicleDao = vehicleDao;
        this.bookingDao = bookingDao;
        this.mailService = mailService;
    }

    @Transactional
    @Override
    public Image getImage(int imgId) {
        return imgDao.getImage(imgId);
    }

    @Transactional
    @Override
    public int uploadPfp(User user, byte[] bin, String fileName) {
        validateImage(fileName, bin);
        int toReturn = imgDao.uploadPfp(bin, fileName, user);
        LOGGER.info("Uploaded pfp for user {}", user.getId());
        return toReturn;
    }

    @Transactional
    @Override
    public int uploadPop(Client client, byte[] bin, String fileName, int bookingId) {
        validateImage(fileName, bin);
        Booking booking = bookingDao.getClientBookingById(client, bookingId);
        int toReturn = imgDao.uploadPop(bin, fileName, booking );
        mailService.sendReceivedPop(client.getUsername(), booking.getDriver().getMail(), booking.getDate(), booking.getDriver().getLanguage().getLocale());
        LOGGER.info("Uploaded proof of payment pfp for booking {}", bookingId);
        return toReturn;
    }

    private void validateImage(String filename, byte[] bin) {
        if (bin == null || bin.length > 10 * 1024 * 1024 || filename == null || filename.isEmpty())
            throw new InvalidImageException();
    }
}
