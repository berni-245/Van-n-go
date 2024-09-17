package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

@Repository
public class ClientJdbcDao implements ClientDao{

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcClientInsert;

    private static final RowMapper<Client> ROW_MAPPER =
            (rs, rowNum) -> new Client(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("mail"),
                    rs.getString("password")
            );


    public ClientJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcClientInsert = new SimpleJdbcInsert(ds).withTableName("client");
    }

    @Override
    public Client create(User user) {
        jdbcClientInsert.execute(Map.of("user_id", user.getId()));
        return new Client(user);
    }

    @Override
    public Optional<Client> findById(long id) {
        return jdbcTemplate.query(
                        "SELECT * FROM client join app_user on client.user_id = app_user.id where id = ?",
                        new Object[]{id},
                        new int[]{java.sql.Types.BIGINT},
                        ROW_MAPPER)
                .stream().findFirst();
    }
}