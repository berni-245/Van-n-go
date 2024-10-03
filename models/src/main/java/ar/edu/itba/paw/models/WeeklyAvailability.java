package ar.edu.itba.paw.models;

public class WeeklyAvailability {
    private final int weekDay;
    private final String hourBlockTimeStart;
    // I would prefer to do this but how do I use ZoneJdbcDao:getZone
    // the WeeklyAvailabilityJdbcDao's row mapped in order to reuse the code?
    // private final Zone zone;
    private final long zoneId;
    private final long vehicleId;

    public WeeklyAvailability(
            int weekDay,
            String hourBlockTimeStart,
            long zoneId,
            long vehicleId
    ) {
        this.weekDay = weekDay;
        this.hourBlockTimeStart = hourBlockTimeStart;
        this.zoneId = zoneId;
        this.vehicleId = vehicleId;
    }

    public int getWeekDay() {
        return weekDay;
    }

    // Clearly should find a better way and we also need to use the message.properties
    public String getWeekDayString() {
        return switch (weekDay) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            case 7 -> "Sunday";
            default -> "";
        };
    }

    public String getHourBlockTimeStart() {
        return hourBlockTimeStart;
    }

    public long getZoneId() {
        return zoneId;
    }

    public long getVehicleId() {
        return vehicleId;
    }
}
