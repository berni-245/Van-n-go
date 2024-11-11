package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Availability;
import ar.edu.itba.paw.models.BookingState;
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
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:add_availability_data.sql")
public class AvailabilityJpaDaoTest {

    private static final Integer PREEXISTING_VEHICLE_ID = 1;
    private static final DayOfWeek WEDNESDAY = DayOfWeek.WEDNESDAY;
    private static final DayOfWeek THURSDAY = DayOfWeek.THURSDAY;
    private static final ShiftPeriod EVENING = ShiftPeriod.EVENING;

    private static final Map<DayOfWeek, ShiftPeriod[]> AVAILABILITY =
            Map.of(DayOfWeek.WEDNESDAY, new ShiftPeriod[]{ShiftPeriod.AFTERNOON, ShiftPeriod.EVENING},
                    DayOfWeek.THURSDAY, new ShiftPeriod[]{ShiftPeriod.MORNING, ShiftPeriod.AFTERNOON});
    private static final int AVAILABILITY_COUNT = 4;

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
        Availability availability = availabilityDao.create(vehicle, WEDNESDAY, EVENING);

        assertNotNull(availability);
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "vehicle_availability",
                String.format("""
                                id = '%d' and vehicle_id = '%d' and
                                week_day = '%s' and shift_period = '%s'""",
                        availability.getId(), PREEXISTING_VEHICLE_ID,
                        WEDNESDAY.name(), ShiftPeriod.EVENING.name()
                )
        ));

    }

    // Remember that the vehicle with PREEXISTING_VEHICLE_ID works friday all day (MORNING, AFTERNOON, EVENING)

    @Test
    public void testUpdate() {
        availabilityDao.updateVehicleAvailability(vehicle, AVAILABILITY);
        // Now we add 4 more availabilities and all previous ones are gone!
        em.flush();
        assertEquals(AVAILABILITY_COUNT, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "vehicle_availability",
                String.format("""
                                vehicle_id = '%d' and
                                week_day = '%s' or week_day = '%s'""",
                        PREEXISTING_VEHICLE_ID,
                        WEDNESDAY.name(), THURSDAY.name()
                )
        ));
    }
}
