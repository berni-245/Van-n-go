package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;

public interface ImageService {
    Image getPfp(long userId);

    Image getVehicleImage(long vehicleId);

    Image getPop(long bookingId);

    long uploadPfp(byte[] bin, String fileName, long userId);

    long uploadVehicleImage(byte[] bin, String fileName, long vehicleId);

    long uploadPop(byte[] bin, String fileName, long bookingId);
}
