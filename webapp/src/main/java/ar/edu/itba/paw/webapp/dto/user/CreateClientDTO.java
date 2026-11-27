package ar.edu.itba.paw.webapp.dto.user;

import ar.edu.itba.paw.webapp.validation.ValidZoneId;

import javax.validation.constraints.NotNull;

public class CreateClientDTO extends CreateUserDTO {
    @ValidZoneId
    @NotNull
    private Integer zoneId;

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

}
