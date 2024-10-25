package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class DriverJpaDao implements DriverDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Driver create(String username, String mail, String password, String extra1) {
        Driver driver = new Driver(username,mail,password,extra1,null,null);
        em.persist(driver);
        return driver;
    }

    @Override
    public Optional<Driver> findById(long id) {
        return Optional.ofNullable(em.find(Driver.class, id));
    }

    @Override
    public List<Driver> getAll() {
        return List.of();
    }


    @Override
    public List<Driver> getAll(Long zoneId, Size size, int offset) {
        return List.of();
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
        if(driver.isPresent()) {
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
