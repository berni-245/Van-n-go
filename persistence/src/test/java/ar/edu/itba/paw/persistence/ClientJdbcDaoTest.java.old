package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.Client;
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

import javax.sql.DataSource;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = {
        "classpath:db_init.sql", // Adds constant data to tables
        "classpath:add_client_test_data.sql"
})
public class ClientJdbcDaoTest {
    private final static int USER_ID_NOT_CREATED = 1;
    private final static String USERNAME_NOT_CREATED = "JuanClient";
    private final static String USERMAIL_NOT_CREATED = "juanC@mail.com";
    private final static String PASSWORD_NOT_CREATED = "123321";

    private final static int USER_ID_EXISTING_CLIENT = 500;
    private final static String USERNAME_EXISTING_CLIENT = "AnotherJuanClient";
    private final static String USERMAIL_EXISTING_CLIENT = "AjuanC@mail.com";
    private final static String PASSWORD_EXISTING_CLIENT = "123321";

    private final static int USER_ID_NOT_EXISTING = -1;

    @Autowired
    private ClientJdbcDao clientDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateClient() {
        Client client = clientDao.create(USER_ID_NOT_CREATED);

        assertEquals(USER_ID_NOT_CREATED, client.getId());
        assertEquals(USERNAME_NOT_CREATED, client.getUsername());
        assertEquals(USERMAIL_NOT_CREATED, client.getMail());
        assertEquals(PASSWORD_NOT_CREATED, client.getPassword());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "client",
                String.format("user_id = '%d'", USER_ID_NOT_CREATED)));
    }

    @Test
    public void testFindById() {
        Client client = clientDao.findById(USER_ID_EXISTING_CLIENT).orElseThrow();

        assertEquals(USER_ID_EXISTING_CLIENT, client.getId());
        assertEquals(USERNAME_EXISTING_CLIENT, client.getUsername());
        assertEquals(USERMAIL_EXISTING_CLIENT, client.getMail());
        assertEquals(PASSWORD_EXISTING_CLIENT, client.getPassword());
    }

    @Test
    public void testFindByUsername() {
        Client client = clientDao.findByUsername(USERNAME_EXISTING_CLIENT).orElseThrow();

        assertEquals(USER_ID_EXISTING_CLIENT, client.getId());
        assertEquals(USERNAME_EXISTING_CLIENT, client.getUsername());
        assertEquals(USERMAIL_EXISTING_CLIENT, client.getMail());
        assertEquals(PASSWORD_EXISTING_CLIENT, client.getPassword());
    }

    @Test
    public void testWrongIdInFindById() {
        Optional<Client> client = clientDao.findById(USER_ID_NOT_EXISTING);

        assertTrue(client.isEmpty());
    }
}
