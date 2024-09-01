package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {

    private static final RowMapper<User> ROW_MAPPER =
            (rs, rowNum) -> new User(rs.getLong("userid"), rs.getString("username"));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("userid")
                .withTableName("users");
    }

    @Override
    public Optional<User> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM users where userid = ?",
                        new Object[]{id},
                        new int[]{java.sql.Types.BIGINT},
                        ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public User create(String username) {
        Map<String, String> userData = Map.of("username", username);
        final Number generatedId = jdbcInsert.executeAndReturnKey(userData);
        return new User(generatedId.longValue(), username);
    }
}
