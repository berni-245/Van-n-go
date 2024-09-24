package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageDao {
    Image getImage(int id);
    /*Returns the Id of the image as stored in the database.
     *this is so that the image isn't unnecessarily loaded
     *into memory during runtime */
    Integer uploadImage(String fileName, byte[] imgData);
}
