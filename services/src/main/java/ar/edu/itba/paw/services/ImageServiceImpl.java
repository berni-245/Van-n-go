package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.InvalidImageException;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);
    private final ImageDao imgDao;

    @Autowired
    public ImageServiceImpl(final ImageDao imgDao) {
        this.imgDao = imgDao;
    }

    @Override
    public Image getImage(long imgId) {
        return imgDao.getImage(imgId);
    }

    @Transactional
    @Override
    public long uploadPfp(byte[] bin, String fileName, long userId) {
        validateImage(fileName,bin);
        return imgDao.uploadPfp(bin, fileName, userId);
    }

    @Transactional
    @Override
    public long uploadVehicleImage(byte[] bin, String fileName, long vehicleId) {
        validateImage(fileName,bin);
        return imgDao.uploadVehicleImage(bin, fileName, vehicleId);
    }

    @Transactional
    @Override
    public long uploadPop(byte[] bin, String fileName, long bookingId) {
        validateImage(fileName,bin);
        return imgDao.uploadPop(bin, fileName, bookingId);
    }

    private void validateImage(String filename, byte[] bin) {
        if(bin == null || bin.length < 0 || bin.length > 10*1024*1024 || filename == null || !filename.isEmpty())
            throw new InvalidImageException();
    }
}
