package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

public class ProfileForm {
    @Length(min = 5, max = 255)
    private String extra1;
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Length(min = 0, max = 32)
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
