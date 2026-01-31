package ar.edu.itba.paw.webapp.dto;

public class UpdateDriverDTO extends UpdateUserDTO {
    private String cbu;
    private String description;

    public UpdateDriverDTO() {

    }

    public String getCbu() {
        return cbu;
    }

    public String getCbuOr(String other) {
        if (cbu == null)
            return other;
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionOr(String other) {
        if (description == null)
            return other;
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
