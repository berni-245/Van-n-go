package ar.edu.itba.paw.webapp.dto;

public class UpdateClientDTO extends UpdateUserDTO {
    private Integer zoneId;

    public UpdateClientDTO() {

    }

    public Integer getZoneId() {
        return zoneId;
    }

    public Integer getZoneIdOr(Integer other) {
        if (zoneId == null)
            return other;
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }
}
