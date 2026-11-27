package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_token")
public class PasswordToken {

    private static final int TOKEN_LENGTH = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passwordtoken_id_seq")
    @SequenceGenerator(sequenceName = "passwordtoken_id_seq", name = "passwordtoken_id_seq", allocationSize = 1)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    private User user;

    @Column(nullable = false, length = TOKEN_LENGTH)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    public PasswordToken() {
    }

    public PasswordToken(User user, String token, LocalDateTime expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

}
