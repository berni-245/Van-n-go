package ar.edu.itba.paw.models;

public class WeeklyAvailability {
    private final int weekDay;
    private final long zoneId;
    private final long vehicleId;
    private final HourInterval hourInterval;

    public WeeklyAvailability(
            int weekDay,
            HourInterval hourInterval,
            long zoneId,
            long vehicleId
    ) {
        this.weekDay = weekDay;
        this.hourInterval = hourInterval;
        this.zoneId = zoneId;
        this.vehicleId = vehicleId;
    }

    public int getWeekDay() {
        return weekDay;
    }

    // Clearly should find a better way and we also need to use the message.properties
    public String getWeekDayString() {
        return switch (weekDay) {
            case 0 -> "Sunday";
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            default -> "";
        };
    }

    public HourInterval getHourInterval() {
        return hourInterval;
    }

    public long getZoneId() {
        return zoneId;
    }

    public long getVehicleId() {
        return vehicleId;
    }
}
