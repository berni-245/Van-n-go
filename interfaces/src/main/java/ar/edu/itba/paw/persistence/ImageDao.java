package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;

public interface ImageDao {
    Image getImage(long id);

    long uploadPop(byte[] bin, String fileName, Booking booking);

    long uploadVehicleImage(byte[] bin, String fileName, Vehicle vehicle);

    long uploadPfp(byte[] bin, String fileName, User user);
}
