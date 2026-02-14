package ar.edu.itba.paw.models;

import javax.persistence.*;

@Table(name = "image")
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_id_seq")
    @SequenceGenerator(sequenceName = "image_id_seq", name = "image_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "bin", nullable = false)
    private byte[] data;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "type")
    private String type;

    public Image() {

    }

    public Image(Integer id, byte[] data, String fileName, String mimeType) {
        this.id = id;
        this.data = data;
        this.fileName = fileName;
        this.type = mimeType;
    }

    public Integer getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setData(byte[] imgData) {
        this.data = imgData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
