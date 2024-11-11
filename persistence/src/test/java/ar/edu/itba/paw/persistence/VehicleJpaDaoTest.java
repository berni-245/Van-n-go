package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
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

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:.sql")
public class VehicleJpaDaoTest {
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
    private VehicleJpaDao vehicleDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        preexistingDriver = em.find(Driver.class, PREEXISTING_ID);
        preexistingVehicleZone = em.find(Zone.class, PREEXISTING_VEHICLE_ZONE);
    }
}
