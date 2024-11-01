package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.Rates;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.models.Zone;
import ar.edu.itba.paw.webapp.validation.ValidPlateNumber;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;

@ValidPlateNumber
public class VehicleForm {
    @Nullable
    @Pattern(regexp = "([a-zA-Z]{3}[0-9]{3})|([a-zA-Z]{2}[0-9]{3}[a-zA-Z]{2})")
    private String ogPlateNumber;

    @Pattern(regexp = "([a-zA-Z]{3}[0-9]{3})|([a-zA-Z]{2}[0-9]{3}[a-zA-Z]{2})")
    private String plateNumber;

    @Min(Size.MIN)
    @Max(Size.MAX)
    private double volume;

    @Length(max = 40)
    private String description;

    @Min(Rates.MINIMUM)
    @Max(Rates.MAXIMUM)
    private double rate;

    private List<Long> zoneIds;

    private long id;

    private Long imgId;

    public void setAll(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.description = vehicle.getDescription();
        this.volume = vehicle.getVolume();
        this.plateNumber = vehicle.getPlateNumber();
        this.zoneIds = vehicle.getZones().stream().map(Zone::getId).toList();
        this.rate = vehicle.getHourlyRate();
        this.imgId = vehicle.getImgId();
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getOgPlateNumber() {
        return ogPlateNumber;
    }

    public void setOgPlateNumber(String ogPlateNumber) {
        this.ogPlateNumber = ogPlateNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }

    public List<Long> getZoneIds() {
        return zoneIds;
    }

    public void setZoneIds(List<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }
}
