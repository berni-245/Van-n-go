package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;

public interface ImageService {
    Image getPfp(int userId);

    Image getVehicleImage(int vehicleId);

    Image getPop(int bookingId);

    int uploadPfp(byte[] bin, String fileName, int userId);

    int uploadVehicleImage(byte[] bin, String fileName, int vehicleId);

    int uploadPop(byte[] bin, String fileName, int bookingId);
}
