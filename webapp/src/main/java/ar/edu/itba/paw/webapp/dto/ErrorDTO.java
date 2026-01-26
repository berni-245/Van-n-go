package ar.edu.itba.paw.webapp.dto;

public class ErrorDTO {

    private String type;
    private String description;

    public ErrorDTO() {

    }

    public ErrorDTO(ErrorType type, String description) {
        this.type = type.name();
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
