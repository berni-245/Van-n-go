package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.Rates;
import ar.edu.itba.paw.models.SearchOrder;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.webapp.validation.ValidZoneId;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;

public class AvailabilitySearchForm {
    @ValidZoneId
    @NotNull
    private Integer zoneId;

    @Nullable
    private Size size;

    @Nullable
    @Min(0)
    @Max(5)
    private Integer rating;

    @Nullable
    @Min(Rates.MINIMUM)
    private Double priceMin;

    @Nullable
    @Max(Rates.MAXIMUM)
    private Double priceMax;

    @Nullable
    private DayOfWeek weekday;

    @Nullable
    private SearchOrder order;

    public AvailabilitySearchForm(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
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

    public SearchOrder getOrder() {return order;}

    public void setOrder(SearchOrder order) {this.order = order;}
}
