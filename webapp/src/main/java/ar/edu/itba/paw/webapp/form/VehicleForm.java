package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.webapp.validation.ValidPlateNumber;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

public class VehicleForm {
    // This should be country dependant I think?
    // @Size(min = 8, max = 20)
    @Pattern(regexp = "([a-zA-Z]{3}[0-9]{3})|([a-zA-Z]{2}[0-9]{3}[a-zA-Z]{2})")
    @ValidPlateNumber
    private String plateNumber;

    @Min(Size.MIN)
    @Max(Size.MAX)
    private double volume;

    @Length(max = 30)
    private String description;

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
}
