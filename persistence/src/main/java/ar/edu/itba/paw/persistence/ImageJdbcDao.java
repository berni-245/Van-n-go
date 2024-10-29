package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ImageJdbcDao implements ImageDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcImageInsert;
    private final RowMapper<Image> ROW_MAPPER;

    public ImageJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcImageInsert = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("image");
        ROW_MAPPER = (rs, rowNum) -> new Image(
                rs.getLong("id"),
                rs.getBytes("bin"),
                rs.getString("file_name")
        );
    }

    @Override
    public Image getImage(long id){
        return jdbcTemplate.query("""
                SELECT id, bin, file_name
                FROM image WHERE id = ?""",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER).getFirst();
    }

    @Override
    public Integer uploadImage(String fileName, byte[] imgData){
        Map<String,Object> toInsert = new HashMap<>();
        toInsert.put("file_name", fileName);
        toInsert.put("bin", imgData);
        final Number imgId = jdbcImageInsert.executeAndReturnKey(toInsert);
        return imgId.intValue();
    }

    @Override
    public long uploadPop(byte[] bin, String fileName, long bookingId) {
        Integer key = uploadImage(fileName,bin);
        return jdbcTemplate.update("""
                update booking
                set proof_of_payment = ?
                where id = ?""",
                new Object[]{key, bookingId},
                new int[]{Types.BIGINT, Types.BIGINT});
    }

    @Override
    public long uploadVehicleImage(byte[] bin, String fileName, long vehicleId) {
        Integer key = uploadImage(fileName,bin);
        jdbcTemplate.update("""
                    update vehicle set img_id = ?
                    where id = ?""",
                new Object[]{key,vehicleId},
                new int[]{Types.BIGINT,Types.BIGINT});
        return key;
    }

    @Override
    public long uploadPfp(byte[] bin, String fileName, long userId) {
        Integer key = uploadImage(fileName,bin);
        jdbcTemplate.update("""
                    update app_user set pfp = ?
                    where id = ?""",
                new Object[]{key,userId},
                new int[]{Types.BIGINT,Types.BIGINT});
        return key;
    }
}
