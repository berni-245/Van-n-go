package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class DriverJpaDao implements DriverDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Driver create(String username, String mail, String password, String extra1) {
        Driver driver = new Driver(username, mail, password, extra1, null, null);
        em.persist(driver);
        return driver;
    }

    @Override
    public Optional<Driver> findById(long id) {
        return Optional.ofNullable(em.find(Driver.class, id));
    }

    @Override
    public List<Driver> getAll() { //TODO: paginar
        return em.createQuery("From Driver", Driver.class)
                .getResultList();
    }


    @Override
    public List<Driver> getAll(Zone zone, Size size, int offset) {
        TypedQuery<Driver> query = em.createQuery("""
                SELECT v.driver FROM Vehicle v  JOIN v.zones z WHERE z = :zone AND v.volume BETWEEN :minVolume AND :maxVolume
""", Driver.class);
        query.setParameter("zone", zone);
        query.setParameter("minVolume", (double) size.getMinVolume());
        query.setParameter("maxVolume", (double) size.getMaxVolume());
        query.setFirstResult(offset);
        query.setMaxResults(Pagination.SEARCH_PAGE_SIZE);
        return query.getResultList();
    }

    @Override
    public void updateDriverRating(long driverId) {

    }

    @Override
    public Optional<Driver> findByUsername(String username) {
        final TypedQuery<Driver> query = em.createQuery("from Driver as d where d.username = :username", Driver.class);
        query.setParameter("username", username);
        final List<Driver> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.getFirst());
    }

    @Override
    public void editProfile(long id, String extra1, String cbu) {
        Optional<Driver> driver = findById(id);
        if (driver.isPresent()) {
            Driver d = driver.get();
            d.setExtra1(extra1);
            d.setCbu(cbu);
            em.persist(d);
        }
    }

    @Override
    public int getSearchCount(Zone zone, Size size) {
        Long count = em.createQuery("""
            SELECT COUNT(v.driver) FROM Vehicle v JOIN v.zones z WHERE z = :zone AND v.volume BETWEEN :minVolume AND :maxVolume
            """, Long.class)
                .setParameter("zone", zone)
                .setParameter("minVolume", (double) size.getMinVolume())
                .setParameter("maxVolume", (double) size.getMaxVolume())
                .getSingleResult();

        return count.intValue();
    }
}
