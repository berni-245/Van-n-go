package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
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

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:add_message_data.sql")
public class MessageJpaDaoTest {

    private static final Integer CLIENT_ID = 1;
    private static final Integer DRIVER_ID = 2;
    private static final Integer CLIENT_WITH_NO_MESSAGE_ID = 3;

    private static final String FIRST_MSG = "hello, Ive sent you the proof of payment";
    private static final String SECOND_MSG = "hello, thanks!";
    private static final String THIRD_MSG = "cya!";

    private static Client client;
    private static Driver driver;

    private static final Integer CREATED_MSG_ID = 1;
    private static Client clientWithNoMessage;
    private static final String MSG = "Greeting!";

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MessageJpaDao messageDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        client = em.find(Client.class, CLIENT_ID);
        driver = em.find(Driver.class, DRIVER_ID);
        clientWithNoMessage = em.find(Client.class, CLIENT_WITH_NO_MESSAGE_ID);
    }

    @Test
    public void testSendMessage() {
        Message message = messageDao.sendMessage(clientWithNoMessage, driver, MSG, false);
        assertNotNull(message);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "message",
                String.format("""
                                id = '%d' and content = '%s' and client_id = '%d' and
                                driver_id = '%d' and sent_by_driver = '%b'""",
                        CREATED_MSG_ID, MSG, CLIENT_WITH_NO_MESSAGE_ID,
                        DRIVER_ID, false
                )
        ));
    }

    @Test
    public void testGetConversation() {
        List<Message> messages = messageDao.getConversation(client, driver);
        assertFalse(messages.isEmpty());
        Iterator<Message> iterator = messages.iterator();
        Message thirdMsg = iterator.next();
        assertEquals(client, thirdMsg.getClient());
        assertEquals(driver, thirdMsg.getDriver());
        // You get the earliest msg first, since you usually see a chat in a most recent order
        assertEquals(THIRD_MSG, thirdMsg.getContent());
        assertEquals(SECOND_MSG, iterator.next().getContent());
        assertEquals(FIRST_MSG, iterator.next().getContent());
    }

    @Test
    public void testGetConversationWithNoMessage() {
        List<Message> messages = messageDao.getConversation(clientWithNoMessage, driver);
        assertTrue(messages.isEmpty());
    }
}
