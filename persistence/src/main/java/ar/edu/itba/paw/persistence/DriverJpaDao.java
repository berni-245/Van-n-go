package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class DriverJpaDao implements DriverDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Driver create(String username, String mail, String password, String description, Language language) {
        Driver driver = new Driver(username, mail, password, description, null, null, language);
        em.persist(driver);
        return driver;
    }

    @Override
    public Optional<Driver> findById(long id) {
        return Optional.ofNullable(em.find(Driver.class, id));
    }

    @Override
    public List<Driver> getSearchResults(Zone zone, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating, SearchOrder order, int offset) {
        StringBuilder idQuery = new StringBuilder("""
            SELECT v.driver_id FROM Vehicle v
            JOIN driver d ON d.id = v.driver_id
            JOIN app_user au ON v.driver_id = au.id
            JOIN vehicle_zone z ON v.id = z.vehicle_id
            JOIN minimal_price m ON m.driver_id = v.driver_id
            JOIN Vehicle_availability a ON v.id = a.vehicle_id
            WHERE z.zone_id = :zoneId
        """);

        if (size != null) idQuery.append(" AND v.volume_m3 BETWEEN :minVolume AND :maxVolume");
        if (priceMin != null) idQuery.append(" AND v.hourly_rate >= :priceMin");
        if (priceMax != null) idQuery.append(" AND v.hourly_rate <= :priceMax");
        if (weekday != null) idQuery.append(" AND a.week_day = :weekday");
        if (rating != null) idQuery.append(" AND d.rating >= :rating");
        idQuery.append(" GROUP BY v.driver_id");
        if(order != null){
            switch (order) {
                case PRICE -> idQuery.append(", m.min ORDER BY m.min");
                case RATING -> idQuery.append(", d.rating ORDER BY d.rating DESC");
                case RECENT -> idQuery.append(", d.lastUpdate ORDER BY d.lastUpdate DESC");
                case ALPHABETICAL -> idQuery.append(", au.username ORDER BY au.username");
            }
        }

        Query idNativeQuery = em.createNativeQuery(idQuery.toString());

        idNativeQuery.setParameter("zoneId", zone.getId());
        if (size != null) {
            idNativeQuery.setParameter("minVolume", (double) size.getMinVolume());
            idNativeQuery.setParameter("maxVolume", (double) size.getMaxVolume());
        }
        if (priceMin != null) idNativeQuery.setParameter("priceMin", priceMin);
        if (priceMax != null) idNativeQuery.setParameter("priceMax", priceMax);
        if (weekday != null) idNativeQuery.setParameter("weekday", weekday.name());
        if (rating != null) idNativeQuery.setParameter("rating", rating.doubleValue());

        idNativeQuery.setFirstResult(offset);
        idNativeQuery.setMaxResults(Pagination.SEARCH_PAGE_SIZE);

        List<Integer> driverIds = idNativeQuery.getResultList();
        if (driverIds.isEmpty()) return Collections.emptyList();
        //JOIN MinimalPrice mp ON d.id = mp.driverId
        StringBuilder driverQuery = new StringBuilder("""
            SELECT d FROM Driver d
            
            WHERE d.id IN :driverIds
        """);

        if (order != null) {
            switch (order) {
                case PRICE -> {}//driverQuery.append(" ORDER BY mp.min");
                case RATING -> driverQuery.append(" ORDER BY d.rating DESC");
                case RECENT -> driverQuery.append(" ORDER BY d.lastUpdate DESC");
                case ALPHABETICAL -> driverQuery.append(" ORDER BY d.username");
            }
        }

        @SuppressWarnings("unchecked")
        List<Long> driverIdLong = driverIds.stream().map(Integer::longValue).toList();
        TypedQuery<Driver> finalQuery = em.createQuery(driverQuery.toString(), Driver.class);
        finalQuery.setParameter("driverIds", driverIdLong);

        return finalQuery.getResultList();
    }



    @Override
    public Optional<Driver> findByUsername(String username) {
        final TypedQuery<Driver> query = em.createQuery("from Driver as d where d.username = :username", Driver.class);
        query.setParameter("username", username);
        final List<Driver> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.getFirst());
    }

    @Override
    public void editProfile(Driver driver, String username, String mail, String description, String cbu) {
        driver.setUsername(username);
        driver.setMail(mail);
        driver.setDescription(description);
        driver.setCbu(cbu);
        em.merge(driver);

    }

    @Override
    public int getSearchCount(Zone zone, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating) {
        StringBuilder queryString = new StringBuilder("""
                    SELECT COUNT(DISTINCT v.driver) FROM Vehicle v
                    JOIN v.zones z
                    JOIN v.availability a
                    WHERE z = :zone
                """);

        // Add optional conditions
        if (size != null) {
            queryString.append(" AND v.volume BETWEEN :minVolume AND :maxVolume");
        }
        if (priceMin != null) {
            queryString.append(" AND v.hourlyRate >= :priceMin");
        }
        if (priceMax != null) {
            queryString.append(" AND v.hourlyRate <= :priceMax");
        }
        if (weekday != null) {
            queryString.append(" AND a.weekDay = :weekday");
        }
        if (rating != null) {
            queryString.append(" AND v.driver.rating >= :rating");
        }

        TypedQuery<Long> query = em.createQuery(queryString.toString(), Long.class);
        query.setParameter("zone", zone);

        // Set parameters conditionally
        if (size != null) {
            query.setParameter("minVolume", (double) size.getMinVolume());
            query.setParameter("maxVolume", (double) size.getMaxVolume());
        }
        if (priceMin != null) {
            query.setParameter("priceMin", priceMin);
        }
        if (priceMax != null) {
            query.setParameter("priceMax", priceMax);
        }
        if (weekday != null) {
            query.setParameter("weekday", weekday);
        }
        if (rating != null) {
            query.setParameter("rating", rating.doubleValue());
        }

        Long count = query.getSingleResult();
        return count.intValue();
    }


    @Override
    public List<DayOfWeek> getDriverWeekDaysOnZoneAndSize(Driver driver, Zone zone, Size size) {

        TypedQuery<DayOfWeek> query = em.createQuery("""
                select a.weekDay from Driver d join Vehicle v join Availability a where :zone in v.zones and v.volume BETWEEN :minVolume AND :maxVolume and d = :driver
                """, DayOfWeek.class);
        query.setParameter("zone", zone);
        query.setParameter("driver", driver);
        query.setParameter("minVolume", (double) size.getMinVolume());
        query.setParameter("maxVolume", (double) size.getMaxVolume());
        return query.getResultList();
    }
}
