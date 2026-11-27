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
    private static final String FILE_TYPE = "image/png";
    private static final byte[] FILE_BYTES = new byte[] {1, 2, 3, 4, 5, 6};


    private static final Integer BOOKING_WITH_POP_ID = 500;
    private static final Integer BOOKING_WITHOUT_POP_ID = 501;
    private static final Integer VEHICLE_WITH_IMAGE_ID = 1;
    private static final Integer VEHICLE_WITHOUT_IMAGE_ID = 2;
    private static final Integer USER_WITH_PFP_ID = 1;
    private static final Integer USER_WITHOUT_PFP_ID = 2;

    private static final Integer PREEXISTING_PFP_ID = 400;
    private static final Integer PREEXISTING_POP_ID = 401;
    private static final Integer PREEXISTING_VEHICLE_IMG_ID = 402;

    private static final String NEW_FILE_NAME = "new.png";

    private static Booking bookingWithPop;
    private static Booking bookingWithoutPop;
    private static Vehicle vehicleWithImage;
    private static Vehicle vehicleWithoutImage;
    private static Client clientWithPfp;
    private static Driver driverWithoutPfp;

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
        bookingWithPop = em.find(Booking.class, BOOKING_WITH_POP_ID);
        bookingWithoutPop = em.find(Booking.class, BOOKING_WITHOUT_POP_ID);
        vehicleWithImage = em.find(Vehicle.class, VEHICLE_WITH_IMAGE_ID);
        vehicleWithoutImage = em.find(Vehicle.class, VEHICLE_WITHOUT_IMAGE_ID);
        clientWithPfp = em.find(Client.class, USER_WITH_PFP_ID);
        driverWithoutPfp = em.find(Driver.class, USER_WITHOUT_PFP_ID);
    }


    @Test
    public void testGetImage() {
        Image retrievedImage = imageDao.getImage(PREEXISTING_PFP_ID);

        assertNotNull(retrievedImage);
        assertEquals("pfp_JuanClient.jpg", retrievedImage.getFileName());
        assertEquals("jpg", retrievedImage.getType());
        assertArrayEquals(new byte[]{0x31}, retrievedImage.getData());
    }


    @Test
    public void testUploadPop() {
        imageDao.uploadPop(FILE_BYTES, FILE_NAME, bookingWithoutPop, FILE_TYPE);
        em.flush();

        Integer newImgId = bookingWithoutPop.getPop();
        assertNotNull(newImgId);

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "image",
                String.format("file_name = '%s' and type = '%s'", FILE_NAME, FILE_TYPE)
        ));

        Image savedImage = em.find(Image.class, newImgId);
        assertNotNull(savedImage);
        assertArrayEquals(FILE_BYTES, savedImage.getData());
    }

    @Test
    public void testUploadPfp() {
        imageDao.uploadPfp(FILE_BYTES, FILE_NAME, driverWithoutPfp, FILE_TYPE);
        em.flush();

        Integer newImgId = driverWithoutPfp.getPfp();
        assertNotNull(newImgId);

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "image",
                String.format("file_name = '%s' and type = '%s'", FILE_NAME, FILE_TYPE)
        ));

        Image savedImage = em.find(Image.class, newImgId);
        assertNotNull(savedImage);
        assertArrayEquals(FILE_BYTES, savedImage.getData());
    }

    @Test
    public void testUploadVehicleImage() {
        imageDao.uploadVehicleImage(FILE_BYTES, FILE_NAME, vehicleWithoutImage, FILE_TYPE);
        em.flush();

        Integer newImgId = vehicleWithoutImage.getImgId();
        assertNotNull(newImgId);

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "image",
                String.format("file_name = '%s' and type = '%s'", FILE_NAME, FILE_TYPE)
        ));

        Image savedImage = em.find(Image.class, newImgId);
        assertNotNull(savedImage);
        assertArrayEquals(FILE_BYTES, savedImage.getData());
    }


    @Test
    public void testReplacePop() {
        byte[] newBytes = new byte[]{9, 8, 7};
        imageDao.uploadPop(newBytes, NEW_FILE_NAME, bookingWithPop, FILE_TYPE);
        em.flush();

        Integer newImgId = bookingWithPop.getPop();

        assertNotEquals(PREEXISTING_POP_ID, newImgId);
        assertNull(em.find(Image.class, PREEXISTING_POP_ID));

        Image newImage = em.find(Image.class, newImgId);
        assertNotNull(newImage);
        assertArrayEquals(newBytes, newImage.getData());
    }

    @Test
    public void testReplacePfp() {
        byte[] newBytes = new byte[]{9, 8, 7};
        imageDao.uploadPfp(newBytes, NEW_FILE_NAME, clientWithPfp, FILE_TYPE);
        em.flush();

        Integer newImgId = clientWithPfp.getPfp();

        assertNotEquals(PREEXISTING_PFP_ID, newImgId);
        assertNull(em.find(Image.class, PREEXISTING_PFP_ID));

        Image newImage = em.find(Image.class, newImgId);
        assertNotNull(newImage);
        assertArrayEquals(newBytes, newImage.getData());
    }

    @Test
    public void testReplaceVehicleImage() {
        byte[] newBytes = new byte[]{9, 8, 7};
        imageDao.uploadVehicleImage(newBytes, NEW_FILE_NAME, vehicleWithImage, FILE_TYPE);
        em.flush();
        Integer newImgId = vehicleWithImage.getImgId();

        assertNotEquals(PREEXISTING_VEHICLE_IMG_ID, newImgId);
        assertNull(em.find(Image.class, PREEXISTING_VEHICLE_IMG_ID));

        Image newImage = em.find(Image.class, newImgId);
        assertNotNull(newImage);
        assertArrayEquals(newBytes, newImage.getData());
    }

    @Test
    public void testRemovePop() {
        imageDao.uploadPop(new byte[0], FILE_NAME, bookingWithPop, FILE_TYPE);
        em.flush();

        assertNull(bookingWithPop.getPop());
        assertNull(em.find(Image.class, PREEXISTING_POP_ID));
    }

    @Test
    public void testRemovePfp() {
        imageDao.uploadPfp(new byte[0], FILE_NAME, clientWithPfp, FILE_TYPE);
        em.flush();

        assertNull(clientWithPfp.getPfp());
        assertNull(em.find(Image.class, PREEXISTING_PFP_ID));
    }

    @Test
    public void testRemoveVehicleImage() {
        imageDao.uploadVehicleImage(new byte[0], FILE_NAME, vehicleWithImage, FILE_TYPE);
        em.flush();

        assertNull(vehicleWithImage.getImgId());
        assertNull(em.find(Image.class, PREEXISTING_VEHICLE_IMG_ID));
    }
}