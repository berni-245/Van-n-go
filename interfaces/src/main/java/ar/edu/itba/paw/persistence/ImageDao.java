package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;

public interface ImageDao {
    Image getImage(int id);

    Integer uploadImage(String fileName, byte[] imgData);

    Image getpfp(int userId);

    Image getVehicleImage(int vehicleId);

    Image getPop(int bookingId);

    int uploadPop(byte[] bin, String fileName, int bookingId);

    int uploadVehicleImage(byte[] bin, String fileName, int vehicleId);

    int uploadPfp(byte[] bin, String fileName, int userId);
}
