package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.webapp.validation.ValidZoneId;
import org.springframework.lang.Nullable;

import java.time.DayOfWeek;

public class AvailabilitySearchForm {
    @ValidZoneId
    @Nullable
    private Long zoneId;

    private Size size;

    private Integer rating;

    private Double priceMin;

    private Double priceMax;

    private DayOfWeek weekday;

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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Double getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(Double priceMin) {
        this.priceMin = priceMin;
    }

    public Double getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(Double priceMax) {
        this.priceMax = priceMax;
    }

    public DayOfWeek getWeekday() {
        return weekday;
    }

    public void setWeekday(DayOfWeek weekday) {
        this.weekday = weekday;
    }
}
