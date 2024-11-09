package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;

public interface ImageDao {
    Image getImage(int id);

    int uploadPop(byte[] bin, String fileName, Booking booking);

    int uploadVehicleImage(byte[] bin, String fileName, Vehicle vehicle);

    int uploadPfp(byte[] bin, String fileName, User user);
}
