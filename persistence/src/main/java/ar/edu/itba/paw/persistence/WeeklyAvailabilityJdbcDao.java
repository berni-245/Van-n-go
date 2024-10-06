package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.HourInterval;
import ar.edu.itba.paw.models.WeeklyAvailability;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.*;

@Repository
public class WeeklyAvailabilityJdbcDao implements WeeklyAvailabilityDao {
    private static final int MIN_DAY = 1;
    private static final int MAX_DAY = 7;
    private static final RowMapper<WeeklyAvailability> ROW_MAPPER =
            (rs, rowNum) -> new WeeklyAvailability(
                    rs.getInt("week_day"),
                    new HourInterval(rs.getString("t_start"), rs.getString("t_end")),
                    rs.getLong("zone_id"),
                    rs.getLong("vehicle_id")
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcAvailabilityInsert;

    public WeeklyAvailabilityJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcAvailabilityInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("id")
                .withTableName("weekly_availability");
    }



    private Integer findHourBlockId(String hourStart) {
        return jdbcTemplate.queryForObject(
                """
                        select id
                        from hour_block
                        where t_start = ?
                        """,
                new Object[]{hourStart},
                new int[]{Types.TIME},
                ((rs, rowNum) -> rs.getInt("id"))
        );
    }


    @Override
    public boolean create(
            int weekDay, List<String> hours, long zoneId, long vehicleId
    ) {

        int changedRows = 0;
        for (String hour : hours) {
            Map<String, Object> availability = new HashMap<>();
            availability.put("week_day", weekDay);
            availability.put("hour_block_id", findHourBlockId(hour));
            availability.put("zone_id", zoneId);
            availability.put("vehicle_id", vehicleId);
            changedRows += jdbcAvailabilityInsert.execute(availability);
        }
        return changedRows == hours.size();

    }

    @Override
    public List<WeeklyAvailability> getDriverWeeklyAvailability(long driverId) {
        return jdbcTemplate.query("""
                        select week_day, t_start, t_end, zone_id, vehicle_id
                        from hour_block hb
                        join weekly_availability wa on hb.id = wa.hour_block_id
                        join vehicle vh on vh.id = wa.vehicle_id
                        where vh.driver_id = ?""",
                new Object[]{driverId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public List<List<WeeklyAvailability>> getDriverWeeklyAvailabilityByDays(long driverId) {
        List<List<WeeklyAvailability>> availabilityByDays = new ArrayList<>();
        for (int day = MIN_DAY; day <= MAX_DAY; day++) {
            List<WeeklyAvailability> dayAvailability = jdbcTemplate.query("""
                    select week_day, t_start, t_end, zone_id, vehicle_id
                    from hour_block hb
                    join weekly_availability wa on hb.id = wa.hour_block_id
                    join vehicle vh on vh.id = wa.vehicle_id
                    where vh.driver_id = ? and week_day = ?
                    """, new Object[]{driverId, day}, new int[]{Types.BIGINT, Types.INTEGER}, ROW_MAPPER);
            availabilityByDays.add(dayAvailability);
        }
        return availabilityByDays;
    }

    @Override
    public List<WeeklyAvailability> getVehicleWeeklyAvailability(long vehicleId) {
        return jdbcTemplate.query("""
                        select week_day, t_start, t_end, zone_id, vehicle_id
                        from hour_block hb
                        join weekly_availability wa on hb.id = wa.hour_block_id
                        where vehicle_id = ?""",
                new Object[]{vehicleId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public List<WeeklyAvailability> getVehicleWeeklyAvailability(long vehicleId, long zoneId) {
        return jdbcTemplate.query("""
                        select week_day, t_start, t_end, zone_id, vehicle_id
                        from weekly_availability as wa join
                        hour_block as hb on hb.id = wa.hour_block_id
                        where vehicle_id = ? and zone_id = ?""",
                new Object[]{vehicleId, zoneId},
                new int[]{Types.BIGINT, Types.BIGINT},
                ROW_MAPPER);
    }
}
