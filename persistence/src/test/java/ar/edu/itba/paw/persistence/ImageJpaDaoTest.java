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

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:add_image_data.sql")
public class ImageJpaDaoTest {

    private static final String FILE_NAME = "file.png";
    private static final byte[] FILE_BYTES = new byte[] {1, 2, 3, 4, 5, 6};
    private static final Integer PREEXISTING_BOOK_ID = 500;
    private static final Integer PREEXISTING_VEHICLE_ID = 1;
    private static final Integer PREEXISTING_USER_ID = 1;

    private static Booking booking;
    private static Vehicle vehicle;
    private static Client client;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ImageJpaDao imageDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        booking = em.find(Booking.class, PREEXISTING_BOOK_ID);
        vehicle = em.find(Vehicle.class, PREEXISTING_VEHICLE_ID);
        client = em.find(Client.class, PREEXISTING_USER_ID);
    }

    @Test
    public void testUploadPop() {
        int id = imageDao.uploadPop(FILE_BYTES, FILE_NAME, booking);
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "image",
                String.format("id = '%d' and file_name = '%s'",
                        id, FILE_NAME
                )
        ));

        byte[] fetchedBytes = jdbcTemplate.queryForObject(
                "SELECT bin FROM image WHERE id = ?",
                new Object[] { id }, byte[].class);

        assertArrayEquals(FILE_BYTES, fetchedBytes);

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("id = '%d' and proof_of_payment = '%d'",
                        PREEXISTING_BOOK_ID, id
                )
        ));
    }

    @Test
    public void testUploadVehicleImage() {
        int id = imageDao.uploadVehicleImage(FILE_BYTES, FILE_NAME, vehicle);
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "image",
                String.format("id = '%d' and file_name = '%s'",
                        id, FILE_NAME
                )
        ));

        byte[] fetchedBytes = jdbcTemplate.queryForObject(
                "SELECT bin FROM image WHERE id = ?",
                new Object[] { id }, byte[].class);

        assertArrayEquals(FILE_BYTES, fetchedBytes);

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "vehicle",
                String.format("id = '%d' and img_id = '%d'",
                        PREEXISTING_VEHICLE_ID, id
                )
        ));
    }

    @Test
    public void testUploadPfp() {
        int id = imageDao.uploadPfp(FILE_BYTES, FILE_NAME, client);
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "image",
                String.format("id = '%d' and file_name = '%s'",
                        id, FILE_NAME
                )
        ));

        byte[] fetchedBytes = jdbcTemplate.queryForObject(
                "SELECT bin FROM image WHERE id = ?",
                new Object[] { id }, byte[].class);

        assertArrayEquals(FILE_BYTES, fetchedBytes);

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("id = '%d' and pfp = '%d'",
                        PREEXISTING_USER_ID, id
                )
        ));
    }


    // We won't be testing getImage because I can't insert bytea types into db, and all alternatives I found didn't work :(
}
