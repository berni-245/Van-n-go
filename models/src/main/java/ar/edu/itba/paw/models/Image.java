package ar.edu.itba.paw.models;

public class Image {
    private final int id;
    private final byte[] data;
    private final String fileName;

    public Image(int id, byte[] data, String fileName) {
        this.id = id;
        this.data = data;
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }
}
