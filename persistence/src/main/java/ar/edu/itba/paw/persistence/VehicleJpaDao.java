package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class VehicleJpaDao implements VehicleDao {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public Vehicle create(
            Driver driver,
            String plateNumber,
            double volume,
            String description,
            List<Zone> zones,
            double hourlyRate
    ) {
        Vehicle v = new Vehicle(driver, plateNumber.toUpperCase(), volume, description, null, hourlyRate);
        v.setZones(zones);
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
                "from Vehicle v where v.driver = :driver order by v.plateNumber",
                Vehicle.class
        );
        query.setParameter("driver", driver);
        return query.getResultList();
    }

    @Override
    public List<Vehicle> getDriverVehicles(Driver driver, Zone zone, Size size, Double priceMin, Double priceMax, DayOfWeek weekday) {
        StringBuilder queryString = new StringBuilder("""
                        SELECT DISTINCT v FROM Vehicle v
                        JOIN v.zones z
                        LEFT JOIN v.availability a
                        WHERE v.driver = :driver AND z = :zone
                        """);
        if(size != null)
            queryString.append(" AND v.volume BETWEEN :minVol AND :maxVol");
        if(priceMin != null)
            queryString.append(" AND v.hourlyRate >= :priceMin");
        if(priceMax != null)
            queryString.append(" AND v.hourlyRate <= :priceMax");
        if(weekday != null)
            queryString.append(" AND a.weekDay = :weekday");
        TypedQuery<Vehicle> query = em.createQuery(queryString.toString(), Vehicle.class);
        query.setParameter("driver", driver);
        query.setParameter("zone", zone);
        if(size != null){
            query.setParameter("minVol", (double) size.getMinVolume());
            query.setParameter("maxVol", (double) size.getMaxVolume());
        }
        if(priceMin != null){
            query.setParameter("priceMin", priceMin);
        }
        if(priceMax != null){
            query.setParameter("priceMax", priceMax);
        }
        if(weekday != null){
            query.setParameter("weekday", weekday);
        }
        return query.getResultList();
    }

    @Override
    public boolean plateNumberExists(String plateNumber) {
        TypedQuery<Vehicle> query = em.createQuery(
                "from Vehicle v where v.plateNumber = :plateNumber",
                Vehicle.class
        );
        query.setParameter("plateNumber", plateNumber.toUpperCase());
        return !query.getResultList().isEmpty();
    }

    @Transactional
    @Override
    public void updateVehicle(Vehicle vehicle) {
        em.merge(vehicle);
    }

    @Transactional
    @Override
    public void deleteVehicle(Vehicle vehicle) {
        em.remove(vehicle);
    }

    @Override
    public List<Vehicle> getDriverVehicles(Driver driver, int offset) {
        List<Long> vehicleIds = em.createQuery("""
            select v.id from Vehicle v
            where v.driver = :driver
            order by v.plateNumber
            """, Long.class).setParameter("driver", driver)
                .setFirstResult(offset)
                .setMaxResults(Pagination.VEHICLES_PAGE_SIZE)
                .getResultList();

        TypedQuery<Vehicle> query = em.createQuery(
                "from Vehicle v where id in (:vehicleIds) order by v.plateNumber",
                Vehicle.class
        );
        query.setParameter("vehicleIds", vehicleIds);
        return query.getResultList();
    }

    @Override
    public long getVehicleCount(Driver driver) {
        return em.createQuery("""
                    select count(*) from Vehicle v
                    where v.driver = :driver
                    """, Long.class)
                .setParameter("driver", driver)
                .getSingleResult();
    }
}
