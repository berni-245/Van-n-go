package ar.edu.itba.paw.exceptions;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException() {
        super("No image was found");
    }
}
