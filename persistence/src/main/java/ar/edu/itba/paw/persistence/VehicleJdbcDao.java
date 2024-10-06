package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.Vehicle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class VehicleJdbcDao implements VehicleDao {

    private static final RowMapper<Vehicle> ROW_MAPPER =
            (rs, rowNum) -> new Vehicle(
                    rs.getLong("id"),
                    rs.getLong("driver_id"),
                    rs.getString("plate_number"),
                    rs.getDouble("volume_m3"),
                    rs.getString("description"),
                    rs.getInt("img_id"),
                    rs.getDouble("hourly_rate")
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcVehicleInsert;
    private final WeeklyAvailabilityDao weeklyAvailabilityDao;

    public VehicleJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcVehicleInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("id")
                .withTableName("vehicle");
        weeklyAvailabilityDao = new WeeklyAvailabilityJdbcDao(ds);
    }

    @Override
    public Vehicle create(long driverId, String plateNumber, double volume, String description, double rate) {
        Map<String, Object> vehicleData = Map.of(
                "driver_id", driverId,
                "plate_number", plateNumber.toUpperCase(),
                "volume_m3", volume,
                "description", description,
                "hourly_rate", rate
        );
        final Number generatedId = jdbcVehicleInsert.executeAndReturnKey(vehicleData);
        return new Vehicle(generatedId.longValue(), driverId, plateNumber, volume, description, null, rate);
    }

    @Override
    public Optional<Vehicle> findById(long id) {
        return jdbcTemplate.query(
                        "SELECT * FROM vehicle where id = ?",
                        new Object[]{id},
                        new int[]{Types.BIGINT},
                        ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public Optional<Vehicle> findByPlateNumber(long driverId, String plateNumber) {
        return jdbcTemplate.query(
                        "SELECT * FROM vehicle where driver_id = ? and plate_number = ?",
                        new Object[]{driverId, plateNumber},
                        new int[]{Types.BIGINT, Types.VARCHAR},
                        ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public List<Vehicle> getDriverVehicles(long driverId) {
        return jdbcTemplate.query(
                "select * from vehicle where driver_id = ? order by plate_number",
                new Object[]{driverId},
                new int[]{java.sql.Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public List<Vehicle> getDriverVehiclesFull(long driverId) {
        List<Vehicle> vehicles = jdbcTemplate.query(
                "select * from vehicle where driver_id = ? order by plate_number",
                new Object[]{driverId},
                new int[]{java.sql.Types.BIGINT},
                ROW_MAPPER);

        for (Vehicle vehicle : vehicles) {
            vehicle.setWeeklyAvailability(
                    weeklyAvailabilityDao.getVehicleWeeklyAvailability(vehicle.getId())
            );
        }
        return vehicles;
    }

    @Override
    public List<Vehicle> getDriverVehicles(long driverId, long zoneId, Size size) {
        List<Vehicle> vehicles = jdbcTemplate.query("""
                        select * from vehicle v
                        where driver_id = ? and volume_m3 between ? and ? and exists(
                            select * from weekly_availability wa
                            where wa.vehicle_id = v.id and wa.zone_id = ?
                        )""",
                new Object[]{driverId, size.getMinVolume(), size.getMaxVolume(), zoneId},
                new int[]{Types.BIGINT, Types.INTEGER, Types.INTEGER, Types.BIGINT},
                ROW_MAPPER
        );
        for (Vehicle vehicle : vehicles) {
            vehicle.setWeeklyAvailability(
                    weeklyAvailabilityDao.getVehicleWeeklyAvailability(vehicle.getId(), zoneId)
            );
        }
        return vehicles;
    }

    @Override
    public boolean plateNumberExists(String plateNumber) {
        Integer count = this.jdbcTemplate.queryForObject(
                "SELECT count(*) FROM vehicle where plate_number = ?", Integer.class,
                plateNumber
        );
        return count != null && count > 0;
    }

    @Override
    public boolean updateVehicle(long driverId, Vehicle v) {
        return jdbcTemplate.update("""
                        UPDATE vehicle
                        SET plate_number = ?, volume_m3 = ?, description = ?, hourly_rate = ?
                        WHERE driver_id = ? and id = ?""",
                new Object[]{v.getPlateNumber(), v.getVolume(), v.getDescription(), v.getRate(), driverId, v.getId()},
                new int[]{Types.VARCHAR, Types.DOUBLE, Types.VARCHAR, Types.DOUBLE, Types.BIGINT, Types.BIGINT}
        ) > 0;
    }
}
