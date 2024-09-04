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

    // Clearly should find a better way and we also need to use the message.properties
    public String getWeekDayString() {
        switch (weekDay) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "";
        }
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
