package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import ar.edu.itba.paw.models.Pagination;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MessageJpaDao implements MessageDao {

    @PersistenceContext
    private EntityManager em;


    @Override
    public List<Message> getConversation(Client client, Driver driver, int offset) {
        var messageIds = em.createNativeQuery("""
                    SELECT m.id FROM message m
                    WHERE m.client_id = :clientId AND m.driver_id = :driverId
                    ORDER BY m.time_sent DESC
                        """)
                .setParameter("clientId", client.getId())
                .setParameter("driverId", driver.getId())
                .setFirstResult(offset)
                .setMaxResults(Pagination.MAX_MESSAGE_RETRIEVAL)
                .getResultList();

        return em.createQuery("SELECT m FROM Message m WHERE m.id IN (:msgIds) order by m.timeSent desc", Message.class)
                .setParameter("msgIds", messageIds)
                .getResultList();
    }


    @Override
    public Message getMessageById(Integer messageId) {
        return em.find(Message.class, messageId);
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

    @Override
    public LocalDateTime getLastMessageSentByUser(Client client, Driver driver, boolean sentByDriver) {
        final TypedQuery<LocalDateTime> query = em.createQuery("SELECT m.timeSent FROM Message m WHERE m.client = :client AND m.driver = :driver AND m.sentByDriver = :sentByDriver ORDER BY m.timeSent DESC", LocalDateTime.class)
                .setParameter("client", client)
                .setParameter("driver", driver)
                .setParameter("sentByDriver", sentByDriver)
                .setMaxResults(1);
        final List<LocalDateTime> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.getFirst();
    }

    @Override
    public int getConversationMessageCount(Client client, Driver driver) {
        return em.createQuery("""
                        select count(*) from Message m
                        where m.client = :client and m.driver = :driver
                        """, Long.class)
                .setParameter("driver", driver)
                .setParameter("client", client)
                .getSingleResult().intValue();
    }
}
