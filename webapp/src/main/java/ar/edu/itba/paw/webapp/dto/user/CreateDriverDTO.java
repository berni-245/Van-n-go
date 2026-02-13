package ar.edu.itba.paw.webapp.dto.user;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

public class CreateDriverDTO extends CreateUserDTO {
    @Length(min = 5, max = 255, message = "{Length.driver.description}")
    private String description;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "{Pattern.driver.description}")
    @Length(max = 32)
    private String cbu;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }
}
