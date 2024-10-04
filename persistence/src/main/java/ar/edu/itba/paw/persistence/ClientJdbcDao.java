package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;
import java.util.Optional;

@Repository
public class ClientJdbcDao implements ClientDao {

    private static final RowMapper<Client> ROW_MAPPER =
            (rs, rowNum) -> new Client(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("mail"),
                    rs.getString("password"),
                    rs.getInt("pfp")
            );

    protected final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcClientInsert;

    public ClientJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcClientInsert = new SimpleJdbcInsert(ds).withTableName("client");
    }

    @Override
    public Client create(long id, String username, String mail, String password) {
        jdbcClientInsert.execute(Map.of("user_id", id));
        return new Client(id, username, mail, password,0);
    }

    @Override
    public Optional<Client> findById(long id) {
        return jdbcTemplate.query(
                        "SELECT * FROM client join app_user on client.user_id = app_user.id where id = ?",
                        new Object[]{id},
                        new int[]{Types.BIGINT},
                        ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public Optional<Client> findByUsername(String username) {
        return jdbcTemplate.query(
                        "SELECT * FROM client join app_user on client.user_id = app_user.id where username = ?",
                        new Object[]{username},
                        new int[]{Types.VARCHAR},
                        ROW_MAPPER)
                .stream().findFirst();
    }
}