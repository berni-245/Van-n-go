package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
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
                (int) rs.getLong("id"),
                rs.getBytes("bin"),
                rs.getString("file_name")
        );
    }

    @Override
    public Image getImage(int id){
        return jdbcTemplate.query("""
                SELECT id, img, file_name
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
    public Image getpfp(int userId) {
        List<Image> images = jdbcTemplate.query("""
                        SELECT i.id, bin, file_name
                        FROM image AS i JOIN app_user AS u ON u.pfp = i.id
                        WHERE u.id = ?""",
                new Object[]{userId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
        if(images.isEmpty())
            return null;
        return images.getFirst();
    }

    @Override
    public Image getVehicleImage(int vehicleId) {
        List<Image> img = jdbcTemplate.query("""
                SELECT id, bin, file_name
                FROM image AS i JOIN vehicle AS v ON v.img_id = i.id 
                WHERE v.id = ?""",
                new Object[]{vehicleId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
        if(img.isEmpty())
            return null;
        return img.getFirst();
    }

    @Override
    public Image getPop(int driverId, int bookingId) {
        List<Image> img;
        img = jdbcTemplate.query("""
                SELECT id, bin, file_name
                FROM image AS i JOIN reservation AS r ON r.proof_of_payment = i.id 
                WHERE r.driver_id = ? AND r.booking_id = ?""",
                new Object[]{driverId, bookingId},
                new int[]{Types.BIGINT,Types.BIGINT},
                ROW_MAPPER);
        if(img.isEmpty())
            return null;
        return img.getFirst();
    }

    @Override
    public int uploadPop(byte[] bin, String fileName, int driverId, int bookingId) {
        Integer key = uploadImage(fileName,bin);
        jdbcTemplate.update("""
                    update reservation set proof_of_payment = ?
                    where driver_id = ? and booking_id = ?""",
                new Object[]{key,driverId,bookingId},
                new int[]{Types.BIGINT,Types.BIGINT,Types.BIGINT});
        return key;
    }

    @Override
    public int uploadVehicleImage(byte[] bin, String fileName, int vehicleId) {
        Integer key = uploadImage(fileName,bin);
        jdbcTemplate.update("""
                    update vehicle set img_id = ?
                    where id = ?""",
                new Object[]{key,vehicleId},
                new int[]{Types.BIGINT,Types.BIGINT});
        return key;
    }

    @Override
    public int uploadPfp(byte[] bin, String fileName, int userId) {
        Integer key = uploadImage(fileName,bin);
        jdbcTemplate.update("""
                    update app_user set pfp = ?
                    where id = ?""",
                new Object[]{key,userId},
                new int[]{Types.BIGINT,Types.BIGINT});
        return key;
    }
}
