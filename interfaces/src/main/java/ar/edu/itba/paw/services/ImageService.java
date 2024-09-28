package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;

public interface ImageService {
    public Image getPfp(int userId);

    public Image getVehicleImage(int vehicleId);

    public Image getPop(int driverId, int bookingId);

    public int uploadPfp(byte[] bin, String fileName, int userId);

    public int uploadVehicleImage(byte[] bin, String fileName, int vehicleId);

    public int uploadPop(byte[] bin, String fileName, int driverId, int bookingId);
}
