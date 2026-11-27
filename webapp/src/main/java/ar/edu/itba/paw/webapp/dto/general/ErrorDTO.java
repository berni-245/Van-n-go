package ar.edu.itba.paw.webapp.dto.general;

import ar.edu.itba.paw.webapp.utils.ExceptionUtils;

public class ErrorDTO {

    private String type;
    private String description;
    private String entity;
    private String subtype;

    public ErrorDTO() {

    }

    public ErrorDTO(Exception e) {
        this.type = ExceptionUtils.getExceptionType(e.getClass());
        this.description = e.getMessage();
    }

    public ErrorDTO(ErrorType type, String description) {
        this.type = type.name();
        this.description = description;
    }

    public ErrorDTO(ErrorType type, String description, String entity) {
        this.type = type.name();
        this.description = description;
        this.entity = entity;
    }

    public ErrorDTO(ErrorType type, String description, String entity, String subtype) {
        this.type = type.name();
        this.description = description;
        this.entity = entity;
        this.subtype = subtype;
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

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }
}
