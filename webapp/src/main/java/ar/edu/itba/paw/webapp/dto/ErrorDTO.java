package ar.edu.itba.paw.webapp.dto;

public class ErrorDTO {

    private String type;
    private String msg;

    public ErrorDTO() {

    }

    public ErrorDTO(ErrorType type, String msg) {
        this.type = type.name();
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
