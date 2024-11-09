package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_id_seq")
    @SequenceGenerator(sequenceName = "message_id_seq", name = "message_id_seq", allocationSize = 1)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Driver driver;

    @Column
    private String content;

    @Column(name = "sent_by_driver", nullable = false)
    private boolean sentByDriver;

    @Column(name = "time_sent", nullable = false, updatable = false)
    private LocalDateTime timeSent;

    @PrePersist
    protected void onCreate() {
        this.timeSent = LocalDateTime.now();
    }

    public Message() {

    }

    public Integer getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    public boolean isSentByDriver() {
        return sentByDriver;
    }

    public void setSentByDriver(boolean sentByDriver) {
        this.sentByDriver = sentByDriver;
    }
}
