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
    private final SimpleJdbcInsert jdbcPfpInsert;
    private final SimpleJdbcInsert jdbcPopInsert;
    private final SimpleJdbcInsert jdbcVehicleInsert;
    private final RowMapper<Image> ROW_MAPPER;

    public ImageJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcImageInsert = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("image");
        jdbcPfpInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("profile_picture");
        jdbcPopInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("proof_of_payment");
        jdbcVehicleInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("vehicle_picture");
        ROW_MAPPER = (rs, rowNum) -> new Image(
                (int) rs.getLong("id"),
                rs.getBytes("img"),
                rs.getString("file_name")
        );
    }

    @Override
    public Image getImage(int id) {
        return jdbcTemplate.query("""
                        SELECT id, img, file_name
                        FROM image WHERE id = ?""",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER).getFirst();
    }

    @Override
    public Integer uploadImage(String fileName, byte[] imgData) {
        Map<String, Object> toInsert = new HashMap<>();
        toInsert.put("file_name", fileName);
        toInsert.put("img", imgData);
        final Number imgId = jdbcImageInsert.execute(toInsert);
        return imgId.intValue();
    }

    @Override
    public Image getpfp(int userId) {
        List<Image> images = jdbcTemplate.query("""
                        SELECT id, img, file_name
                        FROM image AS i JOIN profile_picture AS p ON p.img_id = i.id 
                        WHERE p.user_id = ?""",
                new Object[]{userId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
        if (images.isEmpty())
            return null;
        return images.getFirst();
    }

    @Override
    public Image getVehicleImage(int vehicleId) {
        List<Image> img = jdbcTemplate.query("""
                        SELECT id, img, file_name
                        FROM image AS i JOIN vehicle_picture AS v ON v.img_id = i.id 
                        WHERE v.vehicle_id = ?""",
                new Object[]{vehicleId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
        if (img.isEmpty())
            return null;
        return img.getFirst();
    }

    @Override
    public Image getPop(int driverId, int bookingId) {
        List<Image> img;
        img = jdbcTemplate.query("""
                        SELECT id, img, file_name
                        FROM image AS i JOIN proof_of_payment AS p ON p.img_id = i.id 
                        WHERE p.driver_id = ? AND p.booking_id = ?""",
                new Object[]{driverId, bookingId},
                new int[]{Types.BIGINT, Types.BIGINT},
                ROW_MAPPER);
        if (img.isEmpty())
            return null;
        return img.getFirst();
    }

    @Override
    public int uploadPop(byte[] bin, String fileName, int driverId, int bookingId) {
        Integer key = uploadImage(fileName, bin);
        Map<String, Object> toInsert = new HashMap<>();
        toInsert.put("driver_id", driverId);
        toInsert.put("booking_id", bookingId);
        toInsert.put("img_id", key);
        return jdbcPopInsert.execute(toInsert);
    }

    @Override
    public int uploadVehicleImage(byte[] bin, String fileName, int vehicleId) {
        Integer key = uploadImage(fileName, bin);
        Map<String, Object> toInsert = new HashMap<>();
        toInsert.put("vehicle_id", vehicleId);
        toInsert.put("img_id", key);
        return jdbcVehicleInsert.execute(toInsert);
    }

    @Override
    public int uploadPfp(byte[] bin, String fileName, int userId) {
        Integer key = uploadImage(fileName, bin);
        Map<String, Object> toInsert = new HashMap<>();
        toInsert.put("user_id", userId);
        toInsert.put("img_id", key);
        return jdbcPfpInsert.execute(toInsert);
    }
}
