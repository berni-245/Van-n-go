package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    public Image getPfp(int userId);
    public Image getVehicleImage(int vehicleId);
    public Image getPop(int driverId, int bookingId);
    public int uploadPfp(MultipartFile file,int userId);
    public int uploadVehicleImage(MultipartFile file,int vehicleId);
    public int uploadPop(MultipartFile file,int driverId, int bookingId);
}
