package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.User;
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
public class DriverJdbcDao implements DriverDao {

    private static final RowMapper<Driver> ROW_MAPPER =
            (rs, rowNum) -> new Driver(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("mail"),
                    rs.getString("password"),
                    rs.getString("extra1")
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcUserInsert;
    private final SimpleJdbcInsert jdbcDriverInsert;
    private final VehicleDao vehicleDao;

    public DriverJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcUserInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("id")
                .withTableName("app_user");
        jdbcDriverInsert = new SimpleJdbcInsert(ds).withTableName("driver");
        vehicleDao = new VehicleJdbcDao(ds);
    }

    @Override
    public Driver create(User user, String extra1) {
        jdbcDriverInsert.execute(Map.of("user_id", user.getId(), "extra1", extra1));
        return new Driver(user, extra1);
    }

    @Override
    public Optional<Driver> findById(long id) {
        return jdbcTemplate.query(
                        "SELECT * FROM driver join app_user on driver.user_id = app_user.id where id = ?",
                        new Object[]{id},
                        new int[]{java.sql.Types.BIGINT},
                        ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public List<Driver> getAll(Long zoneId, Size size) {
        List<Driver> drivers = jdbcTemplate.query("""
                        select * from driver d join app_user on d.user_id = app_user.id
                        where exists (select * from vehicle_weekly_zone vwz
                            where vwz.zone_id = ? and exists (
                                select * from vehicle v
                                where v.driver_id = d.user_id and v.id = vwz.vehicle_id
                                and v.volume_m3 between ? and ?
                            )
                        )
                        """,
                new Object[]{zoneId, size.getMinVolume(), size.getMaxVolume()},
                new int[]{Types.BIGINT, Types.INTEGER, Types.INTEGER},
                ROW_MAPPER
        );
        for (Driver driver : drivers) {
            driver.setVehicles(vehicleDao.getDriverVehicles(driver.getId(), zoneId, size));
        }
        return drivers;
    }

    @Override
    public List<Driver> getAll(long zoneId) {
        return List.of();
    }

    // We should probably add some kind of pagination to this.
    @Override
    public List<Driver> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM driver join app_user on driver.user_id = app_user.id",
                ROW_MAPPER
        );
    }
}
