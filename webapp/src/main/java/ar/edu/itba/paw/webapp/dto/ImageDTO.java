package ar.edu.itba.paw.webapp.dto;

public class ImageDTO {
    private String fileName;
    private byte[] imageData;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}
