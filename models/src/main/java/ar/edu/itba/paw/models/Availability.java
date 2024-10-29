package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.DayOfWeek;

@Entity
@Table(
        name = "vehicle_availability",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"vehicle_id", "week_day", "shift_period"}
        )
)
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_availability_id_seq")
    @SequenceGenerator(
            sequenceName = "vehicle_availability_id_seq",
            name = "vehicle_availability_id_seq",
            allocationSize = 1
    )
    private long id;

//    @JoinColumn(name = "vehicle_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Vehicle vehicle;   // on delete cascade?

    @Column(name = "week_day", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek weekDay;//int not null,

    @Column(name = "shift_period", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShiftPeriod shiftPeriod; //not null,

    Availability() {
    }

    public Availability(Vehicle vehicle, DayOfWeek weekDay, ShiftPeriod shiftPeriod) {
        this.vehicle = vehicle;
        this.weekDay = weekDay;
        this.shiftPeriod = shiftPeriod;
    }

    public long getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public DayOfWeek getWeekDay() {
        return weekDay;
    }

    public ShiftPeriod getShiftPeriod() {
        return shiftPeriod;
    }
}
