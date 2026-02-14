package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Vehicle;

public interface ImageDao {
    Image getImage(int id);

    void uploadPop(byte[] bin, String fileName, Booking booking, String type);

    void uploadVehicleImage(byte[] bin, String fileName, Vehicle vehicle, String type);

    void uploadPfp(byte[] bin, String fileName, User user, String type);
}
