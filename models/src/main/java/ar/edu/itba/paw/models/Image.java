package ar.edu.itba.paw.models;

public class Image {
    private final long id;
    private final byte[] data;
    private final String fileName;

    public Image(long id, byte[] data, String fileName) {
        this.id = id;
        this.data = data;
        this.fileName = fileName;
    }

    public long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }
}
