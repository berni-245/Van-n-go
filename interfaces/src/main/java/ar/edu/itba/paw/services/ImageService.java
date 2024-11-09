package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;

public interface ImageService {

    Image getImage(int imgId);

    int uploadPfp(byte[] bin, String fileName, int userId);

    int uploadPop(byte[] bin, String fileName, int bookingId);
}
