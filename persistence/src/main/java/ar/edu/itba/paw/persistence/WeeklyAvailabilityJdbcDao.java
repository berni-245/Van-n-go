package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.HourInterval;
import ar.edu.itba.paw.models.WeeklyAvailability;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

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
    public int create(
            int weekDay, String[] hourBlocks, long zoneId, long vehicleId
    ) {
        int changedRows = 0;
        for (String hour : hourBlocks) {
            changedRows += jdbcTemplate.update("""
                            insert into weekly_availability (
                                week_day, hour_block_id, zone_id, vehicle_id
                            ) VALUES (?, ?, ?, ?) on conflict do nothing
                            """,
                    new Object[]{weekDay, findHourBlockId(hour), zoneId, vehicleId},
                    new int[]{Types.INTEGER, Types.BIGINT, Types.BIGINT, Types.BIGINT}
            );
        }
        return changedRows;
    }

    @Override
    public void removeAll(int weekDay, long zoneId, long vehicleId) {
        jdbcTemplate.update("""
                        delete from weekly_availability
                        where week_day = ? and zone_id = ? and vehicle_id = ?""",
                weekDay, zoneId, vehicleId);
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
