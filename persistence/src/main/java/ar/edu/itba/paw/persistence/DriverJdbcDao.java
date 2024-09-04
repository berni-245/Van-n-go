package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
                    rs.getString("extra1")
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcUserInsert;
    private final SimpleJdbcInsert jdbcDriverInsert;

    public DriverJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcUserInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("id")
                .withTableName("app_user");
        jdbcDriverInsert = new SimpleJdbcInsert(ds).withTableName("driver");
    }

    @Override
    public Driver create(String username, String mail, String extra1) {
        Map<String, String> userData = Map.of("username", username, "mail", mail);
        final Number userId = jdbcUserInsert.executeAndReturnKey(userData);
        jdbcDriverInsert.execute(Map.of("id", userId, "extra1", extra1));
        return new Driver(userId.longValue(), username, mail, extra1);
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

    // We should probably add some kind of pagination to this.
    @Override
    public List<Driver> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM driver join app_user on driver.user_id = app_user.id",
                ROW_MAPPER
        );
    }
}
