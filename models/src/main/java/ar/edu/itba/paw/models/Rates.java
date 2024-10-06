package ar.edu.itba.paw.models;

public enum Rates {
    CHEAP(Rates.MINIMUM,3000),
    MEDIUM(3000,10000),
    EXPENSIVE(10000,Rates.MAXIMUM);

    public final static int MINIMUM = 100;
    public final static int MAXIMUM = 100000;
    private final double minRate;
    private final double maxRate;

    Rates(final double minRate, final double maxRate) {
        this.minRate = minRate;
        this.maxRate = maxRate;
    }

    public double getMinRate() {return minRate;}
    public double getMaxRate() {return maxRate;}
}
