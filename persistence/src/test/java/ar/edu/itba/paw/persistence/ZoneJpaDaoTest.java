package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Zone;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:add_client_data.sql")
public class ZoneJpaDaoTest {
    private static final Integer ADROGUE_ZONE_ID = 1;
    private static final Integer PALERMO_ZONE_ID = 2;
    private static final Integer NON_EXISTING_ZONE_ID = -1;
    private static final Integer CLIENT_ID_WITH_ZONE = 500;

    private static Zone adrogue;
    private static Zone palermo;
    private static Client clientWithZone;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ZoneJpaDao zoneDao;

    @Before
    public void setUp() {
        adrogue = em.find(Zone.class, ADROGUE_ZONE_ID);
        palermo = em.find(Zone.class, PALERMO_ZONE_ID);
        clientWithZone = em.find(Client.class, CLIENT_ID_WITH_ZONE);
    }

    @Test
    public void testGetZone() {
        Optional<Zone> optionalZone = zoneDao.getZone(ADROGUE_ZONE_ID);
        assertTrue(optionalZone.isPresent());

        Zone zone = optionalZone.get();
        assertEquals(adrogue, zone);
    }

    @Test
    public void testGetZoneFail() {
        Optional<Zone> optionalZone = zoneDao.getZone(NON_EXISTING_ZONE_ID);
        assertTrue(optionalZone.isEmpty());
    }

    @Test
    public void testIsValidZone() {
        assertTrue(zoneDao.isValidZone(ADROGUE_ZONE_ID));
    }

    @Test
    public void testIsNotValidZone() {
        assertFalse(zoneDao.isValidZone(NON_EXISTING_ZONE_ID));
    }

    @Test
    public void testGetClientZone() {
        assertEquals(adrogue, zoneDao.getClientZone(clientWithZone));
    }

    @Test
    public void testGetZonesById() {
        List<Zone> zones = zoneDao.getZonesById(List.of(ADROGUE_ZONE_ID, PALERMO_ZONE_ID));
        assertTrue(zones.stream().anyMatch(zone -> zone.equals(adrogue)));
        assertTrue(zones.stream().anyMatch(zone -> zone.equals(palermo)));
    }
}
