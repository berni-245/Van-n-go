package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
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
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:add_vehicle_data.sql")
public class VehicleJpaDaoTest {
    private final static Language ENGLISH = Language.ENGLISH;
    private final static Language SPANISH = Language.SPANISH;

    private final static Integer PREEXISTING_USER_ID = 500;
    private final static Integer PREEXISTING_USER_ID_TWO = 502;

    private final static Integer PREEXISTING_USER_NO_VEHICLE = 502;
    private final static String PLATE_NUMBER = "ABC123";
    private final static String DESCRIPTION = "This is a toyota";
    private final static double VOLUME = 10.0;
    private final static Integer ZONE_ID = 1;
    private final static double HOURLY_RATE = 100;

    private final static Integer PREEXISTING_USER_ID_THREE = 501;

    private final static Integer PREEXISTING_VEHICLE = 500;
    private final static String PREEXISTING_PLATE_NUMBER = "AAA999";
    private final static Size PREEXISTING_VEHICLE_SIZE = Size.LARGE;
    private final static double MAX_PRICE = 200;
    private final static DayOfWeek DAY_OF_WEEK = DayOfWeek.WEDNESDAY;
    private final static Integer MIN_RATING = 3;
    private final static Integer PREEXISTING_VEHICLE_TWO = 501;
    private final static Integer PREEXISTING_VEHICLE_THREE = 502;
    private final static Integer PREEXISTING_VEHICLE_FOUR = 503;


    private final static Integer NON_EXISTING_USER_ID = 1000;



    private final static int VEHICLE_COUNT = 2;

    private static Vehicle preexistingVehicleTwo;
    private static Vehicle preexistingVehicleThree;
    private static Vehicle preexistingVehicleFour;
    private static Driver preexistingDriver;
    private static Driver preexistingDriverNoVehicle;
    private static Zone adrogue;

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
        preexistingVehicleTwo = em.find(Vehicle.class, PREEXISTING_VEHICLE_TWO);
        preexistingVehicleThree = em.find(Vehicle.class, PREEXISTING_VEHICLE_THREE);
        preexistingVehicleFour = em.find(Vehicle.class, PREEXISTING_VEHICLE_FOUR);
        preexistingDriver = em.find(Driver.class, PREEXISTING_USER_ID);
        preexistingDriverNoVehicle = em.find(Driver.class, PREEXISTING_USER_NO_VEHICLE);
        adrogue = em.find(Zone.class, ZONE_ID);
    }

    @Test
    public void testCreate() {
        Vehicle vehicle = vehicleDao.create(preexistingDriverNoVehicle, PLATE_NUMBER, VOLUME, DESCRIPTION, List.of(adrogue), HOURLY_RATE);

        assertNotNull(vehicle);
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "vehicle",
                String.format("""
                                id = '%d' and plate_number = '%s' and
                                description = '%s'
                                """, vehicle.getId(), PLATE_NUMBER, DESCRIPTION)
        ));

        String query = "SELECT hourly_rate, volume_m3 FROM vehicle WHERE id = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(query, vehicle.getId());

        assertEquals(HOURLY_RATE, (double) result.get("hourly_rate"), 0.001);
        assertEquals(VOLUME, (double) result.get("volume_m3"), 0.001);
    }

    @Test
    public void testFindById() {
        Optional<Vehicle> optionalVehicle = vehicleDao.findById(PREEXISTING_VEHICLE);
        assertTrue(optionalVehicle.isPresent());
        Vehicle vehicle = optionalVehicle.get();

        assertEquals(PREEXISTING_VEHICLE, vehicle.getId());
        assertEquals(PREEXISTING_PLATE_NUMBER, vehicle.getPlateNumber());
        assertEquals(PREEXISTING_VEHICLE_SIZE, vehicle.getSize());
    }

    @Test
    public void testFindByIdFail() {
        Optional<Vehicle> optionalVehicle = vehicleDao.findById(NON_EXISTING_USER_ID);
        assertTrue(optionalVehicle.isEmpty());
    }

    @Test
    public void testFindOwnedById() {
        Optional<Vehicle> optionalVehicle = vehicleDao.findOwnedById(preexistingDriver, PREEXISTING_VEHICLE);
        assertTrue(optionalVehicle.isPresent());
        Vehicle vehicle = optionalVehicle.get();

        assertEquals(PREEXISTING_VEHICLE, vehicle.getId());
        assertEquals(PREEXISTING_PLATE_NUMBER, vehicle.getPlateNumber());
        assertEquals(PREEXISTING_VEHICLE_SIZE, vehicle.getSize());
    }

    @Test
    public void testFindOwnedByIdFail() {
        Optional<Vehicle> optionalVehicle = vehicleDao.findOwnedById(preexistingDriverNoVehicle, PREEXISTING_VEHICLE);
        assertTrue(optionalVehicle.isEmpty());
    }

    @Test
    public void testFindByPlateNumber() {
        Optional<Vehicle> optionalVehicle = vehicleDao.findByPlateNumber(preexistingDriver, PREEXISTING_PLATE_NUMBER);
        assertTrue(optionalVehicle.isPresent());
        Vehicle vehicle = optionalVehicle.get();

        assertEquals(PREEXISTING_VEHICLE, vehicle.getId());
        assertEquals(PREEXISTING_PLATE_NUMBER, vehicle.getPlateNumber());
        assertEquals(PREEXISTING_VEHICLE_SIZE, vehicle.getSize());
    }

    @Test
    public void testFindByPlateNumberFail() {
        Optional<Vehicle> optionalVehicle = vehicleDao.findByPlateNumber(preexistingDriverNoVehicle, PREEXISTING_PLATE_NUMBER);
        assertTrue(optionalVehicle.isEmpty());
    }

    @Test
    public void testGetDriverVehicles() {
        List<Vehicle> vehicles = vehicleDao.getDriverVehicles(preexistingDriver, adrogue, PREEXISTING_VEHICLE_SIZE, MAX_PRICE, DayOfWeek.WEDNESDAY);
        assertEquals(VEHICLE_COUNT, vehicles.size());

        assertTrue(vehicles.stream().anyMatch(vehicle -> vehicle.getId().equals(PREEXISTING_VEHICLE)));
        assertTrue(vehicles.stream().anyMatch(vehicle -> vehicle.getId().equals(PREEXISTING_USER_ID_TWO)));
    }

    @Test
    public void testPlateNumberExists() {
        assertTrue(vehicleDao.plateNumberExists(PREEXISTING_PLATE_NUMBER));
    }

    @Test
    public void testPlateNumberExistsFail() {
        assertFalse(vehicleDao.plateNumberExists(PLATE_NUMBER));
    }

    @Test
    public void testUpdateVehicle() {
        preexistingVehicleTwo.setImgId(1);
        vehicleDao.updateVehicle(preexistingVehicleTwo);
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "vehicle",
                String.format("""
                                id = '%d' and img_id = '%d'
                                """, preexistingVehicleTwo.getId(), 1)
        ));
    }

    @Test
    public void testDeleteVehicle() {
        vehicleDao.deleteVehicle(preexistingVehicleFour);
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "vehicle",
                String.format("id = '%d'", PREEXISTING_VEHICLE_FOUR)
        ));
    }

    @Test
    public void testGetVehicleCount() {
        int count = vehicleDao.getVehicleCount(preexistingDriver);
        assertEquals(VEHICLE_COUNT, count);
    }
}
