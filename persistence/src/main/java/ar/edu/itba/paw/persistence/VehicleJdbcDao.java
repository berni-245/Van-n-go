package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Vehicle;
import org.springframework.dao.DuplicateKeyException;
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
                    rs.getString("description")
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcVehicleInsert;

    public VehicleJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcVehicleInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("id")
                .withTableName("vehicle");
    }

    @Override
    public Vehicle create(long driverId, String plateNumber, double volume, String description) {
        Map<String, Object> vehicleData = Map.of(
                "driver_id", driverId,
                "plate_number", plateNumber.toUpperCase(),
                "volume_m3", volume,
                "description", description
        );
        try {
            final Number generatedId = jdbcVehicleInsert.executeAndReturnKey(vehicleData);
            return new Vehicle(generatedId.longValue(), driverId, plateNumber, volume, description);
        } catch (DuplicateKeyException e) {
            return null;
        }
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
    public Optional<Vehicle> findByPlateNumber(String plateNumber) {
        return jdbcTemplate.query(
                        "SELECT * FROM vehicle where plate_number = ?",
                        new Object[]{plateNumber},
                        new int[]{Types.VARCHAR},
                        ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public List<Vehicle> getDriverVehicles(long driverId) {
        return jdbcTemplate.query(
                "SELECT * FROM vehicle where driver_id = ?",
                new Object[]{driverId},
                new int[]{java.sql.Types.BIGINT},
                ROW_MAPPER);
    }
}
