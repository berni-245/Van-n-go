package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public class VehicleJpaDao implements VehicleDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Vehicle create(
            Driver driver,
            String plateNumber,
            double volume,
            String description,
            double hourlyRate
    ) {
        Vehicle v = new Vehicle(driver, plateNumber.toUpperCase(), volume, description, null, hourlyRate);
        em.persist(v);
        return v;
    }

    @Override
    public Optional<Vehicle> findById(int id) {
        return Optional.ofNullable(em.find(Vehicle.class, id));
    }

    @Override
    public Optional<Vehicle> findOwnedById(Driver driver, int id) {
        TypedQuery<Vehicle> query = em.createQuery(
                "from Vehicle v where v.driver = :driver and v.id = :id",
                Vehicle.class
        );
        query.setParameter("driver", driver);
        query.setParameter("id", id);
        return query.getResultList().stream().findFirst();
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
    public List<Vehicle> getDriverVehicles(Driver d, Zone z, Size size, Double priceMax, DayOfWeek weekday, int offset, boolean mustHaveAvailability) {
        StringBuilder nativeSql = new StringBuilder("""
                SELECT DISTINCT v.id
                FROM vehicle v
                LEFT JOIN vehicle_zone vz ON v.id = vz.vehicle_id
                LEFT JOIN vehicle_availability a ON v.id = a.vehicle_id
                WHERE v.driver_id = :driverId
                  AND v.plate_number IS NOT NULL
                """);
        if (z != null)
            nativeSql.append(" AND vz.zone_id = :zoneId");
        if (size != null)
            nativeSql.append(" AND v.volume_m3 BETWEEN :minVol AND :maxVol");
        if (priceMax != null)
            nativeSql.append(" AND v.hourly_rate <= :priceMax");
        if (weekday != null) {
            nativeSql.append(" AND a.week_day = :weekday ");
        } else if (mustHaveAvailability) {
            nativeSql.append(" AND a.id IS NOT NULL ");
        }
        Query nativeQuery = em.createNativeQuery(nativeSql.toString())
                .setParameter("driverId", d.getId());
        if (z != null)
            nativeQuery.setParameter("zoneId", z.getId());
        nativeQuery.setFirstResult(offset)
                .setMaxResults(Pagination.VEHICLES_PAGE_SIZE);
        if (size != null) {
            nativeQuery.setParameter("minVol", size.getMinVolume());
            nativeQuery.setParameter("maxVol", size.getMaxVolume());
        }
        if (priceMax != null)
            nativeQuery.setParameter("priceMax", priceMax);
        if (weekday != null)
            nativeQuery.setParameter("weekday", weekday.name());
        var ids = nativeQuery.getResultList();

        TypedQuery<Vehicle> query = em.createQuery(
                "from Vehicle v where id in (:vehicleIds) order by v.plateNumber",
                Vehicle.class
        );
        query.setParameter("vehicleIds", ids);
        return query.getResultList();
    }

    private List<Integer> getIds(Driver d, Zone z, Size size, Double priceMax, DayOfWeek weekday, int offset, boolean mustHaveAvailability) {
        StringBuilder nativeSql = new StringBuilder("""
                SELECT DISTINCT v.id
                FROM vehicle v
                LEFT JOIN vehicle_zone vz ON v.id = vz.vehicle_id
                LEFT JOIN vehicle_availability a ON v.id = a.vehicle_id
                WHERE v.driver_id = :driverId
                  AND v.plate_number IS NOT NULL
                """);
        if (z != null)
            nativeSql.append(" AND vz.zone_id = :zoneId");
        if (size != null)
            nativeSql.append(" AND v.volume_m3 BETWEEN :minVol AND :maxVol");
        if (priceMax != null)
            nativeSql.append(" AND v.hourly_rate <= :priceMax");
        if (weekday != null) {
            nativeSql.append(" AND a.week_day = :weekday ");
        } else if (mustHaveAvailability) {
            nativeSql.append(" AND a.id IS NOT NULL ");
        }
        Query nativeQuery = em.createNativeQuery(nativeSql.toString())
                .setParameter("driverId", d.getId());
        if (z != null)
            nativeQuery.setParameter("zoneId", z.getId());
        nativeQuery.setFirstResult(offset)
                .setMaxResults(Pagination.VEHICLES_PAGE_SIZE);
        if (size != null) {
            nativeQuery.setParameter("minVol", size.getMinVolume());
            nativeQuery.setParameter("maxVol", size.getMaxVolume());
        }
        if (priceMax != null)
            nativeQuery.setParameter("priceMax", priceMax);
        if (weekday != null)
            nativeQuery.setParameter("weekday", weekday.name());
        return nativeQuery.getResultList();
        // TODO terminar
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

    @Override
    public void updateVehicle(Vehicle vehicle) {
        em.merge(vehicle);
    }

    @Override
    public void deleteVehicle(Vehicle vehicle) {
        vehicle.invalidatePlateNumber();
        em.merge(vehicle);
    }

    @Override
    public int getVehicleCount(Driver driver) {
        return em.createQuery("""
                        select count(*) from Vehicle v
                        where v.driver = :driver and v.plateNumber IS NOT NULL
                        """, Long.class)
                .setParameter("driver", driver)
                .getSingleResult().intValue();
    }
}
