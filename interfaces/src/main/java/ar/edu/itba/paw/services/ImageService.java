package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;

public interface ImageService {

    Image getImage(int imgId);

    int uploadPfp(User user, byte[] bin, String fileName);

    int uploadPop(Client client, byte[] bin, String fileName, int bookingId);
}
