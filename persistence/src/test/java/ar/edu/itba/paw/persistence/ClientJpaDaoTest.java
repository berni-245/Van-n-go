package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Language;
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
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:add_client_data.sql")
public class ClientJpaDaoTest {
    private final static Language ENGLISH = Language.ENGLISH;
    private final static Language SPANISH = Language.SPANISH;
    private final static Integer USER_ID_NOT_CREATED = 1;
    private final static String USERNAME_NOT_CREATED = "JuanClient";
    private final static String MAIL_NOT_CREATED = "juanC@mail.com";
    private final static String PASSWORD_NOT_CREATED = "123321";

    private final static Integer PREEXISTING_ID = 500;
    private final static String PREEXISTING_USERNAME = "AnotherJuanClient";
    private final static String PREEXISTING_MAIL = "AjuanC@mail.com";
    private final static String PREEXISTING_PASSWORD = "123321";

    private final static String CHANGED_USERNAME = "Carlos";
    private final static String CHANGED_MAIL = "Carlos@mail.com";
    private final static String CHANGED_PASSWORD = "321123";
    private final static Integer CHANGED_ZONE_ID = 3;

    private final static String PREEXISTING_MAIL_TWO = "YAjuanC@mail.com";

    private final static Integer USER_ID_NOT_EXISTING = -1;

    private static Client preexistingClient;
    private static Zone changedZone;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ClientJpaDao clientDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        preexistingClient = em.find(Client.class, PREEXISTING_ID);
        changedZone = em.find(Zone.class, CHANGED_ZONE_ID);
    }

    @Test
    public void testCreateClient() {
        Client client = clientDao.create(USERNAME_NOT_CREATED, MAIL_NOT_CREATED, PASSWORD_NOT_CREATED, ENGLISH);
        assertNotNull(client);
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
                jdbcTemplate, "client",
                String.format("id = '%d'", USER_ID_NOT_CREATED)
        ));
    }

    @Test
    public void testFindById() {
        Client client = clientDao.findById(PREEXISTING_ID);
        assertEquals(PREEXISTING_ID, client.getId());
        assertEquals(PREEXISTING_USERNAME, client.getUsername());
        assertEquals(PREEXISTING_MAIL, client.getMail());
        assertEquals(PREEXISTING_PASSWORD, client.getPassword());
        assertEquals(SPANISH, client.getLanguage());
    }

    @Test
    public void testWrongIdInFindById() {
        assertThrows(
                UserNotFoundException.class,
                () -> clientDao.findById(USER_ID_NOT_EXISTING)
        );
    }

    @Test
    public void testFindByUsername() {
        Optional<Client> optionalClient = clientDao.findByUsername(PREEXISTING_USERNAME);
        assertTrue(optionalClient.isPresent());
        Client client = optionalClient.get();

        assertEquals(PREEXISTING_ID, client.getId());
        assertEquals(PREEXISTING_USERNAME, client.getUsername());
        assertEquals(PREEXISTING_MAIL, client.getMail());
        assertEquals(PREEXISTING_PASSWORD, client.getPassword());
        assertEquals(SPANISH, client.getLanguage());
    }

    @Test
    public void testWrongUsernameInFindByUsername() {
        Optional<Client> client = clientDao.findByUsername(USERNAME_NOT_CREATED);

        assertTrue(client.isEmpty());
    }

    @Test
    public void testMailExists() {
        assertTrue(clientDao.mailExists(PREEXISTING_MAIL_TWO));
    }

    @Test
    public void testMailNotExists() {
        assertFalse(clientDao.mailExists(MAIL_NOT_CREATED));
    }

    @Test
    public void testChangePassword() {
        clientDao.updatePassword(preexistingClient, CHANGED_PASSWORD);
        assertEquals(CHANGED_PASSWORD, preexistingClient.getPassword());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                        id = '%d' and password = '%s'
                        """, PREEXISTING_ID, CHANGED_PASSWORD)
        ));
    }

    // Remember that there's a preexistingClient with PREEXISTING_ID, PREEXISTING_USERNAME, PREEXISTING_MAIL and SPANISH lang

    @Test
    public void testEditProfileName() {
        clientDao.editProfile(preexistingClient, CHANGED_USERNAME, PREEXISTING_MAIL, SPANISH);
        assertEquals(CHANGED_USERNAME, preexistingClient.getUsername());

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
        clientDao.editProfile(preexistingClient, PREEXISTING_USERNAME, CHANGED_MAIL, SPANISH);
        assertEquals(CHANGED_MAIL, preexistingClient.getMail());

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
        clientDao.editProfile(preexistingClient, PREEXISTING_USERNAME, PREEXISTING_MAIL, ENGLISH);
        assertEquals(ENGLISH, preexistingClient.getLanguage());

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
        clientDao.editProfile(preexistingClient, CHANGED_USERNAME, CHANGED_MAIL, ENGLISH);
        assertEquals(CHANGED_USERNAME, preexistingClient.getUsername());

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

    // Remember that there's a preexistingClient with PREEXISTING_ID, and zone_id = 1

    @Test
    public void testEditProfileZone() {
        clientDao.editProfile(preexistingClient, PREEXISTING_USERNAME, PREEXISTING_MAIL, changedZone, SPANISH);
        assertEquals(changedZone, preexistingClient.getZone());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "client",
                String.format("""
                        id = '%d' and zone_id = '%d'
                        """, PREEXISTING_ID, CHANGED_ZONE_ID)
        ));
    }
}
