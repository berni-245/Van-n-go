package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Types;
import java.util.HashMap;
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
        toInsert.put("img", imgData);
        final Number imgId = jdbcImageInsert.execute(toInsert);
        return imgId.intValue();
    }

    @Override
    public Image getpfp(int userId) {
        return jdbcTemplate.query("""
                SELECT id, img, file_name
                FROM image AS i JOIN profile_picture AS p ON p.img_id = i.id 
                WHERE p.user_id = ?""",
                new Object[]{userId},
                new int[]{Types.BIGINT},
                ROW_MAPPER).getFirst();
    }

    @Override
    public Image getVehicleImage(int vehicleId) {
        return jdbcTemplate.query("""
                SELECT id, img, file_name
                FROM image AS i JOIN vehicle_picture AS v ON v.img_id = i.id 
                WHERE v.vehicle_id = ?""",
                new Object[]{vehicleId},
                new int[]{Types.BIGINT},
                ROW_MAPPER).getFirst();
    }

    @Override
    public Image getPop(int driverId, int bookingId) {
        return jdbcTemplate.query("""
                SELECT id, img, file_name
                FROM image AS i JOIN proof_of_payment AS p ON p.img_id = i.id 
                WHERE p.driver_id = ? AND p.booking_id = ?""",
                new Object[]{driverId, bookingId},
                new int[]{Types.BIGINT,Types.BIGINT},
                ROW_MAPPER).getFirst();
    }

    @Override
    public int uploadPfp(MultipartFile file, int userId) throws IOException {
        Integer key = uploadImage(file.getOriginalFilename(),file.getBytes());
        Map<String,Object> toInsert = new HashMap<>();
        toInsert.put("user_id", userId);
        toInsert.put("img_id", key);
        return jdbcPfpInsert.execute(toInsert);
    }

    @Override
    public int uploadVehicleImage(MultipartFile file, int vehicleId) throws IOException {
        Integer key = uploadImage(file.getOriginalFilename(),file.getBytes());
        Map<String,Object> toInsert = new HashMap<>();
        toInsert.put("vehicle_id", vehicleId);
        toInsert.put("img_id", key);
        return jdbcVehicleInsert.execute(toInsert);
    }

    @Override
    public int uploadPop(MultipartFile file, int driverId, int bookingId) throws IOException {
        Integer key = uploadImage(file.getOriginalFilename(),file.getBytes());
        Map<String,Object> toInsert = new HashMap<>();
        toInsert.put("driver_id", driverId);
        toInsert.put("booking_id", bookingId);
        toInsert.put("img_id", key);
        return jdbcPopInsert.execute(toInsert);
    }
}
