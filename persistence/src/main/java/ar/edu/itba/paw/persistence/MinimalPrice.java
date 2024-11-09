package ar.edu.itba.paw.persistence;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Immutable
@Subselect("SELECT driver_id, MIN(hourly_rate) FROM vehicle GROUP BY driver_id")
public class MinimalPrice {

    @Id
    @Column(name = "driver_id")
    private Integer driverId;

    @Column(name = "min")
    private Double min;

    public Integer getDriverId() {return driverId;}

    public Double getMin() {return min;}
}
