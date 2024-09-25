package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageDao {
    Image getImage(int id);
    /*Returns the Id of the image as stored in the database.
     *this is so that the image isn't unnecessarily loaded
     *into memory during runtime */
    Integer uploadImage(String fileName, byte[] imgData);

    Image getpfp(int userId);

    Image getVehicleImage(int vehicleId);

    Image getPop(int driverId, int bookingId);

    int uploadPfp(MultipartFile file, int userId) throws IOException;

    int uploadVehicleImage(MultipartFile file, int vehicleId) throws IOException;

    int uploadPop(MultipartFile file, int driverId, int bookingId) throws IOException;
}
