package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    int saveImage(MultipartFile imgFile) throws IOException;
    public Image getImage(int id);
}
