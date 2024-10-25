package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Zone;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
/*
@Repository
public class ZoneJdbcDao implements ZoneDao {

    private static final RowMapper<Zone> ROW_MAPPER =
            (rs, rowNum) -> new Zone(
                    rs.getLong("id"),
                    rs.getLong("country_id"),
                    rs.getString("country_code"),
                    rs.getString("country_name"),
                    rs.getLong("province_id"),
                    rs.getString("province_name"),
                    rs.getLong("neighborhood_id"),
                    rs.getString("neighborhood_name")
            );

    private final JdbcTemplate jdbcTemplate;

    public ZoneJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<Zone> getZone(long id) {
        return jdbcTemplate.query("""
                        SELECT z.id,
                               country_id, c.code as country_code, c.name as country_name,
                               province_id, p.name as province_name,
                               neighborhood_id, n.name as neighborhood_name
                        FROM zone z
                        join country c on z.country_id = c.id
                        join province p on z.province_id = p.id
                        join neighborhood n on z.neighborhood_id = n.id
                        where z.id = ?""",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER).stream().findFirst();
    }

    @Override
    public boolean isValidZone(long zoneId) {
        Integer count = this.jdbcTemplate.queryForObject(
                "SELECT count(*) FROM zone where id = ?", Integer.class,
                zoneId);
        return count != null && count > 0;
    }

    // What's the best way to reuse this sql statements?
    @Override
    public List<Zone> getAllZones() {
        return jdbcTemplate.query("""
                        SELECT z.id,
                               country_id, c.code as country_code, c.name as country_name,
                               province_id, p.name as province_name,
                               neighborhood_id, n.name as neighborhood_name
                        FROM zone z
                        join country c on z.country_id = c.id
                        join province p on z.province_id = p.id
                        join neighborhood n on z.neighborhood_id = n.id""",
                ROW_MAPPER);
    }
}
*/