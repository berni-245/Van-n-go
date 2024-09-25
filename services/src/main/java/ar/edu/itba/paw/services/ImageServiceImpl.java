package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);
    private final ImageDao imgDao;

    @Autowired
    public ImageServiceImpl(final ImageDao imgDao) {
        this.imgDao = imgDao;
    }

    private int saveImage(byte[] bin, String fileName) {
        return imgDao.uploadImage(fileName, bin);
    }

    private Image getImage(int id) {
        return imgDao.getImage(id);
    }


    @Override
    public Image getPfp(int userId) {
        return imgDao.getpfp(userId);
    }

    @Override
    public Image getVehicleImage(int vehicleId) {
        return imgDao.getVehicleImage(vehicleId);
    }

    @Override
    public Image getPop(int driverId, int bookingId) {
        return imgDao.getPop(driverId, bookingId);
    }

    @Override
    public int uploadPfp(byte[] bin, String fileName, int userId) {
        return imgDao.uploadPfp(bin,fileName,userId);
    }

    @Override
    public int uploadVehicleImage(byte[] bin, String fileName, int vehicleId) {
        return imgDao.uploadVehicleImage(bin, fileName,vehicleId);
    }

    @Override
    public int uploadPop(byte[] bin, String fileName, int driverId, int bookingId) {
        return imgDao.uploadPop(bin,fileName,driverId,bookingId);
    }
}
