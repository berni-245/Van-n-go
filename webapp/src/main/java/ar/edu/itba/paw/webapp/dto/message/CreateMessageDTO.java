package ar.edu.itba.paw.webapp.dto.message;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class CreateMessageDTO {

    private Integer clientId;

    private Integer driverId;

    @NotBlank
    @Length(max = 255, message = "{Length.message}")
    private String message;

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
