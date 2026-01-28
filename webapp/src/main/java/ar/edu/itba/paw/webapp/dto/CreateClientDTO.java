package ar.edu.itba.paw.webapp.dto;

public class CreateClientDTO {
    private String username;
    private String email;
    private String password;
    private Integer zoneId;
    private String preferedLanguage;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public String getPreferedLanguage() {
        return preferedLanguage;
    }

    public void setPreferedLanguage(String preferedLanguage) {
        this.preferedLanguage = preferedLanguage;
    }
}
