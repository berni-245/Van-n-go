package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.webapp.validation.ValidZoneId;
import org.springframework.lang.Nullable;

public class AvailabilitySearchForm {
    @ValidZoneId
    @Nullable
    private Long zoneId;

    private Size size;

    public AvailabilitySearchForm(Long zoneId) {
        this.zoneId = zoneId;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }
}
