package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public class DriverJpaDao implements DriverDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Driver create(String username, String mail, String password, String description) {
        Driver driver = new Driver(username, mail, password, description, null, null);
        em.persist(driver);
        return driver;
    }

    @Override
    public Optional<Driver> findById(long id) {
        return Optional.ofNullable(em.find(Driver.class, id));
    }

    @Override
    public List<Driver> getAll(Zone zone, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating, int offset) {
        StringBuilder queryString = new StringBuilder("""
            SELECT DISTINCT v.driver FROM Vehicle v
            JOIN v.zones z
            LEFT JOIN v.availability a
            WHERE z = :zone
        """);
        if(size != null)
            queryString.append(" AND v.volume BETWEEN :minVolume AND :maxVolume");
        if(priceMin != null)
            queryString.append(" AND v.hourlyRate >= :priceMin");
        if(priceMax != null)
            queryString.append(" AND v.hourlyRate <= :priceMax");
        if (weekday != null)
            queryString.append(" AND a.weekDay = :weekday");
        if(rating != null)
            queryString.append(" AND v.driver.rating >= :rating");

        TypedQuery<Driver> query = em.createQuery(queryString.toString(), Driver.class);
        query.setParameter("zone", zone);
        if(size != null) {
            query.setParameter("minVolume", (double) size.getMinVolume());
            query.setParameter("maxVolume", (double) size.getMaxVolume());
        }
        if(priceMin != null)
            query.setParameter("priceMin", priceMin);
        if(priceMax != null)
            query.setParameter("priceMax", priceMax);
        if(weekday != null)
            query.setParameter("weekday", weekday);
        if(rating != null)
            query.setParameter("rating", rating);

        query.setFirstResult(offset);
        query.setMaxResults(Pagination.SEARCH_PAGE_SIZE);
        return query.getResultList();
    }



    @Override
    public Optional<Driver> findByUsername(String username) {
        final TypedQuery<Driver> query = em.createQuery("from Driver as d where d.username = :username", Driver.class);
        query.setParameter("username", username);
        final List<Driver> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.getFirst());
    }

    @Transactional
    @Override
    public void editProfile(Driver driver, String username, String mail, String description, String cbu) {
        driver.setUsername(username);
        driver.setMail(mail);
        driver.setDescription(description);
        driver.setCbu(cbu);
        em.merge(driver);

    }

    //TODO: Ver una forma de comprimir estos dos metodos
    @Override
    public int getSearchCount(Zone zone, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating) {
        StringBuilder queryString = new StringBuilder("""
        SELECT COUNT(DISTINCT v.driver) FROM Vehicle v
        JOIN v.zones z
        LEFT JOIN v.availability a
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
            query.setParameter("rating", rating);
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
