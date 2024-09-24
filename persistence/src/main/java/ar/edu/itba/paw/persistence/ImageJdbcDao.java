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
import java.util.Optional;

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
                rs.getBytes("img"),
                rs.getString("file_name")
        );
    }

    @Override
    public Image getImage(int id){
        return (Image) jdbcTemplate.query("""
                SELECT id, img, file_name
                FROM image WHERE id = ?""",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public Integer uploadImage(String fileName, byte[] imgData){
        Map<String,Object> toInsert = new HashMap<>();
        toInsert.put("file_name", fileName);
        toInsert.put("img", imgData);
        final Number imgId = jdbcImageInsert.execute(toInsert);
        return imgId.intValue();
    }
}
