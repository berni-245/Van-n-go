package ar.edu.itba.paw.models;

import com.google.gson.Gson;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Table(name = "vehicle")
@Entity
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_id_seq")
    @SequenceGenerator(sequenceName = "vehicle_id_seq", name = "vehicle_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Driver driver;
    @Column(name = "plate_number", nullable = false)
    private String plateNumber;
    @Column(name = "volume_m3", nullable = false)
    private double volume;
    @Column(nullable = false)
    private String description;
    @Column(name = "img_id", nullable = false)
    private Long imgId;
    @Column(name = "hourly_rate", nullable = false)
    private double hourlyRate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "vehicle_zone",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "zone_id")
    )
    private List<Zone> zones;

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    // private Map<WeekDay, List<Availability>> weeklyAvailability
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Availability> availabilitiy;

    private static final Gson gson = new Gson();

    Vehicle() {
    }

    public Vehicle(
            Long id,
            Driver driver,
            String plateNumber,
            double volume,
            String description,
            Long imgId,
            double hourlyRate
    ) {
        this.id = id;
        this.driver = driver;
        this.plateNumber = plateNumber;
        this.volume = volume;
        this.description = description;
        this.imgId = imgId;
        this.hourlyRate = hourlyRate;
    }

    public Vehicle(
            Driver driver,
            String plateNumber,
            double volume,
            String description,
            Long imgId,
            double hourlyRate
    ) {
        this(null, driver, plateNumber, volume, description, imgId, hourlyRate);
    }

    public long getId() {
        return id;
    }

    public Driver getDriver() {
        return driver;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public double getVolume() {
        return volume;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "%s||%s||%.2f".formatted(plateNumber, description, volume);
    }

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public List<Availability> getAvailabilitiy() {
        return availabilitiy;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vehicle vehicle && vehicle.id.equals(id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Size getSize() { //revisar.. no es muy lindo

        if (volume >= Size.SMALL.getMinVolume() && volume < Size.MEDIUM.getMinVolume()) {
            return Size.SMALL;
        } else if (volume >= Size.MEDIUM.getMinVolume() && volume < Size.LARGE.getMinVolume()) {
            return Size.MEDIUM;
        } else if (volume >= Size.LARGE.getMinVolume() && volume <= Size.LARGE.getMaxVolume()) {
            return Size.LARGE;
        }
        throw new IllegalArgumentException("Volume fuera del rango permitido");

    }
}
