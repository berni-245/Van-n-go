package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class DriverJpaDao extends UserJpaDao<Driver> implements DriverDao {

    @Override
    public Driver create(String username, String mail, String password, String description, Language language) {
        Driver driver = new Driver(username, mail, password, description, null, null, language);
        em.persist(driver);
        return driver;
    }

    @Override
    public Driver findById(int id) {
        Driver d = em.find(Driver.class, id);
        if (d == null) throw new UserNotFoundException();
        return d;
    }

    @Override
    public Optional<Driver> findByUsername(String username) {
        final TypedQuery<Driver> query = em.createQuery(
                "from Driver as d where d.username = :username",
                Driver.class
        );
        query.setParameter("username", username);
        final List<Driver> list = query.getResultList();
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.getFirst());
    }

    private List<Integer> getSearchResultIds(
            Zone zone, Size size, Double priceMax,
            DayOfWeek weekday, Integer rating, SearchOrder order, Integer offset
    ) {
        StringBuilder idQuery = new StringBuilder("""
                    SELECT v.driver_id FROM vehicle v
                    JOIN driver d ON d.id = v.driver_id
                    JOIN app_user au ON v.driver_id = au.id
                    JOIN vehicle_zone z ON v.id = z.vehicle_id
                    JOIN minimal_price m ON m.driver_id = v.driver_id
                    JOIN Vehicle_availability a ON v.id = a.vehicle_id
                    WHERE z.zone_id = :zoneId
                """);

        if (size != null) idQuery.append(" AND v.volume_m3 BETWEEN :minVolume AND :maxVolume");
        if (priceMax != null) idQuery.append(" AND v.hourly_rate <= :priceMax");
        if (weekday != null) idQuery.append(" AND a.week_day = :weekday");
        if (rating != null) idQuery.append(" AND d.rating >= :rating");
        idQuery.append(" GROUP BY v.driver_id");
        if (order != null) {
            switch (order) {
                case LEAST_PRICE -> idQuery.append(", m.min ORDER BY m.min");
                case BEST_RATING -> idQuery.append(", d.rating ORDER BY d.rating DESC");
                case ALPHABETICAL -> idQuery.append(", au.username ORDER BY au.username");
            }
        }

        Query idNativeQuery = em.createNativeQuery(idQuery.toString());

        idNativeQuery.setParameter("zoneId", zone.getId());
        if (size != null) {
            idNativeQuery.setParameter("minVolume", (double) size.getMinVolume());
            idNativeQuery.setParameter("maxVolume", (double) size.getMaxVolume());
        }
        if (priceMax != null) idNativeQuery.setParameter("priceMax", priceMax);
        if (weekday != null) idNativeQuery.setParameter("weekday", weekday.name());
        if (rating != null) idNativeQuery.setParameter("rating", rating.doubleValue());

        if (offset != null) {
            idNativeQuery.setFirstResult(offset);
            idNativeQuery.setMaxResults(Pagination.SEARCH_PAGE_SIZE);
        }

        @SuppressWarnings("unchecked")
        List<Integer> driverIds = idNativeQuery.getResultList();
        return driverIds;
    }

    @Override
    public List<Driver> getSearchResults(
            Zone zone, Size size, Double priceMax,
            DayOfWeek weekday, Integer rating, SearchOrder order, int offset
    ) {
        List<Integer> driverIds = getSearchResultIds(
                zone, size, priceMax, weekday, rating, order, offset
        );
        if (driverIds.isEmpty()) return Collections.emptyList();
        StringBuilder driverQuery = new StringBuilder("SELECT d FROM Driver d JOIN MinimalPrice mp ON mp.driverId = d.id WHERE d.id IN :driverIds");
        if (order != null) {
            switch (order) {
                case LEAST_PRICE -> driverQuery.append(" ORDER BY mp.min");
                case BEST_RATING -> driverQuery.append(" ORDER BY d.rating DESC");
                case ALPHABETICAL -> driverQuery.append(" ORDER BY d.username");
            }
        }

        TypedQuery<Driver> finalQuery = em.createQuery(driverQuery.toString(), Driver.class);
        finalQuery.setParameter("driverIds", driverIds);

        return finalQuery.getResultList();
    }

    @Override
    public int getSearchCount(Zone zone, Size size, Double priceMax, DayOfWeek weekday, Integer rating) {
        return getSearchResultIds(
                zone,
                size,
                priceMax,
                weekday,
                rating,
                null,
                null
        ).size();
    }

    @Override
    public void editProfile(Driver driver, String username, String mail, String description, String cbu, Language language) {
        super.editProfile(driver, username, mail,language);
        driver.setDescription(description);
        driver.setCbu(cbu);
        em.merge(driver);

    }

    @Override
    public boolean mailExists(String mail) {
        final TypedQuery<Driver> query = em.createQuery(
                "from Driver as d where d.mail = :mail",
                Driver.class
        );
        query.setParameter("mail", mail);
        return !query.getResultList().isEmpty();
    }
}
