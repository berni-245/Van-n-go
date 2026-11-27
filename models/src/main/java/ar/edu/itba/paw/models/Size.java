package ar.edu.itba.paw.models;

public enum Size {
    SMALL(Size.MIN, 6),
    MEDIUM(6, 15),
    LARGE(15, Size.MAX);

    public final static int MIN = 1;
    public final static int MAX = 100;
    private final int minVolume;
    private final int maxVolume;

    Size(int minVolume, int maxVolume) {
        this.minVolume = minVolume;
        this.maxVolume = maxVolume;
    }

    public int getMinVolume() {
        return minVolume;
    }

    public int getMaxVolume() {
        return maxVolume;
    }

    public Size fromString(String string) {
        return valueOf(string.toUpperCase());
    }
}
