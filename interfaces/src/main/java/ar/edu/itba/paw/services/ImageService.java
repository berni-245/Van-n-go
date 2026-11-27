package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;

public interface ImageService {

    Image getImage(int imgId);

    void uploadPfp(User user, byte[] bin, String fileName, String type);

    void uploadPop(Client client, byte[] bin, String fileName, int bookingId, String type);
}
