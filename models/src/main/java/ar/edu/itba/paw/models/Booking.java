package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Optional;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_id_seq")
    @SequenceGenerator(sequenceName = "booking_id_seq", name = "booking_id_seq", allocationSize = 1)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Vehicle vehicle;

    @JoinColumn(name = "origin_zone_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Zone originZone;
    @JoinColumn(name = "destination_zone_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Zone destinationZone;

    @Column(nullable = false)
    private LocalDate date;
    @Column(name = "shift_period")
    @Enumerated(EnumType.STRING)
    private ShiftPeriod shiftPeriod;
    @Column
    @Enumerated(EnumType.STRING)
    private BookingState state;
    @Column
    private Integer rating;
    @Column
    private String review;
    @Column(name = "proof_of_payment")
    private Integer pop;
    @Column(name = "job_description")
    private String jobDescription;

    Booking() {
    }

    public Booking(Integer id, Client client,  Vehicle vehicle,
                   Zone originZone, Zone destinationZone, LocalDate date, ShiftPeriod shiftPeriod,
                   BookingState state, Integer rating,
                   String review, Integer pop, String jobDescription) {
        this.id = id;
        this.client = client;
        this.vehicle = vehicle;
        this.originZone = originZone;
        this.destinationZone = destinationZone;
        this.date = date;
        this.shiftPeriod = shiftPeriod;
        this.state = state;
        this.rating = rating;
        this.review = review;
        this.pop = pop;
        this.jobDescription = jobDescription;
    }

    public Booking(Client client,  Vehicle vehicle,
                   Zone originZone, Zone destinationZone, LocalDate date, ShiftPeriod shiftPeriod,
                   BookingState state, String jobDescription) {
        this.client = client;
        this.vehicle = vehicle;
        this.originZone = originZone;
        this.destinationZone = destinationZone;
        this.date = date;
        this.shiftPeriod = shiftPeriod;
        this.state = state;
        this.jobDescription = jobDescription;
    }

    public Integer getId() {
        return id;
    }

    public Driver getDriver() {
        return vehicle.getDriver();
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Zone getOriginZone() {
        return originZone;
    }

    public LocalDate getDate() {
        return date;
    }

    public ShiftPeriod getShiftPeriod() {
        return shiftPeriod;
    }

    public BookingState getState() {
        return state;
    }

    public void setState(BookingState state) {
        this.state = state;
    }

    public Client getClient() {
        return client;
    }

    public Integer getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public Zone getDestinationZone() {
        return destinationZone;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public Integer getPop() {
        return pop;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setPop(Integer pop) {
        this.pop = pop;
    }

    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
}
