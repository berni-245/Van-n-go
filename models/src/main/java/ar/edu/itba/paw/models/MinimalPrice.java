package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "minimal_price")
public class MinimalPrice {

    @Id
    @Column(name = "driver_id")
    private Integer driverId;

    @Column(name = "min")
    private Double min;

    public Integer getDriverId() {return driverId;}

    public Double getMin() {return min;}
}
