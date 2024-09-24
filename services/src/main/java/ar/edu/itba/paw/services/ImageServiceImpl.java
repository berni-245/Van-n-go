package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageDao imgDao;

    @Autowired
    public ImageServiceImpl(final ImageDao imgDao) {
        this.imgDao = imgDao;
    }
    //Returns image id, used to refer to the image later
    @Override
    public int saveImage(MultipartFile imgFile) throws IOException {
        String fileName = imgFile.getOriginalFilename();
        byte[] imgData = imgFile.getBytes();
        return imgDao.uploadImage(fileName, imgData);
    }

    @Override
    public Image getImage(int id) {
        return imgDao.getImage(id);
    }
}
