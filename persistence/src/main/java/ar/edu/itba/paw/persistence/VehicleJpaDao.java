package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.models.Zone;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public class VehicleJpaDao implements VehicleDao {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public Vehicle create(long driverId, String plateNumber, double volume, String description, double hourlyRate) {
        Driver driver = em.find(Driver.class, driverId);
        Vehicle v = new Vehicle(driver, plateNumber, volume, description, null, hourlyRate);
        em.persist(v);
        return v;
    }

    @Override
    public Optional<Vehicle> findById(long id) {
        return Optional.ofNullable(em.find(Vehicle.class, id));
    }

    @Override
    public Optional<Vehicle> findByPlateNumber(Driver driver, String plateNumber) {
        TypedQuery<Vehicle> query = em.createQuery(
                "from Vehicle v where v.driver = :driver and v.plateNumber = :plateNumber",
                Vehicle.class
        );
        query.setParameter("driver", driver);
        query.setParameter("plateNumber", plateNumber);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Vehicle> getDriverVehicles(Driver driver) {
        TypedQuery<Vehicle> query = em.createQuery(
                "from Vehicle v where v.driver = :driver",
                Vehicle.class
        );
        query.setParameter("driver", driver);
        return query.getResultList();
    }

    @Override
    public List<Vehicle> getDriverVehicles(Driver driver, Zone zone, Size size) {
        TypedQuery<Vehicle> query = em.createQuery(
                """
                        from Vehicle v where v.driver = :driver
                        and :zone member of v.zones and
                        v.volume between :minVolume and :maxVolume""",
                Vehicle.class
        );
        query.setParameter("driver", driver);
        query.setParameter("zone", zone);
        query.setParameter("minVolume", (double) size.getMinVolume());
        query.setParameter("maxVolume", (double) size.getMaxVolume());
        return query.getResultList();
    }

    @Override
    public boolean plateNumberExists(String plateNumber) {
        TypedQuery<Vehicle> query = em.createQuery(
                "from Vehicle v where v.plateNumber = :plateNumber",
                Vehicle.class
        );
        query.setParameter("plateNumber", plateNumber);
        return !query.getResultList().isEmpty();
    }

    @Transactional
    @Override
    public void updateVehicle(Driver driver, Vehicle vehicle) {
        em.merge(vehicle);
    }
}
