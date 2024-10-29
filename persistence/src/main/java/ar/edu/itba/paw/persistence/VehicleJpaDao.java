package ar.edu.itba.paw.persistence;

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
        Vehicle v = new Vehicle(driverId, plateNumber, volume, description, null, hourlyRate);
        em.persist(v);
        return v;
    }

    @Override
    public Optional<Vehicle> findById(long id) {
        return Optional.ofNullable(em.find(Vehicle.class, id));
    }

    @Override
    public Optional<Vehicle> findByPlateNumber(long driverId, String plateNumber) {
        TypedQuery<Vehicle> query = em.createQuery(
                "from Vehicle v where v.driverId = :driverId and v.plateNumber = :plateNumber",
                Vehicle.class
        );
        query.setParameter("driverId", driverId);
        query.setParameter("plateNumber", plateNumber);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Vehicle> getDriverVehicles(long driverId) {
        TypedQuery<Vehicle> query = em.createQuery(
                "from Vehicle v where v.driverId = :driverId",
                Vehicle.class
        );
        query.setParameter("driverId", driverId);
        return query.getResultList();
    }

    @Override
    public List<Vehicle> getDriverVehicles(long driverId, Zone zone, Size size) {
        TypedQuery<Vehicle> query = em.createQuery(
                """
                        from Vehicle v where v.driverId = :driverId
                        and :zone in v.zones and
                        v.volume between :minVolume and :maxVolume""",
                Vehicle.class
        );
        query.setParameter("driverId", driverId);
        query.setParameter("zone", zone);
        query.setParameter("minVolume", size.getMinVolume());
        query.setParameter("maxVolume", size.getMaxVolume());
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

    @Override
    public boolean updateVehicle(long driverId, String plateNumber, double volume, String description, double rate, long vehicleId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
