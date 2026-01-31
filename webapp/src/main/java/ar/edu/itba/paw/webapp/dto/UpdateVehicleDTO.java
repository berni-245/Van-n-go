package ar.edu.itba.paw.webapp.dto;

public class UpdateVehicleDTO {
    private String plateNumber;
    private Double volumeM3;
    private Double hourlyRate;
    private String description;

    public UpdateVehicleDTO() {

    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getPlateNumberOr(String other) {
        if (plateNumber == null)
            return other;
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Double getVolumeM3() {
        return volumeM3;
    }

    public Double getVolumeM3Or(Double other) {
        if (volumeM3 == null)
            return other;
        return volumeM3;
    }

    public void setVolumeM3(Double volumeM3) {
        this.volumeM3 = volumeM3;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public Double getHourlyRateOr(Double other) {
        if (hourlyRate == null)
            return other;
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionOr(String other) {
        if (description == null)
            return other;
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
