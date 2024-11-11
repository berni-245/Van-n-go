package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "app_user")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_id_seq")
    @SequenceGenerator(sequenceName = "app_user_id_seq", name = "app_user_id_seq", allocationSize = 1)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String mail;

    @Column(nullable = false)
    private String password;

    @Column
    private Integer pfp;

   @Column
   @Enumerated(EnumType.STRING)
   private Language language;

   //TODO: añadir nullable=false luego de añadirle creationTimes a los usuarios de pampero
   @Column(name = "creation_time")
   private LocalDateTime creationTime;

    @PrePersist
    protected void onCreate() {
        this.creationTime = LocalDateTime.now();
    }

    User() {
        //This is used by hibernate. Do not remove.
    }

    public User(String username, String mail, String password, Language language) {
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.language = language;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }


    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPfp(Integer pfp) {
        this.pfp = pfp;
    }

    public Integer getPfp() {
        return pfp;
    }

    abstract public boolean isDriver();

    abstract public boolean isClient();


    public boolean getIsDriver() {
        return isDriver();
    }

    public Language getLanguage() { return language; }

    public void setLanguage(Language language) { this.language = language; }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return "User{id = %d, username='%s', mail='%s'}".formatted(id, username, mail);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User user && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public abstract String getType();
}
