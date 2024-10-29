package ar.edu.itba.paw.models;

import com.google.gson.Gson;

import javax.persistence.*;
import java.util.List;

@Table(name = "vehicle")
@Entity
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_id_seq")
    @SequenceGenerator(sequenceName = "vehicle_id_seq", name = "vehicle_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "driver_id", nullable = false)
    private long driverId;
    @Column(name = "plate_number", nullable = false)
    private String plateNumber;
    @Column(name = "volume_m3", nullable = false)
    private double volume;
    @Column(nullable = false)
    private String description;
    @Column(name = "img_id", nullable = false)
    private Integer imgId;
    @Column(name = "hourly_rate", nullable = false)
    private double hourlyRate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "vehicle_zone",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "zone_id")
    )
    private List<Zone> zones;

    // private Map<WeekDay, List<Availability>> weeklyAvailability
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Availability> availabilitiy;

    private static final Gson gson = new Gson();

    Vehicle() {}

    public Vehicle(
            Long id,
            long driverId,
            String plateNumber,
            double volume,
            String description,
            Integer imgId,
            double hourlyRate
    ) {
        this.id = id;
        this.driverId = driverId;
        this.plateNumber = plateNumber;
        this.volume = volume;
        this.description = description;
        this.imgId = imgId;
        this.hourlyRate = hourlyRate;
    }

    public Vehicle(
            long driverId,
            String plateNumber,
            double volume,
            String description,
            Integer imgId,
            double hourlyRate
    ) {
        this(null, driverId, plateNumber, volume, description, imgId, hourlyRate);
    }

    public long getId() {
        return id;
    }

    public long getDriverId() {
        return driverId;
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

    public Integer getImgId() {
        return imgId;
    }

    public void setImgId(Integer imgId) {
        this.imgId = imgId;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public List<Availability> getAvailabilitiy() {
        return availabilitiy;
    }

    public String toJson() {
        return gson.toJson(this);
    }
}
