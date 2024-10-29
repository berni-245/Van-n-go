package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Availability;
import ar.edu.itba.paw.models.ShiftPeriod;
import ar.edu.itba.paw.models.Vehicle;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.DayOfWeek;
import java.util.Optional;

@Repository
public class AvailabilityJpaDao implements AvailabilityDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Availability create(Vehicle vehicle, DayOfWeek weekDay, ShiftPeriod period) {
        Availability av = new Availability(vehicle, weekDay, period);
        em.persist(av);
        return av;
    }
}
