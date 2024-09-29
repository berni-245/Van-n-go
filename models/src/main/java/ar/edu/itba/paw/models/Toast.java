package ar.edu.itba.paw.models;

public class Toast {
    private final ToastType type;
    private final String titleCode;
    private final String descriptionCode;
    private final int delay;

    public Toast(ToastType type, String titleCode, String descriptionCode, int delaySecs) {
        this.type = type;
        this.titleCode = titleCode;
        this.descriptionCode = descriptionCode;
        this.delay = delaySecs * 1000;
    }

    public Toast(ToastType type, String titleCode, String descriptionCode) {
        this(type, titleCode, descriptionCode, 0);
    }

    public Toast(ToastType type, String descriptionCode, int delay) {
        this(type, "", descriptionCode, delay);
    }

    public Toast(ToastType type, String descriptionCode) {
        this(type, "", descriptionCode, 5);
    }

    public String getType() {
        return type.name();
    }

    public String getTitleCode() {
        return titleCode;
    }

    public String getDescriptionCode() {
        return descriptionCode;
    }

    public int getDelay() {
        return delay;
    }
}
