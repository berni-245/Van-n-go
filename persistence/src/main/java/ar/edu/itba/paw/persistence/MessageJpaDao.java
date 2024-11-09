package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import ar.edu.itba.paw.models.Pagination;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MessageJpaDao implements MessageDao {

    @PersistenceContext
    private EntityManager em;


    @Override
    public List<Message> getConversation(Client client, Driver driver) {
        var messageIds = em.createNativeQuery("""
                    SELECT m.id FROM message m
                    WHERE m.client_id = :clientId AND m.driver_id = :driverId
                    ORDER BY m.time_sent DESC
                """).setParameter("clientId", client.getId())
                .setParameter("driverId", driver.getId())
                .setMaxResults(Pagination.MAX_MESSAGE_RETRIEVAL)
                .getResultList();

        @SuppressWarnings("unchecked")
        List<Long> longMessageIds = messageIds.stream().map(id -> ((BigInteger) id).longValue()).toList();

        return em.createQuery("SELECT m FROM Message m WHERE m.id IN :msgIds order by m.timeSent desc", Message.class)
                .setParameter("msgIds", longMessageIds)
                .getResultList();
    }

    @Override
    public Message sendMessage(Client client, Driver driver, String content, boolean sentByDriver) {
        Message message = new Message();
        message.setClient(client);
        message.setDriver(driver);
        message.setContent(content);
        message.setSentByDriver(sentByDriver);
        em.persist(message);
        return message;
    }
}
