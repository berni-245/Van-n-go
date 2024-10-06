package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {
    protected final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    private final DriverDao driverDao;

    @Autowired
    private final ClientDao clientDao;

    public UserJdbcDao(final DataSource ds, DriverDao driverDao, ClientDao clientDao) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("id")
                .withTableName("app_user");
        this.driverDao = driverDao;
        this.clientDao = clientDao;
    }

    @Override
    public long create(String username, String mail, String password) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("mail", mail);
        userData.put("password", password);
        return jdbcInsert.executeAndReturnKey(userData).longValue();
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
                username
        );
        return count != null && count > 0;
    }

    @Override
    public Optional<? extends User> findByUsername(String username) {
        Optional<? extends User> user = driverDao.findByUsername(username);
        if (user.isPresent()) return user;
        user = clientDao.findByUsername(username);
        return user;
    }

    @Override
    public int updateMail(long userId, String updatedMail) {
        return jdbcTemplate.update("""
                update app_user
                set mail = ?
                where id = ?
                """, new Object[]{updatedMail, userId}, new int[]{Types.VARCHAR, Types.BIGINT}) ;
    }

    @Override
    public int updatePassword(long userId, String updatedPassword){
        return jdbcTemplate.update("""
                update app_user
                set password = ?
                where id = ?
                """, new Object[]{updatedPassword, userId}, new int[]{Types.VARCHAR, Types.BIGINT}) ;
    }

    @Override
    public int updateUsername(long userId, String updatedUsername) {
        return jdbcTemplate.update("""
                update app_user
                set username = ?
                where id = ?
                """, new Object[]{updatedUsername, userId}, new int[]{Types.VARCHAR, Types.BIGINT}) ;
    }
}
