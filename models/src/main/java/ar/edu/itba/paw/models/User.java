package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "app_users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_id_seq")
    @SequenceGenerator(sequenceName = "app_user_id_seq", name = "app_user_id_seq", allocationSize = 1)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String mail;

    @Column(nullable = false)
    private String password;

    @Column
    private long pfp;

    User(){
        //This is used by hibernate. Do not remove.
    }

    public User( String username, String mail, String password) {
        this.username = username;
        this.mail = mail;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password) { this.password = password; }

    public String getPassword() {
        return password;
    }


    public String getMail() {
        return mail;
    }
    public void setMail(String mail){
        this.mail = mail;
    }

    public void setPfp(long pfp) {this.pfp = pfp;}

    public long getPfp() {return pfp;}

    public abstract boolean isDriver();

    public boolean getIsDriver() {
        return isDriver();
    }

    @Override
    public String toString() {
        return "User{id = %d, username='%s', mail='%s'}".formatted(id, username, mail);
    }
}
