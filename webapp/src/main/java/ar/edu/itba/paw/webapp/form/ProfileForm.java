package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

public class ProfileForm {
    @Nullable
    @Length(min = 5, max = 255)
    private String extra1;
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Length(max = 32)
    private String cbu;

    public String getExtra1() {return extra1;}

    public String getcbu() {return cbu;}

    public void setcbu(String cbu) {
        this.cbu = cbu;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }
}
