package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Zone;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class ZoneJpaDao implements ZoneDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Zone> getZone(long zoneId) {
        return Optional.ofNullable(em.find(Zone.class, zoneId));
    }

    @Override
    public boolean isValidZone(long zoneId) {
        return getZone(zoneId).isPresent();
    }

    @Override
    public List<Zone> getAllZones() {
        return em.createQuery("From Zone", Zone.class).getResultList();
    }

    @Override
    public List<Zone> getZonesById(List<Long> zoneIds) {
        return em.createQuery("From Zone z where z.id in :zoneIds", Zone.class)
                .setParameter("zoneIds", zoneIds)
                .getResultList();
    }
}
