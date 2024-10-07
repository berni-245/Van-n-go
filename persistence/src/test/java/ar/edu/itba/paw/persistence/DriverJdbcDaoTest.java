package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(scripts = {
        "classpath:db_init.sql", // Adds constant data to tables
        "classpath:add_availability_data.sql" // There will be a vehicle with id 1, who works from 0-4am, on wednesday and thursday in the zone with id 1
})
public class DriverJdbcDaoTest {
    // This booking will accepted and for ve.id = 1, zone.id = 1 on thursday, between 1-3am
    private static final long PREEXISTING_ACCEPTED_BOOK = 500;
    // This booking will pending and for ve.id = 1, zone.id = 1, on friday, between 1-3am
    private static final long PREEXISTING_PENDING_BOOK = 501;
    private static final long ANOTHER_PREEXISTING_PENDING_BOOK = 502;
    private static final long BOOKING_COUNT = 3;


    private static final long VEHICLE_ID = 1;
    private static final long CLIENT_ID = 1;
    private static final long CLIENT_ID_TWO = 2;
    private static final long DRIVER_ID = 3;
    private static final long NEW_DRIVER = 420;
    private static final long ZONE_ID = 1;
    private static final long ZONE_THAT_DRIVER_DOES_NOT_WORK = 2;
    private static final LocalDate DATE_FREE_TO_APPOINT = LocalDate.of(2030, 5, 1);
    private static final LocalDate ALREADY_ACCEPTED_BOOKED_DATE = LocalDate.of(2030, 5, 2);
    private static final LocalDate ALREADY_PENDING_BOOKED_DATE = LocalDate.of(2030, 5, 3);
    private static final int RATING = 5;

    @Autowired
    private DriverJdbcDao driverDao;
    @Test
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {jdbcTemplate = new JdbcTemplate(ds);}

    @Test
    public void testFindById(){
        Optional<Driver> driver = driverDao.findById(DRIVER_ID);
        assertTrue(driver.isPresent());
    }

    @Test
    public void testFindWithClientId(){
        Optional<Driver> client = driverDao.findById(CLIENT_ID);
        assertFalse(client.isPresent());
    }

    @Test
    public void testCreate(){
        assertEquals(NEW_DRIVER,driverDao.create(NEW_DRIVER,"lmao").getId());
    }

    @Test
    public void testGetAll(){
        List<Driver> drivers = driverDao.getAll();
        assertEquals(drivers.get(0).getId(),DRIVER_ID);
        assertEquals(drivers.get(1).getId(),NEW_DRIVER);
    }

    @Test
    public void testFindByUsername(){
        Optional<Driver> driver = driverDao.findByUsername("JuanDriver");
        assertTrue(driver.isPresent()&&driver.get().getId()==DRIVER_ID);
    }


}
