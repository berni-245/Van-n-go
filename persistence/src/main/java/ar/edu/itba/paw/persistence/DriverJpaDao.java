package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.Zone;
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
                From Driver as d join Vehicle v where :zone in v.zones and
                        v.volume between :minVolume and :maxVolume""", Driver.class);
        query.setParameter("zone", zone);
        query.setParameter("minVolume", size.getMinVolume());
        query.setParameter("maxVolume", size.getMaxVolume());
        return query.getResultList();
    }

    @Override
    public void updateDriverRating(long driverId) {

    }

    @Override
    public Optional<Driver> findByUsername(String username) {
        final TypedQuery<Driver> query = em.createQuery("from Driver as u where u.username = :username", Driver.class);
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
    public int getSearchCount(long zoneId, Size size) {
        return 0;
    }
}
