/*
package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.ShiftPeriod;
import ar.edu.itba.paw.models.Vehicle;
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
import java.time.DayOfWeek;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "add_availability_data.sql")
public class AvailabilityJpaDaoTest {
    private static final Integer AVAILABILITY_ID_NOT_CREATED = 10;

    private static final Integer PREEXISTING_VEHICLE_ID = 1;
    private static final DayOfWeek WEDNESDAY = DayOfWeek.WEDNESDAY;
    private static final ShiftPeriod EVENING = ShiftPeriod.EVENING;

    private static Vehicle vehicle;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AvailabilityJpaDao availabilityDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        vehicle = em.find(Vehicle.class, PREEXISTING_VEHICLE_ID);
    }

    @Test
    public void testCreate() {
        availabilityDao.create(vehicle, WEDNESDAY, EVENING);


    }
}
*/
