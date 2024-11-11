package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.Size;
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
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:add_driver_data.sql")
// There will be a vehicle with id 1, who works in the zone with id 1
// This driver works:
//  evening on wednesday
//  afternoon and evening on thursday
//  morning, afternoon and evening on friday
public class DriverJpaDaoTest {
    private final static Language ENGLISH = Language.ENGLISH;
    private final static Language SPANISH = Language.SPANISH;
    private final static Integer USER_ID_NOT_CREATED = 1;
    private final static String USERNAME_NOT_CREATED = "JuanDriver";
    private final static String MAIL_NOT_CREATED = "juanC@mail.com";
    private final static String PASSWORD_NOT_CREATED = "123321";
    private final static String DESCRIPTION = "Hago fletes";

    private final static Integer PREEXISTING_ID = 500;
    private final static String PREEXISTING_USERNAME = "THEDriver";
    private final static String PREEXISTING_MAIL = "driver@mail.com";
    private final static String PREEXISTING_PASSWORD = "123321";
    private final static String PREEXISTING_DESCRIPTION = "Im the driver";
    private final static String PREEXISTING_CBU = "0720072007200720072044";

    private final static Integer PREEXISTING_VEHICLE_ZONE = 1;
    private final static Size PREEXISTING_VEHICLE_SIZE = Size.LARGE;
    private final static double MAX_PRICE = 200;
    private final static DayOfWeek DAY_OF_WEEK = DayOfWeek.WEDNESDAY;
    private final static Integer MIN_RATING = 3;

    private final static Integer PREEXISTING_ID_TWO = 501;
    private final static String PREEXISTING_MAIL_TWO = "YAdriver@mail.com";

    private final static int DRIVER_COUNT = 2;

    private final static String CHANGED_USERNAME = "Carlos";
    private final static String CHANGED_MAIL = "Carlos@mail.com";
    private final static String CHANGED_PASSWORD = "321123";
    private final static String CHANGED_DESCRIPTION = "Hago muchos fletes";
    private final static String CHANGED_CBU = "5656565656565656565644";

