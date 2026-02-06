package ar.edu.itba.paw.webapp.dto.vehicle;

import ar.edu.itba.paw.models.Rates;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.webapp.validation.ValidPlateNumber;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;


public class CreateVehicleDTO {

    @Pattern(regexp = "([a-zA-Z]{3}[0-9]{3})|([a-zA-Z]{2}[0-9]{3}[a-zA-Z]{2})")
    @ValidPlateNumber
    @NotNull
    private String plateNumber;

    @Min(Size.MIN)
    @Max(Size.MAX)
    @NotNull
    private Double volumeM3;

    @Min(Rates.MINIMUM)
    @Max(Rates.MAXIMUM)
    @NotNull
    private Double hourlyRate;

    @NotBlank
    @Length(max = 255)
    private String description;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public double getVolumeM3() {
        return volumeM3;
    }

    public void setVolumeM3(double volumeM3) {
        this.volumeM3 = volumeM3;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
