package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao{

    private static final RowMapper<User> ROW_MAPPER =
            (rs, rowNum) -> new User(rs.getLong("userId"), rs.getString("username"));

    private final JdbcTemplate jdbcTemplate;

    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<User> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM users where userId = ?",
                new Object[]{id},
                new int[]{java.sql.Types.BIGINT},
                ROW_MAPPER)
                .stream().findFirst();
    }
}
