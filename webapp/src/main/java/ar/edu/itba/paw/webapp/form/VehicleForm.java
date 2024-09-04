package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class VehicleForm {
    // This should be country dependant I think?
    // @Size(min = 8, max = 20)
    @Pattern(regexp = "([a-zA-Z]{3}[0-9]{3})|([a-zA-Z]{2}[0-9]{3}[a-zA-Z]{2})",
            message = "Debe ingresar una patente válida")
    @NotBlank
    private String plateNumber;

    @Min(1)
    private double volume;

    private String description;

    public @NotBlank String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(@NotBlank String plateNumber) {
        this.plateNumber = plateNumber;
    }

    @Min(1)
    public double getVolume() {
        return volume;
    }

    public void setVolume(@Min(1) double volume) {
        this.volume = volume;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
