package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;

public interface ImageDao {
    Image getImage(long id);

    Integer uploadImage(String fileName, byte[] imgData);

    Image getpfp(long userId);

    Image getVehicleImage(long vehicleId);

    Image getPop(long bookingId);

    long uploadPop(byte[] bin, String fileName, long bookingId);

    long uploadVehicleImage(byte[] bin, String fileName, long vehicleId);

    long uploadPfp(byte[] bin, String fileName, long userId);
}
