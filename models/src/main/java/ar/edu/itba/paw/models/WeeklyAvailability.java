package ar.edu.itba.paw.models;

public class WeeklyAvailability {
    private final int weekDay;
    private final String timeStart;
    private final String timeEnd;
    // I would prefer to do this but how do I use ZoneJdbcDao:getZone
    // the WeeklyAvailabilityJdbcDao's row mapped in order to reuse the code?
    // private final Zone zone;
    private final long zoneId;
    private final long vehicleId;

    public WeeklyAvailability(
            int weekDay,
            String timeStart,
            String timeEnd,
            long zoneId,
            long vehicleId
    ) {
        this.weekDay = weekDay;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.zoneId = zoneId;
        this.vehicleId = vehicleId;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public long getZoneId() {
        return zoneId;
    }

    public long getVehicleId() {
        return vehicleId;
    }
}
