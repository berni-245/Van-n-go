package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Availability;
import ar.edu.itba.paw.models.ShiftPeriod;
import ar.edu.itba.paw.models.Vehicle;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.DayOfWeek;
import java.util.Map;

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

    @Override
    public void updateVehicleAvailability(Vehicle vehicle, Map<DayOfWeek, ShiftPeriod[]> periods) {
        em.createQuery("delete from Availability av where av.vehicle = :vehicle")
                .setParameter("vehicle", vehicle)
                .executeUpdate();
        for (DayOfWeek day : periods.keySet()) {
            for (ShiftPeriod period : periods.get(day)) {
                create(vehicle, day, period);
            }
        }
    }
}
