package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;
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
                    rs.getString("extra1"),
                    rs.getObject("rating", Double.class)
            );

    protected final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcDriverInsert;

    public DriverJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcDriverInsert = new SimpleJdbcInsert(ds).withTableName("driver");
    }

    @Override
    public Driver create(long id, String username, String mail, String password, String extra1) {
        jdbcDriverInsert.execute(Map.of("user_id", id, "extra1", extra1));
        return new Driver(id, username, mail, password, extra1,null);
    }

    @Override
    public Optional<Driver> findById(long id) {
        return jdbcTemplate.query("""
                                SELECT * FROM driver join app_user
                                on driver.user_id = app_user.id where id = ?""",
                        new Object[]{id},
                        new int[]{Types.BIGINT},
                        ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public List<Driver> getAll(Long zoneId, Size size) {
        return jdbcTemplate.query("""
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

    @Override
    public Optional<Driver> findByUsername(String username) {
        return jdbcTemplate.query(
                        "SELECT * FROM driver join app_user on driver.user_id = app_user.id where username = ?",
                        new Object[]{username},
                        new int[]{Types.VARCHAR},
                        ROW_MAPPER)
                .stream().findFirst();
    }


    @Override
    public void updateDriverRating(long driverId) {
        jdbcTemplate.update("""
                            update driver
                            set rating = (
                                select avg(rating)
                                from booking b join vehicle v on b.vehicle_id = v.id
                                where v.driver_id = ?
                            )
                            where user_id = ?
                """, new Object[]{driverId, driverId}, new int[]{Types.BIGINT, Types.BIGINT});
    }
}
