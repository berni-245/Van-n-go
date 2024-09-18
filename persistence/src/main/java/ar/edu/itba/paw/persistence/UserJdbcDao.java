package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {
    private static final RowMapper<User> ROW_MAPPER =
            (rs, rowNum) -> new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("mail"),
                    rs.getString("password")
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("id")
                .withTableName("app_user");
    }

    @Override
    public Optional<User> findById(long id) {
        return jdbcTemplate.query(
                        "SELECT * FROM app_user where id = ?",
                        new Object[]{id},
                        new int[]{java.sql.Types.BIGINT},
                        ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public User create(String username, String mail, String password) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("mail", mail);
        userData.put("password", password);
        final Number generatedId = jdbcInsert.executeAndReturnKey(userData);
        return new User(generatedId.longValue(), username, mail, password);
    }

    @Override
    public User findByUsername(String username) {
        return jdbcTemplate.query(
                "SELECT * FROM app_user WHERE username = ?",
                new Object[]{username},
                new int[]{java.sql.Types.VARCHAR},
                ROW_MAPPER
        ).stream().findFirst().orElse(null);
    }

    @Override
    public boolean mailExists(String mail) {
        Integer count = this.jdbcTemplate.queryForObject(
                "SELECT count(*) FROM app_user where mail = ?", Integer.class,
                mail);
        return count != null && count > 0;
    }

    // Es prácticamente igual al de arriba... Se puede modularizar?
    @Override
    public boolean usernameExists(String username) {
        Integer count = this.jdbcTemplate.queryForObject(
                "SELECT count(*) FROM app_user where username = ?", Integer.class,
                username);
        return count != null && count > 0;
    }

    @Override
    public boolean isDriver(String username) {
        return !jdbcTemplate.query("SELECT * FROM app_user as au JOIN driver as d ON au.id=d.user_id WHERE username = ?", new Object[]{username}, new int[]{java.sql.Types.VARCHAR},ROW_MAPPER).isEmpty();
    }
}
