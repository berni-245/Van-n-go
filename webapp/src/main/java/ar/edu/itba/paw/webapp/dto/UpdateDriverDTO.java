package ar.edu.itba.paw.webapp.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

public class UpdateDriverDTO extends UpdateUserDTO {

    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Length(min = 2, max = 32)
    private String cbu;

    @Length(min = 5, max = 255)
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
