package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);
    private final ImageDao imgDao;

    @Autowired
    public ImageServiceImpl(final ImageDao imgDao) {
        this.imgDao = imgDao;
    }

    private int saveImage(MultipartFile imgFile) throws IOException {
        String fileName = imgFile.getOriginalFilename();
        byte[] imgData = imgFile.getBytes();
        return imgDao.uploadImage(fileName, imgData);
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
    public int uploadPfp(MultipartFile file, int userId){
        int aux = -1;
        try {
            aux = imgDao.uploadPfp(file, userId);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return aux;
    }

    @Override
    public int uploadVehicleImage(MultipartFile file, int vehicleId){
        int aux = -1;
        try {
            aux = imgDao.uploadVehicleImage(file, vehicleId);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return aux;
    }

    @Override
    public int uploadPop(MultipartFile file, int driverId, int bookingId){
        int aux = -1;
        try {
            aux = imgDao.uploadPop(file, driverId, bookingId);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return aux;
    }
}
