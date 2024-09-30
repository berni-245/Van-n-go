package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.WeeklyAvailability;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class WeeklyAvailabilityJdbcDao implements WeeklyAvailabilityDao {

    private static final RowMapper<WeeklyAvailability> ROW_MAPPER =
            (rs, rowNum) -> new WeeklyAvailability(
                    rs.getInt("week_day"),
                    rs.getString("t_start"),
                    rs.getString("t_end"),
                    rs.getLong("zone_id"),
                    rs.getLong("vehicle_id")
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcAvailabilityInsert;
    private final SimpleJdbcInsert jdbcVWZInsert;

    public WeeklyAvailabilityJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcAvailabilityInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("id")
                .withTableName("weekly_availability");
        jdbcVWZInsert = new SimpleJdbcInsert(ds)
                .withTableName("vehicle_weekly_zone");
    }

    private void printTimeRange(WeeklyAvailability wa) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime startTime = LocalTime.parse(wa.getTimeStart(), timeFormatter);
        LocalTime endTime = LocalTime.parse(wa.getTimeEnd(), timeFormatter);

        StringBuilder result = new StringBuilder("old: [");
        for (int hour = 0; hour < 24; hour++) {
            if (startTime.getHour() <= hour && hour < endTime.getHour()) {
                result.append("X");
            } else {
                result.append("O");
            }
        }
        result.append("] - %s to %s".formatted(startTime, endTime));
        System.out.println(result);
    }

    private void printNewWaList(List<WeeklyAvailability> was) {
        StringBuilder result = new StringBuilder("new: [");
        boolean[] hours = new boolean[24];
        for (WeeklyAvailability wa : was) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime startTime = LocalTime.parse(wa.getTimeStart(), timeFormatter);
            hours[startTime.getHour()] = true;
        }
        for (boolean hour : hours) {
            if (hour) {
                result.append("X");
            } else {
                result.append("O");
            }
        }

        result.append("]");
        System.out.println(result);
    }

    public void testRefactor() {
        List<WeeklyAvailability> oldWa = jdbcTemplate.query("""
                select id, week_day, t_start, t_end, zone_id, vehicle_id
                from vehicle_weekly_zone vwz
                join weekly_availability_old wa on vwz.availability_id = wa.id
                order by week_day, vehicle_id, zone_id""", ROW_MAPPER);
        for (WeeklyAvailability wa : oldWa) {
            var was = jdbcTemplate.query("""
                            select week_day, t_start, t_end, vehicle_id, zone_id
                            from weekly_availability wa
                            join hour_block hb on hb.id = wa.hour_block_id
                            where week_day = ? and vehicle_id = ? and zone_id = ?""",
                    new Object[]{wa.getWeekDay(), wa.getVehicleId(), wa.getZoneId()},
                    new int[]{Types.INTEGER, Types.BIGINT, Types.BIGINT},
                    ROW_MAPPER);
            System.out.printf(
                    "week_day: %d, vehicle_id: %d, zone_id: %d\n",
                    wa.getWeekDay(), wa.getVehicleId(), wa.getZoneId()
            );
            printTimeRange(wa);
            printNewWaList(was);
        }
//        List<WeeklyAvailability> newWa = jdbcTemplate.query("""
//                select week_day, t_start, t_end, vehicle_id, zone_id
//                from weekly_availability wa
//                join hour_block hb on hb.id = wa.hour_block_id""", ROW_MAPPER);
    }

    private Optional<Long> findId(int weekDay, String timeStart, String timeEnd) {
        return jdbcTemplate.query("""
                        select id from weekly_availability where
                        week_day = ? and t_start = ? and t_end = ?""",
                new Object[]{weekDay, timeStart, timeEnd},
                new int[]{Types.INTEGER, Types.TIME, Types.TIME},
                (rs, rowNum) -> rs.getLong("id")
        ).stream().findFirst();
    }

    @Override
    public Optional<WeeklyAvailability> create(
            int weekDay, String timeStart, String timeEnd, long zoneId, long vehicleId
    ) {
        // This should be a db trigger...
        if (timeEnd.compareTo(timeStart) <= 0) {
            throw new IllegalArgumentException("Time end must be later than time start");
        }
        Optional<Long> availabilityIdOpt = findId(weekDay, timeStart, timeEnd);
        final Number availabilityId;
        if (availabilityIdOpt.isPresent()) {
            availabilityId = availabilityIdOpt.get();
        } else {
            availabilityId = jdbcAvailabilityInsert.executeAndReturnKey(Map.of(
                    "week_day", weekDay,
                    "t_start", timeStart,
                    "t_end", timeEnd
            ));
        }
        try {
            jdbcVWZInsert.execute(Map.of(
                    "vehicle_id", vehicleId,
                    "availability_id", availabilityId,
                    "zone_id", zoneId
            ));
            return Optional.of(new WeeklyAvailability(
                    weekDay,
                    timeStart,
                    timeEnd,
                    zoneId,
                    vehicleId
            ));
        } catch (DuplicateKeyException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<WeeklyAvailability> getDriverWeeklyAvailability(long driverId) {
        return jdbcTemplate.query("""
                        select id, week_day, t_start, t_end, zone_id, vehicle_id
                        from vehicle_weekly_zone vwz
                        join weekly_availability wa on vwz.availability_id = wa.id
                        where exists(select *
                                        from vehicle
                                where id = vwz.vehicle_id
                                and driver_id = ?)""",
                new Object[]{driverId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public List<WeeklyAvailability> getVehicleWeeklyAvailability(long vehicleId) {
        return jdbcTemplate.query("""
                        select id, week_day, t_start, t_end, zone_id, vehicle_id
                        from vehicle_weekly_zone vwz
                        join weekly_availability wa on vwz.availability_id = wa.id
                        where vehicle_id = ?""",
                new Object[]{vehicleId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public List<WeeklyAvailability> getVehicleWeeklyAvailability(long vehicleId, long zoneId) {
        return jdbcTemplate.query("""
                        select id, week_day, t_start, t_end, zone_id, vehicle_id
                        from vehicle_weekly_zone vwz
                        join weekly_availability wa on vwz.availability_id = wa.id
                        where vehicle_id = ? and vwz.zone_id = ?""",
                new Object[]{vehicleId, zoneId},
                new int[]{Types.BIGINT, Types.BIGINT},
                ROW_MAPPER);
    }
}
