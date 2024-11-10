package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Language;
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
@Sql(scripts = "classpath:add_client_test_data.sql")
public class ClientJpaDaoTest {
    private final static Language ENGLISH = Language.ENGLISH;
    private final static Language SPANISH = Language.SPANISH;
    private final static int USER_ID_NOT_CREATED = 1;
    private final static String USERNAME_NOT_CREATED = "JuanClient";
    private final static String MAIL_NOT_CREATED = "juanC@mail.com";
    private final static String PASSWORD_NOT_CREATED = "123321";

    private final static int USER_ID_EXISTING = 500;
    private final static String USERNAME_EXISTING = "AnotherJuanClient";
    private final static String MAIL_EXISTING = "AjuanC@mail.com";
    private final static String PASSWORD_EXISTING = "123321";

    private final static String CHANGED_USERNAME = "Carlos";
    private final static String CHANGED_MAIL = "Carlos@mail.com";
    private final static String CHANGED_PASSWORD = "321123";

    private final static String MAIL_EXISTING_TWO = "YAjuanC@mail.com";

    private final static int USER_ID_NOT_EXISTING = -1;

    private static Client existingClient;

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
        existingClient = em.find(Client.class, USER_ID_EXISTING);
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
        Optional<Client> optionalClient = clientDao.findById(USER_ID_EXISTING);
        assertTrue(optionalClient.isPresent());
        Client client = optionalClient.get();

        assertEquals(USER_ID_EXISTING, client.getId());
        assertEquals(USERNAME_EXISTING, client.getUsername());
        assertEquals(MAIL_EXISTING, client.getMail());
        assertEquals(PASSWORD_EXISTING, client.getPassword());
        assertEquals(SPANISH, client.getLanguage());
    }

    @Test
    public void testFindByUsername() {
        Optional<Client> optionalClient = clientDao.findByUsername(USERNAME_EXISTING);
        assertTrue(optionalClient.isPresent());
        Client client = optionalClient.get();

        assertEquals(USER_ID_EXISTING, client.getId());
        assertEquals(USERNAME_EXISTING, client.getUsername());
        assertEquals(MAIL_EXISTING, client.getMail());
        assertEquals(PASSWORD_EXISTING, client.getPassword());
        assertEquals(SPANISH, client.getLanguage());
    }

    @Test
    public void testWrongIdInFindById() {
        Optional<Client> client = clientDao.findById(USER_ID_NOT_EXISTING);

        assertTrue(client.isEmpty());
    }

    @Test
    public void testMailExists() {
        assertTrue(clientDao.mailExists(MAIL_EXISTING_TWO));
    }

    @Test
    public void testMailNotExists() {
        assertFalse(clientDao.mailExists(MAIL_NOT_CREATED));
    }

    @Test
    public void testChangePassword() {
        clientDao.updatePassword(existingClient, CHANGED_PASSWORD);
        assertEquals(CHANGED_PASSWORD, existingClient.getPassword());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                        id = '%d' and password = '%s'
                        """, USER_ID_EXISTING, CHANGED_PASSWORD)
        ));
    }

    @Test
    public void testEditProfileName() {
        clientDao.editProfile(existingClient, CHANGED_USERNAME, MAIL_EXISTING, SPANISH);
        assertEquals(CHANGED_USERNAME, existingClient.getUsername());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                        id = '%d' and username = '%s' and mail = '%s' and
                        password = '%s' and language = '%s'
                        """, USER_ID_EXISTING, CHANGED_USERNAME, MAIL_EXISTING,
                        PASSWORD_EXISTING, SPANISH.name())
        ));
    }

    @Test
    public void testEditProfileMail() {
        clientDao.editProfile(existingClient, USERNAME_EXISTING, CHANGED_MAIL, SPANISH);
        assertEquals(CHANGED_MAIL, existingClient.getMail());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                        id = '%d' and username = '%s' and mail = '%s' and
                        password = '%s' and language = '%s'
                        """, USER_ID_EXISTING, USERNAME_EXISTING, CHANGED_MAIL,
                        PASSWORD_EXISTING, SPANISH.name())
        ));
    }

    @Test
    public void testEditProfileLanguage() {
        clientDao.editProfile(existingClient, USERNAME_EXISTING, MAIL_EXISTING, ENGLISH);
        assertEquals(ENGLISH, existingClient.getLanguage());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                        id = '%d' and username = '%s' and mail = '%s' and
                        password = '%s' and language = '%s'
                        """, USER_ID_EXISTING, USERNAME_EXISTING, MAIL_EXISTING,
                        PASSWORD_EXISTING, ENGLISH.name())
        ));
    }

    @Test
    public void testEditProfileMultipleAttributes() {
        clientDao.editProfile(existingClient, CHANGED_USERNAME, CHANGED_MAIL, ENGLISH);
        assertEquals(CHANGED_USERNAME, existingClient.getUsername());

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "app_user",
                String.format("""
                        id = '%d' and username = '%s' and mail = '%s' and
                        password = '%s' and language = '%s'
                        """, USER_ID_EXISTING, CHANGED_USERNAME, CHANGED_MAIL,
                        PASSWORD_EXISTING, ENGLISH.name())
        ));
    }
}
