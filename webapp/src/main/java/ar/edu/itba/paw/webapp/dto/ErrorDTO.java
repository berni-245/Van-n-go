package ar.edu.itba.paw.webapp.dto;

import java.net.URI;

// This ErrorDTO follows problem details format for HTTP APIs in the proposed standard RFC 9457
// https://datatracker.ietf.org/doc/html/rfc9457
public class ErrorDTO {

    private URI type;
    private String title;
    private Integer status;
    private String detail;
    private URI instance;

    public URI getType() {
        return type;
    }

    public void setType(URI type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public URI getInstance() {
        return instance;
    }

    public void setInstance(URI instance) {
        this.instance = instance;
    }
}
