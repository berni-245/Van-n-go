package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

public class ProfileForm {
    @Nullable
    @Length(min = 5, max = 255)
    private String description;
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Length(max = 32)
    private String cbu;

    public String getDescription() {return description;}

    public String getcbu() {return cbu;}

    public void setcbu(String cbu) {
        this.cbu = cbu;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
