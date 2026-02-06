package ar.edu.itba.paw.webapp.dto.user;

import ar.edu.itba.paw.webapp.validation.ValidZoneId;

public class CreateClientDTO extends CreateUserDTO {
    @ValidZoneId
    private Integer zoneId;

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

}