    private static Driver preexistingDriver;
    private static Zone preexistingVehicleZone;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DriverJpaDao driverDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        preexistingDriver = em.find(Driver.class, PREEXISTING_ID);
        preexistingVehicleZone = em.find(Zone.class, PREEXISTING_VEHICLE_ZONE);
    }

    @Test
    public void testCreateDriver() {
        Driver driver = driverDao.create(USERNAME_NOT_CREATED, MAIL_NOT_CREATED, PASSWORD_NOT_CREATED, DESCRIPTION, ENGLISH);
        assertNotNull(driver);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                                id = '%d' and username = '%s' and mail = '%s' and
                                password = '%s' and language = '%s'
                                """, USER_ID_NOT_CREATED, USERNAME_NOT_CREATED, MAIL_NOT_CREATED,
                        PASSWORD_NOT_CREATED, ENGLISH.name())
        ));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "driver",
                String.format("id = '%d' and description = '%s'", USER_ID_NOT_CREATED, DESCRIPTION)
        ));
    }

    @Test
    public void testFindById() {
        Driver driver = driverDao.findById(PREEXISTING_ID);
        assertEquals(PREEXISTING_ID, driver.getId());
        assertEquals(PREEXISTING_USERNAME, driver.getUsername());
        assertEquals(PREEXISTING_MAIL, driver.getMail());
        assertEquals(PREEXISTING_PASSWORD, driver.getPassword());
        assertEquals(SPANISH, driver.getLanguage());
    }

    @Test
    public void testWrongIdInFindById() {
        assertThrows(
                UserNotFoundException.class,
                () -> driverDao.findById(USER_ID_NOT_CREATED));
    }

    @Test
    public void testFindByUsername() {
        Optional<Driver> optionalDriver = driverDao.findByUsername(PREEXISTING_USERNAME);
        assertTrue(optionalDriver.isPresent());
        Driver driver = optionalDriver.get();

        assertEquals(PREEXISTING_ID, driver.getId());
        assertEquals(PREEXISTING_USERNAME, driver.getUsername());
        assertEquals(PREEXISTING_MAIL, driver.getMail());
        assertEquals(PREEXISTING_PASSWORD, driver.getPassword());
        assertEquals(SPANISH, driver.getLanguage());
    }

    @Test
    public void testWrongUsernameInFindByUsername() {
        Optional<Driver> driver = driverDao.findByUsername(USERNAME_NOT_CREATED);

        assertTrue(driver.isEmpty());
    }

    @Test
    public void testMailExists() {
        assertTrue(driverDao.mailExists(PREEXISTING_MAIL_TWO));
    }

    @Test
    public void testMailNotExists() {
        assertFalse(driverDao.mailExists(MAIL_NOT_CREATED));
    }

    @Test
    public void testChangePassword() {
        driverDao.updatePassword(preexistingDriver, CHANGED_PASSWORD);
        assertEquals(CHANGED_PASSWORD, preexistingDriver.getPassword());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                        id = '%d' and password = '%s'
                        """, PREEXISTING_ID, CHANGED_PASSWORD)
        ));
    }

    // Remember that there's a preexistingDriver with PREEXISTING_ID, PREEXISTING_USERNAME, PREEXISTING_MAIL and SPANISH lang

    @Test
    public void testEditProfileName() {
        driverDao.editProfile(preexistingDriver, CHANGED_USERNAME, PREEXISTING_MAIL, SPANISH);
        assertEquals(CHANGED_USERNAME, preexistingDriver.getUsername());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                                id = '%d' and username = '%s' and mail = '%s' and
                                password = '%s' and language = '%s'
                                """, PREEXISTING_ID, CHANGED_USERNAME, PREEXISTING_MAIL,
                        PREEXISTING_PASSWORD, SPANISH.name())
        ));
    }

    @Test
    public void testEditProfileMail() {
        driverDao.editProfile(preexistingDriver, PREEXISTING_USERNAME, CHANGED_MAIL, SPANISH);
        assertEquals(CHANGED_MAIL, preexistingDriver.getMail());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                                id = '%d' and username = '%s' and mail = '%s' and
                                password = '%s' and language = '%s'
                                """, PREEXISTING_ID, PREEXISTING_USERNAME, CHANGED_MAIL,
                        PREEXISTING_PASSWORD, SPANISH.name())
        ));
    }

    @Test
    public void testEditProfileLanguage() {
        driverDao.editProfile(preexistingDriver, PREEXISTING_USERNAME, PREEXISTING_MAIL, ENGLISH);
        assertEquals(ENGLISH, preexistingDriver.getLanguage());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                                id = '%d' and username = '%s' and mail = '%s' and
                                password = '%s' and language = '%s'
                                """, PREEXISTING_ID, PREEXISTING_USERNAME, PREEXISTING_MAIL,
                        PREEXISTING_PASSWORD, ENGLISH.name())
        ));
    }

    @Test
    public void testEditProfileMultipleAttributes() {
        driverDao.editProfile(preexistingDriver, CHANGED_USERNAME, CHANGED_MAIL, ENGLISH);
        assertEquals(CHANGED_USERNAME, preexistingDriver.getUsername());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                                id = '%d' and username = '%s' and mail = '%s' and
                                password = '%s' and language = '%s'
                                """, PREEXISTING_ID, CHANGED_USERNAME, CHANGED_MAIL,
                        PREEXISTING_PASSWORD, ENGLISH.name())
        ));
    }

    // Remember that there's a preexistingDriver with PREEXISTING_ID, PREEXISTING_DESCRIPTION and PREEXISTING_CBU

    @Test
    public void testEditProfileDescription() {
        driverDao.editProfile(preexistingDriver, PREEXISTING_USERNAME, PREEXISTING_MAIL, CHANGED_DESCRIPTION, PREEXISTING_CBU, SPANISH);
        assertEquals(CHANGED_DESCRIPTION, preexistingDriver.getDescription());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "driver",
                String.format("""
                        id = '%d' and description = '%s' and cbu = '%s'
                        """, PREEXISTING_ID, CHANGED_DESCRIPTION, PREEXISTING_CBU
                )
        ));
    }

    @Test
    public void testEditProfileCbu() {
        driverDao.editProfile(preexistingDriver, PREEXISTING_USERNAME, PREEXISTING_MAIL, PREEXISTING_DESCRIPTION, CHANGED_CBU, SPANISH);
        assertEquals(CHANGED_CBU, preexistingDriver.getCbu());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "driver",
                String.format("""
                        id = '%d' and description = '%s' and cbu = '%s'
                        """, PREEXISTING_ID, PREEXISTING_DESCRIPTION, CHANGED_CBU
                )
        ));
    }

    @Test
    public void testGetSearchResults() {
        List<Driver> matchingDrivers = driverDao.getSearchResults(preexistingVehicleZone, PREEXISTING_VEHICLE_SIZE, MAX_PRICE, DAY_OF_WEEK, MIN_RATING, null, 0);
        // There's two match for this filters in the data added for this test
        assertEquals(DRIVER_COUNT, matchingDrivers.size());
        assertTrue(matchingDrivers.stream().anyMatch(driver -> driver.getId().equals(PREEXISTING_ID)));
        assertTrue(matchingDrivers.stream().anyMatch(driver -> driver.getId().equals(PREEXISTING_ID_TWO)));
    }

    @Test
    public void testGetSearchCount() {
        int count = driverDao.getSearchCount(preexistingVehicleZone, PREEXISTING_VEHICLE_SIZE, MAX_PRICE, DAY_OF_WEEK, MIN_RATING);
        assertEquals(DRIVER_COUNT, count);
    }
}
